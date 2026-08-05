#!/usr/bin/env bash
#
# Arranca l'aplicació PINBAL (docker compose/podman-compose) contra una base
# de dades EFÍMERA — Oracle Database Free (per defecte, contenidor "oracle"
# definit a docker-compose.e2e-oracle.yml) o H2 en memòria (--db=h2,
# contenidor "h2" definit a docker-compose.e2e.yml), sempre afegit com a
# overlay sobre docker-compose.yml —, el servidor fake de SCSP
# (pinbal-scsp-fake) i executa la suite Playwright de e2e/. Keycloak segueix
# sent l'extern configurat a .env (AUTH_URL/AUTH_REALM/...); no es toca.
#
# L'esquema i les dades de proves es creen automàticament en arrencar JBoss:
# Liquibase està desactivat per defecte (pinbal-ejb/application.properties,
# perquè un JBoss normal/de producció no l'ha d'executar mai), però aquests
# dos overlays el reactiven explícitament (SPRING_LIQUIBASE_ENABLED=true).
# CADA EXECUCIÓ PARTEIX D'UN ESTAT NET tan bon punt es recrea el contenidor
# de BD (h2 o oracle: cap dels dos overlays fa servir volums persistents,
# vegeu docker/oracle/README.md pel motiu concret d'Oracle). Si es reutilitza
# el mateix contenidor en marxa entre execucions (comportament per defecte,
# vegeu --down), les dades es mantenen; Liquibase ja és idempotent (salta els
# canvis ja aplicats).
#
# Ús:
#   scripts/e2e/run-e2e.sh [opcions] [-- <arguments per a playwright>]
#
# Opcions:
#   --db=oracle|h2          Motor de BD a fer servir (per defecte: oracle).
#                            La primera vegada amb Oracle cal descarregar una
#                            imatge d'uns 5 GB (pública, no cal cap "docker
#                            login"); un cop en caché local, arrenca en
#                            segons, igual que H2. Vegeu docker/oracle/README.md.
#   --down                  Atura (compose down) l'aplicació en acabar.
#                            Per defecte es deixa la pila engegada perquè
#                            l'arrencada de JBoss és lenta i es puguin tornar
#                            a executar els tests sense esperar de nou (però
#                            l'estat de la BD només es conserva mentre el
#                            contenidor de BD segueixi en marxa: cap dels dos
#                            overlays fa servir volums persistents).
#   --skip-compose          No aixequi l'aplicació (assumeix que ja està en marxa).
#   --skip-fake-build       No recompili pinbal-scsp-fake (usa el jar existent).
#   --skip-fake             No arrenqui el fake SCSP (assumeix que ja està en marxa).
#   --headed                Executa Playwright en mode "headed".
#   --ui                    Executa Playwright en mode interactiu (--ui).
#   --stop                  Atura tot el que pugui estar en marxa d'una
#                            execució anterior (fake SCSP + aplicació PINBAL +
#                            BD, tant l'overlay Oracle com l'H2, per si no se
#                            sap amb quin es va arrencar) i surt. No aixeca
#                            res ni executa cap test. Útil sobretot perquè,
#                            per defecte, l'script deixa la pila engegada en
#                            acabar (vegeu --down més amunt).
#   -h, --help              Mostra aquesta ajuda.
#
# Variables d'entorn opcionals:
#   FAKE_SCSP_HOST (per defecte 127.0.0.1) — només per a la comprovació de
#                   salut feta DES DEL HOST (wait_for_http). NO és la interfície
#                   on escolta el procés Java: l'aplicació, dins del contenidor,
#                   hi arriba via host.docker.internal (FAKE_SCSP_BASE_URL),
#                   que amb el backend "netavark" de podman NO travessa cap a
#                   un servei escoltant només a 127.0.0.1 del host (confirmat
#                   empíricament: connexió refusada). Per això el fake SCSP
#                   escolta sempre a FAKE_SCSP_BIND_HOST (per defecte 0.0.0.0,
#                   totes les interfícies), no a FAKE_SCSP_HOST.
#   FAKE_SCSP_BIND_HOST (per defecte 0.0.0.0)
#   FAKE_SCSP_PORT (per defecte 18080)
#   E2E_BASE_URL   (per defecte http://localhost:8080/pinbalback)
#   APP_READY_TIMEOUT_SECONDS (per defecte 600; considereu augmentar-lo si el
#                              pull de la imatge d'Oracle (--db=oracle, la
#                              primera vegada) triga més que això)
#
# Credencials i càrrega de dades:
#   Les credencials de pinbal-back/.../e2e/.env.e2e (E2E_ADMIN_USERNAME,
#   E2E_DELEGAT_USERNAME, E2E_REPRESENTANT_USERNAME, E2E_AUDITOR_USERNAME;
#   usuaris reals del Keycloak compartit) es passen a l'aplicació com a
#   paràmetres de Liquibase perquè la càrrega de dades de e2e
#   (pinbal-persistence/.../db/changelog/e2e/) creï els usuaris/entitats de
#   proves corresponents a la BD efímera (H2 o Oracle, indistintament).
#   Aquesta mateixa càrrega ja redirigeix els serveis SCSP coberts pel fake
#   cap a FAKE_SCSP_BASE_URL, de manera que ja NO cal cap pas manual per
#   apuntar-los-hi (l'antiga opció --point-scsp-urls/scripts/scsp-fake/
#   point-to-fake.sql només té sentit contra un Oracle real NO gestionat per
#   aquest script, fora d'aquest flux; vegeu FAKE_SCSP_SERVER.md).
#
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
REACT_DIR="$ROOT_DIR/pinbal-back/src/main/reactapp/pinbal-back"
FAKE_SCSP_DIR="$ROOT_DIR/pinbal-scsp-fake"
RUN_DIR="$ROOT_DIR/.e2e-run"
FAKE_SCSP_LOG="$RUN_DIR/fake-scsp.log"
FAKE_SCSP_PID_FILE="$RUN_DIR/fake-scsp.pid"

