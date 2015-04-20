- Propietat de sistema per al fitxer de properties
--------------------------------------------------
-Des.caib.pinbal.properties.path="file://c:/tmp/pinbal/pinbal.properties"

- Propietats de sistema per poder fer connexions SSL cap a SCSP
---------------------------------------------------------------
-Djavax.net.ssl.trustStore=c:/tmp/pinbal/truststore.jks -Djavax.net.ssl.trustStorePassword=changeit

- Propietats de sistema per a configurar el keystore de signaturacaib
---------------------------------------------------------------------
(http://www.caib.es/signaturacaib/docum/manual_instal_servidores.jsp)
-Dcaib-crypto-keystore=[Ruta al keystore jks que conte els certificats] -Dcaib-crypto-keystore-password=[Contrasenya del keystore]
(convé que la clau del keystore i del certificat siguin la mateixa o no funcionarà)

- Actualitzar versió SCSP
---------------------------
- Actualització repositori local:
	mvn install:install-file -Dfile=scsp-core-X.X.X.jar -DgroupId=es.scsp -DartifactId=scsp-core -Dversion=X.X.X -Dpackaging=jar -DlocalRepositoryPath=local-repo
	mvn install:install-file -Dfile=scsp-beans-X.X.X.jar -DgroupId=es.scsp -DartifactId=scsp-beans -Dversion=X.X.X -Dpackaging=jar -DlocalRepositoryPath=local-repo
- Canviar versió scsp al pom.xml.
- Revisar classes modificades:
	- /pinbal-scsp/src/main/java/es/scsp/common/dao/SecuenciaIdPeticionPinbalDao.java
	- /pinbal-scsp/src/main/java/es/scsp/common/dao/SecuenciaIdTransmisionPinbalDao.java
- Revisar applicationContext-scsp.xml
	- /pinbal-scsp/src/main/resources/applicationContext-scsp.xml
