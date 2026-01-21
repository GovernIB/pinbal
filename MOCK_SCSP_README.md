# Mock SCSP per Desenvolupament

## Descripció

Aquest mock permet desenvolupar i provar l'aplicació Pinbal sense necessitat de:
- Certificats digitals reals
- Connexió als serveis SCSP de producció
- Accessos VPN o xarxes restringides

El mock intercepta totes les crides a `ClienteUnico` i retorna respostes simulades que imiten el comportament dels serveis SCSP reals.

## Característiques

✅ **Peticions Síncrones**: Simula l'enviament i recepció immediata de peticions
✅ **Peticions Asíncrones**: Simula confirmació de petició amb estat OK
✅ **Generació de justificants**: Retorna un PDF mock bàsic
✅ **Recuperació de respostes**: Genera respostes amb estructura completa
✅ **Logging detallat**: Mostra clarament quan s'executen operacions mock

## Com utilitzar-lo

### Opció 1: Maven

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mock-scsp
```

### Opció 2: Variable d'entorn

```bash
export SPRING_PROFILES_ACTIVE=mock-scsp
mvn spring-boot:run
```

### Opció 3: IntelliJ IDEA

1. Anar a **Run > Edit Configurations**
2. Seleccionar la configuració d'execució
3. A **Active profiles**, afegir: `mock-scsp`
4. Aplicar i executar

### Opció 4: Docker

```bash
docker run -e SPRING_PROFILES_ACTIVE=mock-scsp pinbal:latest
```

## Verificació

Quan el mock estigui actiu, veuràs missatges com aquests als logs:

```
🔧 [MOCK SCSP] ============================================
🔧 [MOCK SCSP] Enviant petició SÍNCRONA
🔧 [MOCK SCSP] ID Petició: MOCK1234567890
🔧 [MOCK SCSP] Servei: SVDXXXXX
🔧 [MOCK SCSP] Número de sol·licituds: 1
🔧 [MOCK SCSP] ============================================
🔧 [MOCK SCSP] ✅ Petició síncrona processada correctament
```

## Fitxers implicats

```
pinbal-scsp/src/main/java/es/caib/pinbal/scsp/mock/
└── ClienteUnicoMock.java          # Classe principal del mock

pinbal-scsp/src/main/resources/
└── application-mock-scsp.properties  # Configuració del perfil
```

## Comportament del Mock

### Generació d'ID de Petició
- Format: `MOCK{timestamp}`
- Exemple: `MOCK1737391234567`

### Estats de resposta
- **Síncrones**: Sempre retorna estat `0000` (OK)
- **Asíncrones**: Sempre retorna confirmació amb estat `0000`
- **Recuperació**: Genera respostes amb transmissions mock completes

### Latència simulada
- **Peticions síncrones**: 800ms
- **Peticions asíncrones**: 500ms

Aquesta latència es pot modificar editant els valors a `ClienteUnicoMock.java`.

## Desenvolupament

### Afegir nous comportaments

Si necessites simular comportaments específics, pots:

1. **Modificar la latència**: Canviar els `Thread.sleep()` a `ClienteUnicoMock.java`

2. **Simular errors**: Utilitzar el mètode `simularError()`
   ```java
   @Autowired
   private ClienteUnicoMock clienteMock;

   // En un test o desenvolupament
   clienteMock.simularError("9999", "Error simulat per proves");
   ```

3. **Afegir més dades a les respostes**: Editar els mètodes de `ClienteUnicoMock.java`

### Exemple: Simular diferents serveis

```java
@Override
public Respuesta recuperaRespuesta(String idPeticion) throws ScspException {
    // Detectar el tipus de servei segons l'idPeticion
    if (idPeticion.contains("ECOT")) {
        return crearRespostaCertificatEmpresa();
    } else if (idPeticion.contains("SVDD")) {
        return crearRespostaRegistreCivil();
    }
    // ... resposta per defecte
}
```

## Limitacions

⚠️ **Aquest mock NO és adequat per**:
- Entorns de producció
- Tests d'integració amb serveis reals
- Validació de certificats
- Proves de rendiment reals

⚠️ **El mock NO valida**:
- Estructura de dades específiques
- Esquemes XSD reals
- Signatures digitals
- Certificats SSL/TLS

## Desactivar el mock

Per tornar a utilitzar els serveis SCSP reals:

```bash
# Eliminar el perfil mock-scsp
mvn spring-boot:run

# O especificar un altre perfil
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Troubleshooting

### El mock no s'activa

1. Verificar que el perfil està actiu:
   ```bash
   # Als logs, hauries de veure:
   The following profiles are active: mock-scsp
   ```

2. Verificar que la classe està al classpath:
   ```bash
   mvn clean install
   ```

3. Comprovar que Spring detecta el component:
   ```bash
   # Als logs de Spring:
   Mapped bean: 'clienteUnicoMock'
   ```

### Errors de compilació

Si `ClienteUnico` no es troba, assegurar-te que la dependència `scsp-core` està al POM:
```xml
<dependency>
    <groupId>es.scsp</groupId>
    <artifactId>scsp-core</artifactId>
    <version>4.26.1</version>
</dependency>
```

### El mock i el real s'executen simultàniament

Això no hauria de passar gràcies a `@Primary`. Si passa:
- Verificar que només hi ha un perfil actiu
- Comprovar que no hi ha altres beans de tipus `ClienteUnico` sense `@Profile`

## Contribuir

Per afegir millores al mock:

1. Editar `ClienteUnicoMock.java`
2. Afegir tests si cal
3. Actualitzar aquesta documentació
4. Fer commit amb missatge descriptiu

---

**Data de creació**: Gener 2026
**Versió**: 1.0
**Mantenidor**: Equip de desenvolupament Pinbal