FAKE_SCSP_HOST="${FAKE_SCSP_HOST:-127.0.0.1}"
FAKE_SCSP_BIND_HOST="${FAKE_SCSP_BIND_HOST:-0.0.0.0}"
FAKE_SCSP_PORT="${FAKE_SCSP_PORT:-18080}"
APP_BASE_URL="${E2E_BASE_URL:-http://localhost:8080/pinbalback}"
APP_READY_TIMEOUT_SECONDS="${APP_READY_TIMEOUT_SECONDS:-600}"
FAKE_READY_TIMEOUT_SECONDS="${FAKE_READY_TIMEOUT_SECONDS:-60}"

DB_ENGINE="oracle"
DOWN_AFTER=false
SKIP_COMPOSE=false
SKIP_FAKE_BUILD=false
SKIP_FAKE=false
STOP_MODE=false
PLAYWRIGHT_MODE="test:e2e"
PLAYWRIGHT_EXTRA_ARGS=()

log()  { echo "[e2e] $*"; }
warn() { echo "[e2e][avís] $*" >&2; }
err()  { echo "[e2e][error] $*" >&2; }

while [[ $# -gt 0 ]]; do
    case "$1" in
        --db=*) DB_ENGINE="${1#--db=}"; shift ;;
        --db) DB_ENGINE="$2"; shift 2 ;;
        --down) DOWN_AFTER=true; shift ;;
        --skip-compose) SKIP_COMPOSE=true; shift ;;
        --skip-fake-build) SKIP_FAKE_BUILD=true; shift ;;
        --skip-fake) SKIP_FAKE=true; shift ;;
        --headed) PLAYWRIGHT_MODE="test:e2e:headed"; shift ;;
        --ui) PLAYWRIGHT_MODE="test:e2e:ui"; shift ;;
        --stop) STOP_MODE=true; shift ;;
        -h|--help) sed -n '2,/^set -euo pipefail/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'; exit 0 ;;
        --) shift; PLAYWRIGHT_EXTRA_ARGS=("$@"); break ;;
        *) err "Opció desconeguda: $1"; exit 1 ;;
    esac
done

case "$DB_ENGINE" in
    oracle|h2) ;;
    *) err "Motor de BD desconegut: '$DB_ENGINE' (valors vàlids: oracle, h2)"; exit 1 ;;
esac

mkdir -p "$RUN_DIR"

# --- Detecció de docker compose / podman-compose ---------------------------
# Comprovar només que la CLI existeixi no basta: en entorns on l'usuari no
# pertany al grup "docker" (soquet /var/run/docker.sock inaccessible), `docker
# compose version` respon igualment (no toca el dimoni) però qualsevol ordre
# real ("up", "down"...) fallaria per permisos. Per això es comprova
# connectivitat real amb `docker info`.
COMPOSE_CMD=()
if command -v docker >/dev/null 2>&1 && docker info >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
    COMPOSE_CMD=(docker compose)
