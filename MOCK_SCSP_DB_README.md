# Mock SCSP amb Persistència a Base de Dades

## Descripció

Aquest mock avançat simula el comportament complet del `ClienteUnico` de SCSP però **guarda tota la informació a les taules de la base de dades** igual que ho fa el client real.

### Diferències entre els dos mocks

| Característica | `mock-scsp` | `mock-scsp-db` |
|----------------|-------------|----------------|
| **Simula peticions** | ✅ Sí | ✅ Sí |
| **Guarda a BBDD** | ❌ No | ✅ Sí |
| **Permet recuperar consultes** | ❌ No | ✅ Sí |
| **Visualització a l'aplicació** | ❌ Limitada | ✅ Completa |
| **Taules utilitzades** | Cap | core_peticion_respuesta<br>core_transmision<br>core_token |
| **Ús recomanat** | Proves ràpides | Desenvolupament complet |

## Taules de BBDD utilitzades

### 1. `core_peticion_respuesta`
Emmagatzema la petició i el seu estat:
- `id_peticion`: ID únic de la petició
- `servicio_id`: Servei SCSP consultat
- `estado`: Estat de la petició (0000 = OK)
- `fecha_peticion`: Data d'enviament
- `fecha_respuesta`: Data de resposta
- `ter`: Temps estimat de resposta
- `numero_transmisiones`: Número de consultes

### 2. `core_transmision`
Emmagatzema cada consulta individual:
- `id_solicitud`: ID de la sol·licitud
- `id_transmision`: ID de la transmissió
- `peticion_id`: Referència a la petició
- `id_solicitante`, `nombre_solicitante`: Dades del solicitant
- `doc_titular`, `nombre_titular`: Dades del titular
- `doc_funcionario`, `nombre_funcionario`: Dades del funcionari
- `codigo_procedimiento`, `unidad_tramitadora`: Context administratiu
- `xml_transmision`: XML amb la resposta completa
- `estado`: Estat de la transmissió

### 3. `core_token`
Emmagatzema l'XML de la petició original:
- `peticion_id`: Referència a la petició
- `tipo_mensaje`: Tipus (1=Petició, 2=Resposta)
- `datos`: XML serialitzat de la petició

## Com utilitzar-lo

### Opció 1: Docker (Recomanada per desenvolupament)

```bash
docker run -it --rm \
  --name "${CONTAINER_NAME}" \
  -p 8180:8080 \
  -p 8887:8787 \
  -e SPRING_PROFILES_ACTIVE=mock-scsp-db \
  --add-host afirmades.caib.es:10.215.9.239 \
  --add-host proves.caib.es:10.215.29.224 \
  --add-host=host.docker.internal:host-gateway \
  "${IMAGE_NAME}"
```

### Opció 2: Maven

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mock-scsp-db
```

### Opció 3: Variable d'entorn

```bash
export SPRING_PROFILES_ACTIVE=mock-scsp-db
mvn spring-boot:run
```

### Opció 4: IntelliJ IDEA

1. **Run > Edit Configurations**
2. Seleccionar la configuració d'execució
3. A **Active profiles**, afegir: `mock-scsp-db`
4. Aplicar i executar

## Verificació

### 1. Als logs

Cerca missatges com:

```
🔧 [MOCK SCSP DB] ============================================
🔧 [MOCK SCSP DB] Enviant petició SÍNCRONA amb persistència
🔧 [MOCK SCSP DB] ID Petició: MOCK1737391234567
🔧 [MOCK SCSP DB] ✅ Petició síncrona guardada a BBDD
🔧 [MOCK SCSP DB] PeticionRespuesta guardada: MOCK1737391234567
🔧 [MOCK SCSP DB] Transmision guardada: MOCK173739123456700001
```

### 2. A la base de dades

Consulta directa a BBDD:

```sql
-- Veure les peticions mock
SELECT id_peticion, estado, fecha_peticion, numero_transmisiones
FROM core_peticion_respuesta
WHERE id_peticion LIKE 'MOCK%'
ORDER BY fecha_peticion DESC;

-- Veure les transmissions mock
SELECT t.id_solicitud, t.id_transmision, t.estado, t.nombre_solicitante
FROM core_transmision t
JOIN core_peticion_respuesta pr ON t.peticion_id = pr.id_peticion
WHERE pr.id_peticion LIKE 'MOCK%'
ORDER BY t.fecha_generacion DESC;

