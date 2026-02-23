# Com activar els Mocks SCSP en Docker

Els mocks SCSP estan preparats però necessiten configuració adicional per funcionar en aquest projecte Java EE.

## El Problema

Aquest és un projecte **Java EE (JBoss/WildFly)**, no Spring Boot. Això significa que:
- La variable d'entorn `SPRING_PROFILES_ACTIVE` **NO s'aplica automàticament**
- Spring necessita que els profiles s'activin via **system properties de la JVM**

## Solució 1: Variable JVM al Dockerfile (Recomanada)

Modifica el `Dockerfile` per afegir el profile com a system property:

```dockerfile
# Al final del Dockerfile, abans de CMD
ENV JAVA_OPTS="-Dspring.profiles.active=mock-scsp-db"
```

O si ja existeix `JAVA_OPTS`, afegeix-ho:

```dockerfile
ENV JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=mock-scsp-db"
```

Després rebuilda la imatge:

```bash
docker build -t pinbal:mock .
```

I executa:

```bash
docker run -it --rm \
  --name pinbal \
  -p 8180:8080 \
  -p 8887:8787 \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  pinbal:mock
```

## Solució 2: System Property via Docker

Si no vols modificar el Dockerfile, passa la propietat via `JAVA_OPTS`:

```bash
docker run -it --rm \
  --name pinbal \
  -p 8180:8080 \
  -p 8887:8787 \
  -e JAVA_OPTS="-Dspring.profiles.active=mock-scsp-db" \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  pinbal:latest
```

**Nota**: Això sobreescriurà completament `JAVA_OPTS`. Si necessites mantenir altres opcions:

```bash
docker run -it --rm \
  --name pinbal \
  -p 8180:8080 \
  -p 8887:8787 \
  -e JAVA_OPTS="-Xms1024m -Xmx2048m -Dspring.profiles.active=mock-scsp-db" \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  pinbal:latest
```

## Solució 3: Modificar web.xml

Afegeix al `web.xml` de cada mòdul web (pinbal-webapp, pinbal-api-*):

```xml
<context-param>
    <param-name>spring.profiles.active</param-name>
    <param-value>mock-scsp-db</param-value>
</context-param>
```

**Inconvenient**: Això activarà SEMPRE el mock, fins i tot en producció.

## Solució 4: Script d'arrencada personalitzat

Crea un script `docker-entrypoint-mock.sh`:

```bash
#!/bin/bash
export JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-mock-scsp-db}"
exec /opt/jboss/tools/docker-entrypoint.sh "$@"
```

I al `docker run`:

```bash
docker run -it --rm \
  --name pinbal \
  -p 8180:8080 \
  -p 8887:8787 \
  -e SPRING_PROFILES_ACTIVE=mock-scsp-db \
  -v $(pwd)/docker-entrypoint-mock.sh:/docker-entrypoint-mock.sh:ro \
  --entrypoint /docker-entrypoint-mock.sh \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  pinbal:latest
```

## Verificar que el Mock està actiu

Un cop arrencat el contenidor, busca als logs:

```bash
docker logs pinbal 2>&1 | grep -i "MOCK SCSP"
```

Hauries de veure:

```
🔧 [MOCK SCSP DB] Generant ID Petició: MOCK1737391234567
🔧 [MOCK SCSP DB] Enviant petició SÍNCRONA amb persistència
```

També pots verificar que Spring ha carregat el bean:

```bash
docker logs pinbal 2>&1 | grep -i "clienteUnicoMock"
```

## Perfils disponibles

- **`mock-scsp`**: Mock simple sense persistència a BBDD
- **`mock-scsp-db`**: Mock amb persistència completa a BBDD (RECOMANAT)

## Troubleshooting

### El mock no s'activa

1. Verifica que `JAVA_OPTS` conté el profile:
   ```bash
   docker exec pinbal printenv JAVA_OPTS
   ```

2. Verifica els logs de Spring:
   ```bash
   docker logs pinbal 2>&1 | grep -i "profile\|bean.*clienteUnico"
   ```

3. Comprova que els fitxers de configuració existeixen:
   ```bash
   docker exec pinbal ls -la /opt/jboss/wildfly/standalone/deployments/*.ear/lib/pinbal-scsp*.jar
   ```

### Encara utilitza el ClienteUnico real

Si veus als logs:

```
at es.scsp.client.ClienteUnico.realizaPeticionSincrona(ClienteUnico.java:158)
```

Significa que el profile NO està actiu. Revisa els passos anteriors.

### Error: FileNotFoundException application-context-scsp-mock.xml

Assegura't que has recompilar el projecte després d'afegir el fitxer:

```bash
mvn clean package
docker build -t pinbal:latest .
```

## Configuració recomanada per desenvolupament

La millor opció és crear un `Dockerfile.dev`:

```dockerfile
FROM pinbal:base

# Activar mock amb BBDD per defecte en desenvolupament
ENV JAVA_OPTS="${JAVA_OPTS} -Dspring.profiles.active=mock-scsp-db"

# Opcional: Mode debug
ENV JAVA_OPTS="${JAVA_OPTS} -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8787"

CMD ["/opt/jboss/tools/docker-entrypoint.sh"]
```

Build i run:

```bash
docker build -f Dockerfile.dev -t pinbal:dev .

docker run -it --rm \
  --name pinbal-dev \
  -p 8180:8080 \
  -p 8887:8787 \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  pinbal:dev
```

---

**Data**: Gener 2026
**Autor**: Equip desenvolupament Pinbal