elif command -v docker-compose >/dev/null 2>&1 && docker info >/dev/null 2>&1; then
    COMPOSE_CMD=(docker-compose)
elif command -v podman-compose >/dev/null 2>&1; then
    COMPOSE_CMD=(podman-compose)
else
    err "No s'ha trobat 'docker compose', 'docker-compose' ni 'podman-compose' amb accés al dimoni al PATH."
    exit 1
fi
log "Motor de compose: ${COMPOSE_CMD[*]}"
log "Motor de BD: $DB_ENGINE"

# --- --stop: atura tot el que pugui estar en marxa d'una execució anterior -
# No se sap amb quin --db es va arrencar l'última vegada (no es persisteix
# enlloc), així que s'ataquen els dos overlays (oracle i h2): "compose down"
# sobre un projecte/contenidors que ja no existeixen no falla, només avisa.
if [[ "$STOP_MODE" == true ]]; then
    log "--stop: aturant tot el que pugui estar en marxa..."

    if [[ -f "$FAKE_SCSP_PID_FILE" ]]; then
        stop_pid="$(cat "$FAKE_SCSP_PID_FILE" 2>/dev/null || true)"
        if [[ -n "$stop_pid" ]] && kill -0 "$stop_pid" 2>/dev/null; then
            log "Aturant el fake SCSP (pid $stop_pid, de $FAKE_SCSP_PID_FILE)..."
            kill "$stop_pid" 2>/dev/null || true
            wait "$stop_pid" 2>/dev/null || true
        else
            log "El pid de $FAKE_SCSP_PID_FILE ja no correspon a cap procés en marxa."
        fi
        rm -f "$FAKE_SCSP_PID_FILE"
    fi
    # Sempre es comprova també qui escolta realment al port, tant si hi havia
    # fitxer de pid com si no: un pid "orfe" (procés mort però amb un altre de
    # NOU escoltant al mateix port, arrencat a mà o per una execució anterior
    # que no va netejar bé) no es detecta només mirant el fitxer.
    if command -v lsof >/dev/null 2>&1; then
        stop_pid="$(lsof -t -i "tcp:$FAKE_SCSP_PORT" -sTCP:LISTEN 2>/dev/null || true)"
        if [[ -n "$stop_pid" ]]; then
            log "Trobat un procés escoltant al port $FAKE_SCSP_PORT (pid $stop_pid); aturant-lo."
            kill $stop_pid 2>/dev/null || true
        else
            log "Ningú escolta ja al port $FAKE_SCSP_PORT."
        fi
    else
        warn "'lsof' no disponible: no es pot comprovar si queda algun procés orfe al port $FAKE_SCSP_PORT."
    fi

    log "Aturant l'aplicació i la BD (overlay Oracle)..."
    (cd "$ROOT_DIR" && "${COMPOSE_CMD[@]}" -f "$ROOT_DIR/docker-compose.yml" -f "$ROOT_DIR/docker-compose.e2e-oracle.yml" down) \
        || warn "compose down (oracle) ha fallat o no hi havia res a aturar."
    log "Aturant l'aplicació i la BD (overlay H2)..."
    (cd "$ROOT_DIR" && "${COMPOSE_CMD[@]}" -f "$ROOT_DIR/docker-compose.yml" -f "$ROOT_DIR/docker-compose.e2e.yml" down) \
        || warn "compose down (h2) ha fallat o no hi havia res a aturar."

    log "Fet."
    exit 0
fi

# S'afegeix sempre un overlay que substitueix la BD Oracle externa de .env
# per un contenidor efímer (Oracle Free o H2, segons --db) i injecta els
# paràmetres/context de Liquibase de e2e (vegeu docker-compose.e2e-oracle.yml
# / docker-compose.e2e.yml). No modifica docker-compose.yml.
if [[ "$DB_ENGINE" == "oracle" ]]; then
    COMPOSE_FILE_ARGS=(-f "$ROOT_DIR/docker-compose.yml" -f "$ROOT_DIR/docker-compose.e2e-oracle.yml")
else
    COMPOSE_FILE_ARGS=(-f "$ROOT_DIR/docker-compose.yml" -f "$ROOT_DIR/docker-compose.e2e.yml")