-- Veure el token (XML petició)
SELECT peticion_id, tipo_mensaje, LENGTH(datos) as xml_length
FROM core_token
WHERE peticion_id LIKE 'MOCK%';
```

### 3. A l'aplicació web

Ara pots:
- ✅ Llistar les consultes realitzades
- ✅ Veure el detall de cada consulta
- ✅ Recuperar les respostes
- ✅ Generar justificants
- ✅ Visualitzar històrics

## Flux complet simulat

### Petició Síncrona

```
1. Aplicació crida: scspHelper.enviarPeticionSincrona()
   ↓
2. Mock intercepta i:
   - Crea registre a core_peticion_respuesta (estat=0000, fecha_respuesta=NOW)
   - Crea registres a core_transmision (un per cada sol·licitud)
   - Guarda XML petició a core_token
   - Genera XML resposta mock
   ↓
3. Retorna ResultatEnviamentPeticio amb idsSolicituds[]
   ↓
4. Aplicació pot recuperar la informació de BBDD immediatament
```

### Petició Asíncrona

```
1. Aplicació crida: scspHelper.enviarPeticionAsincrona()
   ↓
2. Mock intercepta i:
   - Crea registre a core_peticion_respuesta (estat=0002, fecha_respuesta=NULL)
   - Crea registres a core_transmision
   - Guarda XML petició a core_token
   ↓
3. Retorna ConfirmacionPeticion (estat=0000)
   ↓
4. Aplicació crida: scspHelper.recuperarRespuesta()
   ↓
5. Mock:
   - Detecta que encara no hi ha resposta (fecha_respuesta=NULL)
   - Simula el processament
   - Actualitza estat=0000, fecha_respuesta=NOW
   - Genera XMLs resposta
   ↓
