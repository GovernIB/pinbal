#!/usr/bin/env bash
#
# Arranca l'aplicació PINBAL (docker compose/podman-compose) contra una base
# de dades H2 EFÍMERA (en memòria, servida per un contenidor auxiliar "h2"
# definit a docker-compose.e2e.yml, sempre afegit com a overlay sobre
# docker-compose.yml), el servidor fake de SCSP (pinbal-scsp-fake) i executa
# la suite Playwright de e2e/. Keycloak segueix sent l'extern configurat a
# .env (AUTH_URL/AUTH_REALM/...); no es toca.
#
# L'esquema i les dades de proves es creen automàticament en arrencar JBoss
# (Liquibase, spring.liquibase.enabled=true). Com que la BD és en memòria,
# CADA EXECUCIÓ PARTEIX D'UN ESTAT NET tan bon punt es recrea el contenidor
# h2 (p. ex. amb --down seguit d'una nova execució, o en aturar-lo a mà); si
# es reutilitza el mateix contenidor h2 en marxa entre execucions (comportament
# per defecte, vegeu --down), les dades es mantenen.
#
# Ús:
#   scripts/e2e/run-e2e.sh [opcions] [-- <arguments per a playwright>]
#
# Opcions:
#   --down                  Atura (compose down) l'aplicació en acabar.
#                            Per defecte es deixa la pila engegada perquè
#                            l'arrencada de JBoss és lenta i es puguin tornar
#                            a executar els tests sense esperar de nou (però
#                            l'estat de la BD H2 només es conserva mentre el
#                            contenidor h2 segueixi en marxa).
#   --skip-compose          No aixequi l'aplicació (assumeix que ja està en marxa).
#   --skip-fake-build       No recompili pinbal-scsp-fake (usa el jar existent).
#   --skip-fake             No arrenqui el fake SCSP (assumeix que ja està en marxa).
#   --headed                Executa Playwright en mode "headed".
#   --ui                    Executa Playwright en mode interactiu (--ui).
#   -h, --help              Mostra aquesta ajuda.
#
# Variables d'entorn opcionals:
#   FAKE_SCSP_HOST (per defecte 127.0.0.1)
#   FAKE_SCSP_PORT (per defecte 18080)
#   E2E_BASE_URL   (per defecte http://localhost:8080/pinbalback)
#   APP_READY_TIMEOUT_SECONDS (per defecte 600)
#
# Credencials i càrrega de dades:
#   Les credencials de pinbal-back/.../e2e/.env.e2e (E2E_ADMIN_USERNAME,
#   E2E_DELEGAT_USERNAME, E2E_REPRESENTANT_USERNAME, E2E_AUDITOR_USERNAME;
#   usuaris reals del Keycloak compartit) es passen a l'aplicació com a
#   paràmetres de Liquibase perquè la càrrega de dades de e2e
#   (pinbal-persistence/.../db/changelog/e2e/) creï els usuaris/entitats de
#   proves corresponents a la BD H2. Aquesta mateixa càrrega ja redirigeix
#   els serveis SCSP coberts pel fake cap a FAKE_SCSP_BASE_URL, de manera que
#   ja NO cal cap pas manual per apuntar-los-hi (l'antiga opció
#   --point-scsp-urls/scripts/scsp-fake/point-to-fake.sql només té sentit
#   contra un Oracle real, fora d'aquest flux; vegeu FAKE_SCSP_SERVER.md).
#
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
REACT_DIR="$ROOT_DIR/pinbal-back/src/main/reactapp/pinbal-back"
FAKE_SCSP_DIR="$ROOT_DIR/pinbal-scsp-fake"
RUN_DIR="$ROOT_DIR/.e2e-run"
FAKE_SCSP_LOG="$RUN_DIR/fake-scsp.log"
FAKE_SCSP_PID_FILE="$RUN_DIR/fake-scsp.pid"

FAKE_SCSP_HOST="${FAKE_SCSP_HOST:-127.0.0.1}"
FAKE_SCSP_PORT="${FAKE_SCSP_PORT:-18080}"
APP_BASE_URL="${E2E_BASE_URL:-http://localhost:8080/pinbalback}"
APP_READY_TIMEOUT_SECONDS="${APP_READY_TIMEOUT_SECONDS:-600}"
FAKE_READY_TIMEOUT_SECONDS="${FAKE_READY_TIMEOUT_SECONDS:-60}"

DOWN_AFTER=false
SKIP_COMPOSE=false
SKIP_FAKE_BUILD=false
SKIP_FAKE=false
PLAYWRIGHT_MODE="test:e2e"
PLAYWRIGHT_EXTRA_ARGS=()

log()  { echo "[e2e] $*"; }
warn() { echo "[e2e][avís] $*" >&2; }
err()  { echo "[e2e][error] $*" >&2; }

while [[ $# -gt 0 ]]; do
    case "$1" in
        --down) DOWN_AFTER=true; shift ;;
        --skip-compose) SKIP_COMPOSE=true; shift ;;
        --skip-fake-build) SKIP_FAKE_BUILD=true; shift ;;
        --skip-fake) SKIP_FAKE=true; shift ;;
        --headed) PLAYWRIGHT_MODE="test:e2e:headed"; shift ;;
        --ui) PLAYWRIGHT_MODE="test:e2e:ui"; shift ;;
        -h|--help) sed -n '2,/^set -euo pipefail/p' "$0" | sed '$d' | sed 's/^# \{0,1\}//'; exit 0 ;;
        --) shift; PLAYWRIGHT_EXTRA_ARGS=("$@"); break ;;
        *) err "Opció desconeguda: $1"; exit 1 ;;
    esac
done

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

# S'afegeix sempre l'overlay docker-compose.e2e.yml, que substitueix la BD
# Oracle externa per un H2 efímer i injecta els paràmetres de Liquibase de
# e2e (vegeu docker-compose.e2e.yml). No modifica docker-compose.yml.
COMPOSE_FILE_ARGS=(-f "$ROOT_DIR/docker-compose.yml" -f "$ROOT_DIR/docker-compose.e2e.yml")

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
    log "Arrencant fake SCSP ($FAKE_JAR) a $FAKE_SCSP_HOST:$FAKE_SCSP_PORT..."
    nohup java \
        -Dfake.scsp.host="$FAKE_SCSP_HOST" \
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

# --- 3. Aplicació PINBAL (docker/podman compose + overlay H2) --------------
if [[ "$SKIP_COMPOSE" == false ]]; then
    log "Aixecant l'aplicació amb ${COMPOSE_CMD[*]} ${COMPOSE_FILE_ARGS[*]}..."
    # --build assegura que la imatge local de l'overlay (h2, docker/h2/Dockerfile)
    # es reconstrueix si el Dockerfile ha canviat; en cas contrari el motor de
    # compose pot reutilitzar en silenci una imatge ja etiquetada i desactualitzada.
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