fi

if [[ "$SKIP_COMPOSE" == false ]]; then
    if [[ ! -f "$ROOT_DIR/.env" ]]; then
        err "No existeix $ROOT_DIR/.env. Cal crear-lo abans (vegeu docker-compose.yml i README)."
        exit 1
    fi
fi

FAKE_SCSP_PID=""

cleanup() {
    local exit_code=$?
    if [[ -n "$FAKE_SCSP_PID" ]] && kill -0 "$FAKE_SCSP_PID" 2>/dev/null; then
        log "Aturant el fake SCSP (pid $FAKE_SCSP_PID)..."
        kill "$FAKE_SCSP_PID" 2>/dev/null || true
        wait "$FAKE_SCSP_PID" 2>/dev/null || true
    fi
    if [[ "$DOWN_AFTER" == true && "$SKIP_COMPOSE" == false ]]; then
        log "Aturant l'aplicació (compose down)..."
        (cd "$ROOT_DIR" && "${COMPOSE_CMD[@]}" "${COMPOSE_FILE_ARGS[@]}" down) || warn "compose down ha fallat."
    fi
    exit "$exit_code"
}
trap cleanup EXIT INT TERM

wait_for_http() {
    local url="$1" timeout_seconds="$2" description="$3"
    local waited=0
    log "Esperant que $description respongui a $url (timeout ${timeout_seconds}s)..."
    until curl --silent --fail --max-time 5 --output /dev/null "$url"; do
        if (( waited >= timeout_seconds )); then
            err "$description no ha respost dins de ${timeout_seconds}s."
            return 1
        fi
        sleep 5
        waited=$((waited + 5))
    done
    log "$description operatiu (${waited}s)."
}

# --- 1. Fake SCSP server -----------------------------------------------------
if [[ "$SKIP_FAKE" == false ]]; then
    if [[ "$SKIP_FAKE_BUILD" == false ]]; then
        log "Compilant pinbal-scsp-fake..."
        # pinbal-scsp-fake no és un mòdul del reactor principal (no apareix al
        # <modules> del pom.xml arrel), cal compilar-lo des del seu directori.
        (cd "$FAKE_SCSP_DIR" && "$ROOT_DIR/mvnw" -q package -DskipTests)
    fi
    FAKE_JAR=$(ls -t "$FAKE_SCSP_DIR"/target/pinbal-scsp-fake-*.jar 2>/dev/null | grep -v sources | head -n1 || true)
    if [[ -z "$FAKE_JAR" ]]; then
        err "No s'ha trobat cap jar a $FAKE_SCSP_DIR/target. Executeu sense --skip-fake-build."
        exit 1
    fi
    log "Arrencant fake SCSP ($FAKE_JAR) a $FAKE_SCSP_BIND_HOST:$FAKE_SCSP_PORT (comprovat des del host via $FAKE_SCSP_HOST)..."
    # Escolta a FAKE_SCSP_BIND_HOST (0.0.0.0 per defecte), NO a FAKE_SCSP_HOST:
    # amb el backend "netavark" de podman (i el bridge estàndard de Docker), un
    # servei que només escolta a 127.0.0.1 del host és inabastable des de dins
    # d'un contenidor via host.docker.internal (confirmat empíricament) -- la
    # comprovació de salut de sota, feta des del host mateix, sí que funciona
    # igual amb qualsevol dels dos, per això no cal canviar-la.
    nohup java \
        -Dfake.scsp.host="$FAKE_SCSP_BIND_HOST" \
        -Dfake.scsp.port="$FAKE_SCSP_PORT" \
        -jar "$FAKE_JAR" > "$FAKE_SCSP_LOG" 2>&1 &
    FAKE_SCSP_PID=$!
    echo "$FAKE_SCSP_PID" > "$FAKE_SCSP_PID_FILE"
    wait_for_http "http://$FAKE_SCSP_HOST:$FAKE_SCSP_PORT/__fake-scsp" "$FAKE_READY_TIMEOUT_SECONDS" "el fake SCSP"
else
    log "Es salta l'arrencada del fake SCSP (--skip-fake)."
fi