6. Construeix Respuesta des de BBDD i la retorna
```

## Estructura de dades generada

### Exemple de registre a `core_peticion_respuesta`

```
id_peticion: MOCK1737391234567
servicio_id: 123
estado: 0000
fecha_peticion: 2026-01-20 10:30:00
fecha_respuesta: 2026-01-20 10:30:01
ter: 2026-01-21 10:30:00
error: Mock: Consulta processada correctament
numero_envios: 1
numero_transmisiones: 1
transmision_sincrona: 1
```

### Exemple de registre a `core_transmision`

```
id_solicitud: MOCK173739123456700001
id_transmision: TRANS1737391234567001
peticion_id: MOCK1737391234567
id_solicitante: B07167448
nombre_solicitante: Entitat de proves
doc_titular: 12345678Z
nombre_completo_titular: Nom Cognoms
doc_funcionario: 00000000T
nombre_funcionario: Funcionari Test
codigo_procedimiento: PROC-TEST
unidad_tramitadora: Unitat de Test
finalidad: Proves de desenvolupament
consentimiento: Si
estado: 0000
xml_transmision: <TransmisionDatos>...</TransmisionDatos>
```

## Avantatges d'aquest mock

### ✅ Per a desenvolupadors

- Desenvolupament complet sense certificats
- Proves de tot el flux end-to-end
- Debug fàcil consultant la BBDD
- No cal VPN ni accés a serveis externs

### ✅ Per a l'aplicació

- Funcionalitat completa de visualització
- Històric de consultes disponible
- Generació de justificants
- Tests d'integració complets

### ✅ Per a QA

- Proves repetibles
- Escenaris controlats
- No depèn de disponibilitat de serveis externs
- Dades consistents

## Limitacions

⚠️ **Aquest mock NO fa**:
- Validació real de certificats
- Comunicació amb serveis SCSP reals
- Validació d'esquemes XSD reals (opcional)
- Resposta amb dades reals de registres

⚠️ **Les dades generades són mock**:
- Els XMLs de resposta són plantilles genèriques
- Les dades específiques són mínimes
- No hi ha validació de camps obligatoris SCSP

## Personalització

### Modificar les respostes mock

Edita `ClienteUnicoMockPersistent.java`:

```java
private String generarXmlMockResposta(String idSolicitud) {
    // Personalitza el XML segons el servei
    if (idSolicitud.contains("ECOT")) {
        return generarRespostaEmpadronament();
    } else if (idSolicitud.contains("SVDD")) {
        return generarRespostaRegistreCivil();
    }
    // ... resposta per defecte
}
```

### Simular errors

```java
private void guardarPeticionRespuesta(Peticion peticion, boolean sincrona) {
    // ...

    // Simular error de forma aleatòria (10% de vegades)
    if (Math.random() < 0.1) {
        pr.setEstado("9999");
        pr.setError("Mock: Error simulat per proves");
    } else {
        pr.setEstado("0000");
        pr.setError("Mock: Consulta processada correctament");
    }

    // ...
}
```

### Afegir latències variables

```java
private void simularLatencia(String codigoCertificado) {
    // Latència segons el servei
    int latencia = 500; // per defecte

    if (codigoCertificado.startsWith("ECOT")) {
        latencia = 2000; // Empadronament més lent
    } else if (codigoCertificado.startsWith("SVDD")) {
        latencia = 1000; // Registre civil mitjà
    }

    try {
        Thread.sleep(latencia);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

## Comparació amb el client real

| Operació | Client Real | Mock Persistent | Temps |
|----------|-------------|-----------------|-------|
| getIDPeticion | Crida SCSP | Genera local | ~0ms vs ~200ms |
| realizaPeticionSincrona | Envia HTTPS + espera | Guarda BBDD | ~800ms vs ~5000ms |
| realizaPeticionAsincrona | Envia HTTPS | Guarda BBDD | ~500ms vs ~2000ms |
| recuperaRespuesta | Crida SCSP | Consulta BBDD | ~100ms vs ~3000ms |
| generaJustificante | Crida SCSP | PDF mock | ~200ms vs ~2000ms |

## Troubleshooting

### El mock no guarda a BBDD

1. Verificar que les taules existeixen:
   ```sql
   SELECT * FROM information_schema.tables
   WHERE table_name IN ('core_peticion_respuesta', 'core_transmision', 'core_token');
   ```

2. Comprovar permisos de BBDD

3. Revisar logs d'Hibernate:
   ```
   logging.level.org.hibernate.SQL=DEBUG
   ```

### Error: "No session found"

Assegurar que la configuració d'Hibernate permet sessions per thread:
```properties
spring.jpa.properties.hibernate.current_session_context_class=thread
```

### Les consultes no es veuen a l'aplicació

1. Verificar que el servei existeix a la taula `core_servicio`
2. Comprovar que `servicio_id` és correcte a `core_peticion_respuesta`
3. Fer flush de la cache de l'aplicació

### Els XMLs estan buits

Els XMLs són generats de forma simplificada. Per XMLs més reals, personalitza els mètodes:
- `generarXmlMockPeticion()`
- `generarXmlMockResposta()`

## Migració entre mocks

### De mock-scsp a mock-scsp-db

Simplement canvia el perfil - no cal més:

```bash
# Abans
-e SPRING_PROFILES_ACTIVE=mock-scsp

# Després
-e SPRING_PROFILES_ACTIVE=mock-scsp-db
```

### De mock-scsp-db a real

1. Assegurar certificats configurats
2. Configurar serveis SCSP reals
3. Eliminar el perfil mock:

```bash
# Eliminar -e SPRING_PROFILES_ACTIVE=mock-scsp-db
```

## Scripts útils

### Netejar consultes mock

```sql
-- Eliminar totes les dades mock (PRECAUCIÓ!)
DELETE FROM core_token WHERE peticion_id LIKE 'MOCK%';
DELETE FROM core_transmision WHERE id_solicitud LIKE 'MOCK%';
DELETE FROM core_peticion_respuesta WHERE id_peticion LIKE 'MOCK%';
```

### Estadístiques de consultes mock

```sql
SELECT
    DATE(fecha_peticion) as dia,
    COUNT(*) as num_consultes,
    SUM(numero_transmisiones) as num_transmissions,
    AVG(TIMESTAMPDIFF(SECOND, fecha_peticion, fecha_respuesta)) as temps_mig_segons
FROM core_peticion_respuesta
WHERE id_peticion LIKE 'MOCK%'
GROUP BY DATE(fecha_peticion)
ORDER BY dia DESC;
```

---

**Data de creació**: Gener 2026
**Versió**: 1.0
**Perfil**: mock-scsp-db
**Mantenidor**: Equip de desenvolupament Pinbal