# --- 2. Credencials i configuració per a la càrrega de dades e2e -----------
# Les credencials de e2e/.env.e2e es reenvien com a paràmetres de Liquibase
# (SPRING_LIQUIBASE_PARAMETERS_* a docker-compose.e2e.yml) perquè la càrrega
# de dades de e2e (db/changelog/e2e/) creï els usuaris/entitats de proves
# corresponents al Keycloak compartit, i redirigeixi ella mateixa els
# serveis SCSP coberts pel fake cap a FAKE_SCSP_BASE_URL.
if [[ ! -f "$REACT_DIR/e2e/.env.e2e" ]]; then
    warn "No existeix e2e/.env.e2e; es copia des de l'exemple. Ompliu les credencials abans de tornar a executar."
    cp "$REACT_DIR/e2e/.env.e2e.example" "$REACT_DIR/e2e/.env.e2e"
fi
# shellcheck disable=SC1091
set -a; source "$REACT_DIR/e2e/.env.e2e"; set +a
export E2E_ADMIN_USERNAME="${E2E_ADMIN_USERNAME:-}"
export E2E_DELEGAT_USERNAME="${E2E_DELEGAT_USERNAME:-}"
export E2E_REPRESENTANT_USERNAME="${E2E_REPRESENTANT_USERNAME:-}"
export E2E_AUDITOR_USERNAME="${E2E_AUDITOR_USERNAME:-}"
# Usuaris fixos (no lligats a Keycloak) usats per alguns tests de llistat/
# filtre/permisos; mateixos valors per defecte que e2e/utils/env.ts perquè
# el codi a la BD i el que cerquen els tests coincideixin sempre.
export E2E_USER_ACTIU_USERNAME="${E2E_USER_ACTIU_USERNAME:-E2E_USER_ACTIU}"
export E2E_USER_INACTIU_USERNAME="${E2E_USER_INACTIU_USERNAME:-E2E_USER_INACTIU}"
export E2E_USER_ALL_ROLES="${E2E_USER_ALL_ROLES:-pbl_all}"
export FAKE_SCSP_BASE_URL="http://host.docker.internal:${FAKE_SCSP_PORT}"

# --- 3. Aplicació PINBAL (docker/podman compose + overlay H2/Oracle) -------
if [[ "$SKIP_COMPOSE" == false ]]; then
    log "Aixecant l'aplicació amb ${COMPOSE_CMD[*]} ${COMPOSE_FILE_ARGS[*]}..."
    # --build assegura que la imatge local de l'overlay H2 (docker/h2/Dockerfile)
    # es reconstrueix si el Dockerfile ha canviat; en cas contrari el motor de
    # compose pot reutilitzar en silenci una imatge ja etiquetada i desactualitzada.
    # (L'overlay Oracle no en té, cap "build:"; --build no hi té cap efecte.)
    if [[ "$DB_ENGINE" == "oracle" ]]; then
        log "Amb --db=oracle, si encara no teniu la imatge en caché local (~5 GB), aquest pas pot trigar una estona (vegeu docker/oracle/README.md)."
    fi
    (cd "$ROOT_DIR" && "${COMPOSE_CMD[@]}" "${COMPOSE_FILE_ARGS[@]}" up -d --build)
    wait_for_http "$APP_BASE_URL" "$APP_READY_TIMEOUT_SECONDS" "l'aplicació PINBAL"
else
    log "Es salta l'arrencada de l'aplicació (--skip-compose)."
fi

# --- 4. Playwright -----------------------------------------------------------
if [[ ! -d "$REACT_DIR/node_modules" ]]; then
    log "Instal·lant dependències npm a $REACT_DIR..."
    (cd "$REACT_DIR" && npm ci)
fi
if [[ ! -d "$HOME/.cache/ms-playwright" ]]; then
    log "Instal·lant navegadors de Playwright (chromium)..."
    (cd "$REACT_DIR" && npx playwright install --with-deps chromium)
fi

log "Executant els tests Playwright (npm run $PLAYWRIGHT_MODE)..."
set +e
(cd "$REACT_DIR" && npm run "$PLAYWRIGHT_MODE" -- "${PLAYWRIGHT_EXTRA_ARGS[@]}")
PLAYWRIGHT_EXIT_CODE=$?
set -e

if [[ $PLAYWRIGHT_EXIT_CODE -ne 0 ]]; then
    err "Playwright ha finalitzat amb errors (codi $PLAYWRIGHT_EXIT_CODE). Informe: $REACT_DIR/playwright-report/index.html"
else
    log "Tests Playwright completats correctament."
fi

exit $PLAYWRIGHT_EXIT_CODE
