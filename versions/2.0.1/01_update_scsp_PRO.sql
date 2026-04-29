-- UpdateRequirente-4.25.0 a 4.28.0

CREATE SEQUENCE ID_SSL_CESIONARIO_SEQ START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE TABLE CORE_REQ_SSL_CESIONARIOS
(
    ID           NUMBER(19,0),
    SERVICIO     NUMBER(19,0),
    CLAVEPRIVADA NUMBER(19,0),
    ORGANISMO    NUMBER(19,0),
    FECHAALTA    TIMESTAMP(6),
    FECHABAJA    TIMESTAMP(6),
    PRIMARY KEY (ID)
);
ALTER TABLE "CORE_REQ_SSL_CESIONARIOS" ADD CONSTRAINT "fk_servicio_sslcesionario" FOREIGN KEY ("SERVICIO") REFERENCES "CORE_SERVICIO" ("ID") ENABLE;
ALTER TABLE "CORE_REQ_SSL_CESIONARIOS" ADD CONSTRAINT "fk_clave_priv_sslcesionario" FOREIGN KEY ("CLAVEPRIVADA") REFERENCES "CORE_CLAVE_PRIVADA" ("ID") ENABLE;
ALTER TABLE "CORE_REQ_SSL_CESIONARIOS" ADD CONSTRAINT "fk_organismo_sslcesionario" FOREIGN KEY ("ORGANISMO") REFERENCES "CORE_ORGANISMO_CESIONARIO" ("ID") ENABLE;

GRANT SELECT ON ID_SSL_CESIONARIO_SEQ TO WWW_PINBAL;
GRANT SELECT, UPDATE, INSERT, DELETE ON CORE_REQ_SSL_CESIONARIOS TO WWW_PINBAL;


INSERT INTO CORE_REQ_SSL_CESIONARIOS (ID, SERVICIO, CLAVEPRIVADA, ORGANISMO, FECHAALTA)
SELECT ID_SSL_CESIONARIO_SEQ.NEXTVAL,
       CORE_REQ_CESIONARIOS_SERVICIOS.SERVICIO,
       CORE_REQ_CESIONARIOS_SERVICIOS.CLAVEPRIVADA,
       CORE_REQ_CESIONARIOS_SERVICIOS.ORGANISMO,
       SYSDATE
FROM CORE_REQ_CESIONARIOS_SERVICIOS
WHERE CORE_REQ_CESIONARIOS_SERVICIOS.SSLFLAG = 1;

ALTER TABLE CORE_REQ_CESIONARIOS_SERVICIOS DROP COLUMN SSLFLAG;

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.27.3' where NOMBRE = 'version.datamodel.scsp';

-- End UpdateRequirente-4.25.0 a 4.28.0


-- UpdateRequirente-4.28.0 a 4.32.0

-- Requirente_01
ALTER TABLE CORE_CLAVE_PRIVADA ADD BLOQUEADO NUMBER(1,0) DEFAULT '0' NOT NULL;

-- Requirente_02
update core_transmision set clasetramite = 35 where clasetramite = 1;
update core_transmision set clasetramite = 36 where clasetramite = 2;
update core_transmision set clasetramite = 37 where clasetramite = 3;
update core_transmision set clasetramite = 38 where clasetramite = 4;
update core_transmision set clasetramite = 39 where clasetramite = 5;
update core_transmision set clasetramite = 40 where clasetramite = 6;
update core_transmision set clasetramite = 41 where clasetramite = 7;
update core_transmision set clasetramite = 42 where clasetramite = 8;
update core_transmision set clasetramite = 43 where clasetramite = 9;
update core_transmision set clasetramite = 44 where clasetramite = 10;
update core_transmision set clasetramite = 45 where clasetramite = 11;
update core_transmision set clasetramite = 46 where clasetramite = 12;
update core_transmision set clasetramite = 47 where clasetramite = 13;
update core_transmision set clasetramite = 48 where clasetramite = 14;
update core_transmision set clasetramite = 49 where clasetramite = 15;
update core_transmision set clasetramite = 50 where clasetramite = 16;
update core_transmision set clasetramite = 51 where clasetramite = 17;
update core_transmision set clasetramite = 52 where clasetramite = 18;
update core_transmision set clasetramite = 53 where clasetramite = 19;

update core_transmision set clasetramite = 34 where clasetramite = 35;
update core_transmision set clasetramite = 19 where clasetramite = 36;
update core_transmision set clasetramite = 20 where clasetramite = 37;
update core_transmision set clasetramite = 21 where clasetramite = 38;
update core_transmision set clasetramite = 22 where clasetramite = 39;
update core_transmision set clasetramite = 23 where clasetramite = 40;
update core_transmision set clasetramite = 24 where clasetramite = 41;
update core_transmision set clasetramite = 25 where clasetramite = 42;
update core_transmision set clasetramite = 26 where clasetramite = 43;
update core_transmision set clasetramite = 27 where clasetramite = 44;
update core_transmision set clasetramite = 28 where clasetramite = 45;
update core_transmision set clasetramite = 29 where clasetramite = 46;
update core_transmision set clasetramite = 2 where clasetramite = 47;
update core_transmision set clasetramite = 30 where clasetramite = 48;
update core_transmision set clasetramite = 31 where clasetramite = 49;
update core_transmision set clasetramite = 32 where clasetramite = 50;
update core_transmision set clasetramite = 14 where clasetramite = 51;
update core_transmision set clasetramite = 33 where clasetramite = 52;
update core_transmision set clasetramite = 3 where clasetramite = 53;


-- Requirente_NIVRENTI PRO
UPDATE CORE_SERVICIO set URLSINCRONA = 'https://intermediacion.redsara.es/servicios/SVD/AEAT.NIVRENT.NivelDeRenta', TIPOSEGURIDAD = 'XMLSignature' where CODCERTIFICADO = 'NIVRENTI';

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='4.32.0' where NOMBRE = 'version.datamodel.scsp';

-- End UpdateRequirente-4.28.0 a 4.32.0


-- UpdateRequirente-4.32.0 a 4.35.0

-- Requirente_01
UPDATE core_servicio SET plantillaXSLT = '/plantillaspdf/catastro/plantilla_ConsultaDatosCatastro_SVDCDATWS02.xslt' WHERE codcertificado = 'SVDCDATWS02';

-- Requirente_02 PRO
UPDATE CORE_SERVICIO
SET PLANTILLAXSLT = '/plantillaspdf/paisvasco/plantilla_PaisVascoIAE_SVDPVASCOIAEEXTWS01.xslt',
    URLASINCRONA='https://intermediacion.redsara.es/servicios/SVD/PaisVasco.ConsultaDatosIAE.Asincrona',
    URLSINCRONA='https://intermediacion.redsara.es/servicios/SVD/PaisVasco.ConsultaDatosIAE',
    ESQUEMAS='/schemas/SVDPVASCOIAEEXTWS01v3',
    CODCERTIFICADO='SVDPVASCOIAEEXTWS01'
WHERE CODCERTIFICADO = 'SVDPVIAEWS01';

-- Requirente_03
-- Generacion de la tabla que controlara aquellos certificados de firma que residan en un almacen ajeno a las librerias SCSP
CREATE TABLE "CORE_CLAVE_PRIVADA_EXTERNA"
(
    "CLAVE_PRIVADA" NUMBER(19,0) NOT NULL ENABLE,
    "HANDLER"       VARCHAR2(512 CHAR) NOT NULL ENABLE,
    PRIMARY KEY ("CLAVE_PRIVADA"),
    CONSTRAINT "CLAVEPRIVEXT_CLAVEPRIV" FOREIGN KEY ("CLAVE_PRIVADA")
        REFERENCES "CORE_CLAVE_PRIVADA" ("ID") ENABLE
);

GRANT SELECT, UPDATE, INSERT, DELETE ON CORE_CLAVE_PRIVADA_EXTERNA TO WWW_PINBAL;


-- Requirente_04
-- Se actualiza el nombre del servicio
UPDATE core_servicio SET descripcion = 'Consulta de los últimos doce meses de la vida laboral' where codcertificado = 'SVDTGSSVIDALABORALWS01';
UPDATE core_servicio SET descripcion = 'Consulta de los datos de un título universitario' where codcertificado = 'SVDVTUWS01';
UPDATE core_servicio SET descripcion = 'Consulta de los datos de un título no universitario' where codcertificado = 'SVDVTNUWS01';

-- Requirente_05 PRO
UPDATE core_servicio SET urlSincrona='https://ws.ia.aeat.es/wlpl/SUWS-JDIT/ws/domfis/DomFV3SOAP' WHERE codcertificado = 'DOMFISC';

-- Requirente_06
UPDATE core_servicio SET tiposeguridad='WS-Security' WHERE codcertificado = 'ECOT101';
UPDATE core_servicio SET tiposeguridad='WS-Security' WHERE codcertificado = 'ECOTGEN';
UPDATE core_servicio SET tiposeguridad='WS-Security' WHERE codcertificado = 'ECOT104';
UPDATE core_servicio SET tiposeguridad='WS-Security' WHERE codcertificado = 'ECOT103';
UPDATE core_servicio SET tiposeguridad='WS-Security' WHERE codcertificado = 'ECOT102';

-- Requirente_07
update core_servicio set descripcion = 'Consulta de Permisos de Conducir de un Conductor' where codcertificado = 'SVDDGTCONDUCTORPERMISOWS01';

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR ='4.35.0' where NOMBRE = 'version.datamodel.scsp';

-- End UpdateRequirente-4.32.0 a 4.35.0


-- UpdateRequirente-4.35.0 a 4.37.0

-- Requirente_01
-- Se añade una columna para especificar un posible manejador de firma externo para las peticiones al servicio
ALTER TABLE CORE_SERVICIO ADD SIGNER VARCHAR2(512 BYTE);
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES ('external.signer.validate.sign', 'false', 'Flag que indica si se debe validar la firma generada desde un manejador externo');
INSERT INTO CORE_PARAMETRO_CONFIGURACION(NOMBRE, VALOR, DESCRIPCION) VALUES ('external.signer.afirma', 'none', 'Manejador externo que realizara la firma de las firmas enviadas a @firma');

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR ='4.38.0' where NOMBRE = 'version.datamodel.scsp';

-- End UpdateRequirente-4.35.0 a 4.37.0


-- UpdateRequirente-4.37.0 a 4.38.0

-- Requirente_01
update core_servicio set descripcion = 'Consulta detallada sobre empleado público en Auténtica' where codcertificado = 'SVDRCPPUESTOWS01';
update core_servicio set descripcion = 'Consulta de datos IRPF de las Haciendas Forales Vascas (País Vasco)' where codcertificado = 'SVDPVASCOIRPFWS01';

-- End UpdateRequirente-4.37.0 a 4.38.0


-- UpdateRequirente-4.38.0 a 4.45.1

-- Requirente_01
update core_servicio set tiposeguridad = 'WS-Security' where codcertificado = 'NIVRENTI';

-- End UpdateRequirente-4.38.0 a 4.45.1


-- UpdateRequirente-4.45.1 a 4.48.1

-- Requirente_01
update core_servicio set descripcion='Consulta del Certificado del Valor de Referencia y Motivación del cálculo' where codcertificado = 'SVDCATASTROCERTIFICADOVDRWS01';
update core_servicio set descripcion='Consulta del Valor de Referencia' where codcertificado = 'SVDCATASTROVDRWS01';
update core_servicio set descripcion='Confirmación del estado de la última descarga' where codcertificado = 'SVDDGOJCONFDESCARGAWS01';
update core_servicio set descripcion='Descarga Completa del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCCOMPLWS01';
update core_servicio set descripcion='Descarga Desde la última Descarga del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCULTIMAWS01';
update core_servicio set descripcion='Descarga Parcial del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCPARCIALWS01';
update core_servicio set descripcion='Registro de Alta en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJALTAWS01';
update core_servicio set descripcion='Registro de Baja en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJBAJAWS01';
update core_servicio set descripcion='Registro de Modificación en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJMODIFWS01';
update core_servicio set descripcion='Consulta de Datos de Vehículo para Sanciones' where codcertificado = 'SVDDGTVEHICULOSANCWS01';
update core_servicio set descripcion='Consulta de listado de Vehículos' where codcertificado = 'SVDDGTVEHICULOHISTWS01';
update core_servicio set descripcion='Consulta de saldo de puntos' where codcertificado = 'SVDDGTSALDOPUNTOSWS01';
update core_servicio set descripcion='Consulta de un vehículo para el abono de daños ocasionados en la vía' where codcertificado = 'SVDDGTVEHICULODANOSVIAWS01';
update core_servicio set descripcion='Consulta sanciones, vigencias, condenas' where codcertificado = 'SVDDGTCONDUCTORSANCWS01';
update core_servicio set descripcion='Consulta de centros universitarios' where codcertificado = 'SVDEDURJCUCENTROSUNIVWS01';
update core_servicio set descripcion='Consulta de profesiones reguladas' where codcertificado = 'SVDEDURJCUPROFREGUWS01';
update core_servicio set descripcion='Consulta de títulos universitarios' where codcertificado = 'SVDEDURJCUTITUNIVWS01';
update core_servicio set descripcion='Consulta de universidades' where codcertificado = 'SVDEDURJCUUNIVWS01';
update core_servicio set descripcion='Consulta detallada de titulaciones universitarias' where codcertificado = 'SVDEDURJCUDETALLETITUNIVWS01';
update core_servicio set descripcion='Alta de Pagos en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUALTAPAGOWS01';
update core_servicio set descripcion='Alta de Prestaciones en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUALTAPRESTACIONWS01';
update core_servicio set descripcion='Baja de Pagos en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUBAJAPAGOWS01';
update core_servicio set descripcion='Baja de Prestaciones en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUBAJAPRESTACIONWS01';
update core_servicio set descripcion='Cambio de Situación de Pagos en la Tarjeta Social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUCAMBSITUACIONPAGOWS01';
update core_servicio set descripcion='Cambio de situación de Prestaciones en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUCAMBSITUACIONPRESTWS01';
update core_servicio set descripcion='Modificación de datos personales en la Tarjeta social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUMODIFDATPERWS01';
update core_servicio set descripcion='Modificación de la identificación de una persona asociada a una prestación en la Tarjeta Social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUCAMBIDPERSONAWS01';
update core_servicio set descripcion='Modificación de Prestaciones en la Tarjeta Social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUMODPRESTACIONWS01';
update core_servicio set descripcion='Traspaso de Prestaciones en la Tarjeta Social Universal. Instituto Nacional de la Seguridad Social' where codcertificado = 'SVDTSUTRASPASOPRESTACIONWS01';
update core_servicio set descripcion='Consulta de Datos de Parejas de Hecho' where codcertificado = 'SVDCCAAIRPHWS01';
update core_servicio set descripcion='Consulta de datos del registro oficial de licitadores y empresas clasificadas del sector público' where codcertificado = 'SVDMINHAFPROLECEWS01';
update core_servicio set descripcion='Consulta de escolarización' where codcertificado = 'SVDSMATESCOLARIZAWS01';
update core_servicio set descripcion='Consulta de Renta Salario Prestación Social Básica (Importes Actuales)' where codcertificado = 'SVDSRSPSBACTUALWS01';
update core_servicio set descripcion='Consulta de Renta Salario Prestación Social Básica (Importes Anuales)' where codcertificado = 'SVDSRSPSBANUALWS01';
update core_servicio set descripcion='Consulta de formación sanitaria especializada' where codcertificado = 'SVDSMSSSIRESIDENTEWS01';
update core_servicio set descripcion='Consulta de ser beneficiario de víctima del terrorismo' where codcertificado = 'SVDIVTWS01';
update core_servicio set descripcion='Consulta de ser beneficiario de víctima del terrorismo Manual' where codcertificado = 'SVDIVTMWS01';
update core_servicio set descripcion='Consulta de Garantías' where codcertificado = 'SVDSTESOROGARANTIASWS01';
update core_servicio set descripcion='Consulta de vida laboral en los cinco últimos años' where codcertificado = 'SVDTGSSVIDALABORAL5ANIOSWS01';
update core_servicio set descripcion='Consulta de Plantilla Media de una Empresa' where codcertificado = 'SVDTGSSPLANTILLAMEDIAWS01';
update core_servicio set descripcion='Consulta de vida laboral en los últimos doce meses' where codcertificado = 'SVDTGSSVIDALABORALWS01';
update core_servicio set descripcion='Consulta de los datos de un título universitario' where codcertificado = 'SVDVTUWS01';
update core_servicio set descripcion='Consulta de los datos de un título no universitario' where codcertificado = 'SVDVTNUWS01';

-- Requirente_02
update core_servicio set descripcion = 'Consulta detallada sobre empleado público en Auténtica' where codcertificado = 'SVDRCPPUESTOWS01';
update core_servicio set descripcion = 'Consulta de datos IRPF de las Haciendas Forales Vascas (País Vasco)' where codcertificado = 'SVDPVASCOIRPFWS01';

-- End UpdateRequirente-4.37.0 a 4.38.0


-- UpdateRequirente-4.38.0 a 4.45.1

-- Requirente_01
update core_servicio set tiposeguridad = 'WS-Security' where codcertificado = 'NIVRENTI';

-- End UpdateRequirente-4.38.0 a 4.45.1


-- UpdateRequirente-4.45.1 a 4.48.1

-- Requirente_01
update core_servicio set descripcion='Consulta del Certificado del Valor de Referencia y Motivación del cálculo' where codcertificado = 'SVDCATASTROCERTIFICADOVDRWS01';
update core_servicio set descripcion='Consulta del Valor de Referencia' where codcertificado = 'SVDCATASTROVDRWS01';
update core_servicio set descripcion='Confirmación del estado de la última descarga' where codcertificado = 'SVDDGOJCONFDESCARGAWS01';
update core_servicio set descripcion='Consulta del histórico de municipios de residencia' where codcertificado = 'SVDINEHISTORICOMUNICIPIOSWS01';
COMMIT;

-- Requirente_02
update core_servicio set descripcion='Consulta de convivencia actual por LEY (SECOPA)' where codcertificado = 'SVDINESECOPACONVIVENCIAACTUALWS01';
update core_servicio set descripcion='Consulta del historico de residencia y convivientes por LEY (SECOPA)' where codcertificado = 'SVDINESECOPAHISTRESIDENCIACONVWS01';
update core_servicio set descripcion='Consulta de datos de residencia con fecha de última variación para finalidades distintas a la supresión del volante de empadronamiento por LEY (SECOPA)' where codcertificado = 'SVDSECOPA01WS01';
update core_servicio set descripcion='Consulta de defunción - DICIREG' where codcertificado = 'SVDRRCCDEFUNCIONWS01';
update core_servicio set descripcion='Consulta de nacimiento - DICIREG' where codcertificado = 'SVDRRCCNACIMIENTOWS01';
update core_servicio set descripcion='Consulta de domicilio fiscal' where codcertificado = 'SVDNAVARRADOMFISCWS01';
update core_servicio set descripcion='Consulta de datos del impuesto de actividades económicas' where codcertificado = 'HTNIAE';
update core_servicio set descripcion='Consulta de datos del nivel de renta' where codcertificado = 'SVDNIVRENTINAVARRAWS01';
update core_servicio set descripcion='Consulta de datos del domicilio fiscal' where codcertificado = 'SVDPVASCODOMFISCWS01';
update core_servicio set descripcion='Consulta de datos de IRPF' where codcertificado = 'SVDPVASCOIRPFWS01';
update core_servicio set descripcion='Consulta de datos del impuesto de actividades económicas' where codcertificado = 'SVDPVASCOIAEEXTWS01';
update core_servicio set descripcion='Consulta de Empresas Relacionadas con un tiular' where codcertificado = 'SVDPVASCOEMPRESASRELWS01';
update core_servicio set descripcion='Consulta del histórico de municipios de residencia' where codcertificado = 'SVDINEHISTORICOMUNICIPIOSWS01';
update core_servicio set descripcion='Consulta de paises pertenecientes al convenio de la haya para apostillas' where codcertificado = 'SVDAPOSTILLAPAISESHAYAWS01';
update core_servicio set descripcion='Consulta de estar al corriente de pago de obligaciones tributarias genérico' where codcertificado = 'SVDNAVARRACPAGOGENERICOWS01';
update core_servicio set descripcion='Consulta de estar al corriente de pago de obligaciones tributarias para residencia' where codcertificado = 'SVDNAVARRACPAGORESIDENCIAWS01';
update core_servicio set descripcion='Consulta de estar al corriente de pago de obligaciones tributarias para licencias de transporte' where codcertificado = 'SVDNAVARRACPAGOTRANSPORTESWS01';

update core_servicio set descripcion='Consulta de datos de vehículos' where codcertificado = 'SVDDGTVEHICULODATOSWS01';
update core_servicio set descripcion='Anulación de una apostilla' where codcertificado = 'SVDAPOSTILLAANULARWS01';
update core_servicio set descripcion='Obtención de apostilla' where codcertificado = 'SVDAPOSTILLAOBTENERWS01';
update core_servicio set descripcion='Obtención del estado de una apostilla' where codcertificado = 'SVDAPOSTILLAESTADOWS01';
update core_servicio set descripcion='Solicitud de apostilla de documentos firmados' where codcertificado = 'SVDAPOSTILLASOLICITARWS01';

update core_servicio set descripcion='Consulta del certificado del valor de referencia y motivación del cálculo' where codcertificado = 'SVDCATASTROCERTIFICADOVDRWS01';
update core_servicio set descripcion='Consulta del valor de referencia' where codcertificado = 'SVDCATASTROVDRWS01';
update core_servicio set descripcion='Confirmación del estado de la última descarga' where codcertificado = 'SVDDGOJCONFDESCARGAWS01';
update core_servicio set descripcion='Descarga completa del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCCOMPLWS01';
update core_servicio set descripcion='Descarga desde la última descarga del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCULTIMAWS01';
update core_servicio set descripcion='Descarga parcial del Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJDESCPARCIALWS01';
update core_servicio set descripcion='Registro de alta en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJALTAWS01';
update core_servicio set descripcion='Registro de baja en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJBAJAWS01';
update core_servicio set descripcion='Registro de modificación en Registro General de Interdicciones de Acceso al Juego' where codcertificado = 'SVDDGOJMODIFWS01';
update core_servicio set descripcion='Consulta de datos de vehículo para sanciones' where codcertificado = 'SVDDGTVEHICULOSANCWS01';
update core_servicio set descripcion='Consulta de listado de vehículos' where codcertificado = 'SVDDGTVEHICULOHISTWS01';
update core_servicio set descripcion='Consulta de saldo de puntos' where codcertificado = 'SVDDGTSALDOPUNTOSWS01';
update core_servicio set descripcion='Consulta de un vehículo para el abono de daños ocasionados en la vía' where codcertificado = 'SVDDGTVEHICULODANOSVIAWS01';
update core_servicio set descripcion='Consulta sanciones, vigencias, condenas' where codcertificado = 'SVDDGTCONDUCTORSANCWS01';
update core_servicio set descripcion='Consulta de centros universitarios' where codcertificado = 'SVDEDURJCUCENTROSUNIVWS01';
update core_servicio set descripcion='Consulta de profesiones reguladas' where codcertificado = 'SVDEDURJCUPROFREGUWS01';
update core_servicio set descripcion='Consulta de títulos universitarios' where codcertificado = 'SVDEDURJCUTITUNIVWS01';
update core_servicio set descripcion='Consulta de universidades' where codcertificado = 'SVDEDURJCUUNIVWS01';
update core_servicio set descripcion='Consulta detallada de titulaciones universitarias' where codcertificado = 'SVDEDURJCUDETALLETITUNIVWS01';
update core_servicio set descripcion='Alta de pagos en la tarjeta social universal' where codcertificado = 'SVDTSUALTAPAGOWS01';
update core_servicio set descripcion='Alta de prestaciones en la tarjeta social universal' where codcertificado = 'SVDTSUALTAPRESTACIONWS01';
update core_servicio set descripcion='Baja de pagos en la tarjeta social universal' where codcertificado = 'SVDTSUBAJAPAGOWS01';
update core_servicio set descripcion='Baja de prestaciones en la tarjeta social universal' where codcertificado = 'SVDTSUBAJAPRESTACIONWS01';
update core_servicio set descripcion='Cambio de situación de pagos en la tarjeta social universal' where codcertificado = 'SVDTSUCAMBSITUACIONPAGOWS01';
update core_servicio set descripcion='Cambio de situación de prestaciones en la tarjeta social universal' where codcertificado = 'SVDTSUCAMBSITUACIONPRESTWS01';
update core_servicio set descripcion='Modificación de datos personales en la tarjeta social universal' where codcertificado = 'SVDTSUMODIFDATPERWS01';
update core_servicio set descripcion='Modificación de la identificación de una persona asociada a una prestación en la tarjeta social universal' where codcertificado = 'SVDTSUCAMBIDPERSONAWS01';
update core_servicio set descripcion='Modificación de prestaciones en la tarjeta social universal' where codcertificado = 'SVDTSUMODPRESTACIONWS01';
update core_servicio set descripcion='Traspaso de prestaciones en la tarjeta social universal' where codcertificado = 'SVDTSUTRASPASOPRESTACIONWS01';
update core_servicio set descripcion='Consulta de datos de parejas de hecho' where codcertificado = 'SVDCCAAIRPHWS01';
update core_servicio set descripcion='Consulta de datos del registro oficial de licitadores y empresas clasificadas del sector público' where codcertificado = 'SVDMINHAFPROLECEWS01';
update core_servicio set descripcion='Consulta de escolarización' where codcertificado = 'SVDSMATESCOLARIZAWS01';
update core_servicio set descripcion='Consulta de renta salario prestación social básica (importes actuales)' where codcertificado = 'SVDSRSPSBACTUALWS01';
update core_servicio set descripcion='Consulta de renta salario prestación social básica (importes anuales)' where codcertificado = 'SVDSRSPSBANUALWS01';
update core_servicio set descripcion='Consulta de formación sanitaria especializada' where codcertificado = 'SVDSMSSSIRESIDENTEWS01';
update core_servicio set descripcion='Consulta de ser beneficiario de víctima del terrorismo' where codcertificado = 'SVDIVTWS01';
update core_servicio set descripcion='Consulta de ser beneficiario de víctima del terrorismo manual' where codcertificado = 'SVDIVTMWS01';
update core_servicio set descripcion='Consulta de garantías' where codcertificado = 'SVDSTESOROGARANTIASWS01';
update core_servicio set descripcion='Consulta de vida laboral en los cinco últimos años' where codcertificado = 'SVDTGSSVIDALABORAL5ANIOSWS01';
update core_servicio set descripcion='Consulta de plantilla media de una empresa' where codcertificado = 'SVDTGSSPLANTILLAMEDIAWS01';
update core_servicio set descripcion='Consulta del histórico de municipios de residencia' where codcertificado = 'SVDINEHISTORICOMUNICIPIOSWS01';

update core_servicio set descripcion='Consulta de autorizaciones en la Plataforma de Intermediación' where codcertificado = 'SVDMINHAFPCONSULTAAUTWS01';
update core_servicio set descripcion='Consulta de datos de discapacidad para obtención de tarjeta de estacionamiento' where codcertificado = 'SVDSCDDESTACWS01';
update core_servicio set descripcion='Consulta de datos de convivencia a fecha actual' where codcertificado = 'SVDCONVIVENCIAWS01';
update core_servicio set descripcion='Consulta del histórico de datos de convivencia para un periodo' where codcertificado = 'SVDHISTCONVIVENCIAWS01';
update core_servicio set descripcion='Consulta del distintivo medioambiental de un vehículo' where codcertificado = 'SVDDGTTARJETAAMBIENTALWS01';
update core_servicio set descripcion='Consulta de las autorizaciones de transporte de un titular' where codcertificado = 'SVDFOMENTOAUTTITULARWS01';

update core_servicio set descripcion='Consulta del estado de una apostilla' where codcertificado = 'SVDAPOSTILLAESTADOWS01';
update core_servicio set descripcion='Obtención de apostilla realizada' where codcertificado = 'SVDAPOSTILLAOBTENERWS01';

-- End UpdateRequirente-4.45.1 a 4.48.1


-- UpdateRequirente-4.48.1 a 4.51.1

-- Requirente_01
update core_servicio set descripcion='Consulta de la condición de deportista de alto nivel' where codcertificado = 'SVDCSDBECASDANWS01';
update core_servicio set plantillaxslt='/plantillaspdf/justicia/plantilla_DefuncionMasiva_SVDDEFMAWS01.xslt' where codcertificado = 'SVDDEFMAWS01';
update core_servicio set descripcion='Consulta de Empresas Relacionadas con un titular' where codcertificado = 'SVDPVASCOEMPRESASRELWS01';

-- Requirente_02 PRO
update core_servicio set actionasincrona='peticionAsincrona', urlasincrona='https://intermediacion.redsara.es/servicios/SVD/Justicia.SolicitarApostillaCSV.Asincrona' where codcertificado = 'SVDAPOSTILLASOLICITARCSVWS01';

-- Requirente_03 PRO
update core_servicio set actionasincrona='peticionAsincrona', urlasincrona='https://intermediacion.redsara.es/servicios/SVD/Justicia.SolicitarApostilla.Asincrona' where codcertificado = 'SVDAPOSTILLASOLICITARWS01';
update core_servicio set urlsincrona='https://ws.ia.aeat.es/wlpl/iniinvoc/es.aeat.dit.adu.suap.irpfws.IRPFXMLV3SOAP' where codcertificado = 'AEATIR01';

-- Requirente_04
update core_servicio set descripcion='Verificación de un vehículo VTC a fecha concreta' where codcertificado = 'SVDFOMENTOVTCWS01';
update core_servicio set descripcion='Consulta del listado de vehículos VTC a fecha concreta' where codcertificado = 'SVDFOMENTOLISTADOVTCWS01';

-- End UpdateRequirente-4.48.1 a 4.51.1


-- UpdateRequirente-4.51.1 a 4.52.1

-- Requirente_01
update core_servicio set plantillaxslt='/plantillaspdf/ine/plantilla_ConvivenciaHistorico_SVDINESECOPAHISTRESIDENCIACONVWS01.xslt' where codcertificado = 'SVDINESECOPAHISTRESIDENCIACONVWS01';

UPDATE CORE_PARAMETRO_CONFIGURACION SET VALOR='5.11.0' where NOMBRE = 'version.datamodel.scsp';

-- End UpdateRequirente-4.51.1 a 4.52.1


-- UpdateRequirente-4.52.1 a 4.57.2

-- Requirente_01
update core_servicio set descripcion='Consulta del valor de referencia de un inmueble' where codcertificado = 'SVDCATASTROVDRWS01';
update core_servicio set descripcion='Consulta de Datos de Residencia en entidades locales' where codcertificado = 'SVDREELLEXTFECHAWS01';

-- Requirente_02
update core_servicio set tiposeguridad='WS-Security' where codcertificado = 'SVDSEPEDATOSCONTRATOWS01';

-- Requirente_03
update core_servicio set descripcion='Consulta de Datos del titular de un Vehículo por parte del titular de la vía' where codcertificado = 'SVDDGTVEHICULODANOSVIAWS01';

-- Requirente_04 PRO
update core_servicio
set codcertificado='SVDCSDCONDICIONALTONIVELWS01',
    descripcion='Consulta de la condición de deportista de alto nivel',
    plantillaxslt='/plantillaspdf/csd/plantilla_BecasDeportistasEliteSVDCSDCONDICIONALTONIVELWS01.xslt',
    esquemas='/schemas/SVDCSDCONDICIONALTONIVELWS01v3',
    urlsincrona='https://intermediacion.redsara.es/servicios/SVD/CSD.ConsultaCondicionDeportistasAltoNivel',
    urlasincrona='https://intermediacion.redsara.es/servicios/SVD/CSD.ConsultaCondicionDeportistasAltoNivel.Asincrona'
where codcertificado = 'SVDCSDBECASDANWS01';

-- End UpdateRequirente-4.52.1 a 4.57.2


-- UpdateRequirente-4.57.2 a 4.60.1

-- Requirente_01
update core_servicio set plantillaxslt='/plantillaspdf/catastro/plantilla_ConsultaDatosCatastro_SVDCDYGWS02.xslt' where codcertificado = 'SVDCDYGWS02';

-- Requirente_02 PRO
update core_servicio
set urlsincrona='https://intermediacion.redsara.es/servicios/SVD/INTERIOR.ConsultaAsociaciones',
    urlasincrona='https://intermediacion.redsara.es/servicios/SVD/INTERIOR.ConsultaAsociaciones.Asincrona'
where codcertificado = 'SVDMIRASOCIACIONESWS01';

-- End UpdateRequirente-4.57.2 a 4.60.1


-- UpdateRequirente-4.60.1 a 4.63.3

-- Requirente_01
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ley de contratos' WHERE CODCERTIFICADO = 'ECOT101I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Autorización de transportes' WHERE CODCERTIFICADO = 'ECOT102I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ayudas y subvenciones' WHERE CODCERTIFICADO = 'ECOT103I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Residencia y trabajo' WHERE CODCERTIFICADO = 'ECOT104I';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Genérico' WHERE CODCERTIFICADO = 'ECOTGENI';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Nivel de renta intermediado' WHERE CODCERTIFICADO = 'NIVRENTI';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ley de contratos' WHERE CODCERTIFICADO = 'ECOT101';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Autorización de transportes' WHERE CODCERTIFICADO = 'ECOT102';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Ayudas y subvenciones' WHERE CODCERTIFICADO = 'ECOT103';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Residencia y trabajo' WHERE CODCERTIFICADO = 'ECOT104';
UPDATE CORE_SERVICIO SET DESCRIPCION='ECOT - Genérico' WHERE CODCERTIFICADO = 'ECOTGEN';

-- End UpdateRequirente-4.60.1 a 4.63.3


-- UpdateRequirente-4.63.3 a 4.65.3

-- Requirente_01
update core_servicio set descripcion='Consulta de especialidades de titulaciones sanitarias universitarias por filiación' where codcertificado = 'SVDUNIVERSIDADESESPECIALIDADESSANITARIASFILWS01';
update core_servicio set descripcion='Consulta de especialidades de titulaciones sanitarias universitarias por documentación' where codcertificado = 'SVDUNIVERSIDADESESPECIALIDADESSANITARIASDOCWS01';

-- End UpdateRequirente-4.63.3 a 4.65.3


-- UpdateRequirente-4.65.3 a 4.70.3

-- Requirente_01
update core_servicio set descripcion='Consulta detallada sobre empleado público en Autentica' where codcertificado = 'SVDRCPPUESTOWS01';

-- Requirente_02
update core_servicio set plantillaxslt='/plantillaspdf/ine/plantilla_ServicioConsultaResidenciaFecha_SVDREXTFECHAWS01.xslt' where codcertificado = 'SVDREXTFECHAWS01';
update core_servicio set plantillaxslt='/plantillaspdf/ine/plantilla_ServicioConsultaResidenciaTerritorial_SVDRWS01.xslt' where codcertificado = 'SVDRWS01';
update core_servicio set plantillaxslt='/plantillaspdf/ine/plantilla_MunicipioResidenciaHistorico_SVDINEHISTORICOMUNICIPIOSWS01.xslt' where codcertificado = 'SVDINEHISTORICOMUNICIPIOSWS01';
update core_servicio set plantillaxslt='/plantillaspdf/ine/plantilla_MunicipioResidenciaHistorico_SVDINESECOPAHISTORICOMUNICIPIOSWS01.xslt' where codcertificado = 'SVDINESECOPAHISTORICOMUNICIPIOSWS01';

-- Requirente_03
INSERT INTO CORE_PARAMETRO_CONFIGURACION (NOMBRE, VALOR, DESCRIPCION) VALUES ('afirma.protocolo', 'TLSv1.2', 'Protocolo TLS para el establecimiento del SSL');

-- End UpdateRequirente-4.65.3 a 4.70.3


-- UpdateRequirente-4.70.3 a 4.71.5

-- Requirente_01
update core_servicio set descripcion='Consulta de vehículos UE (o europeos) por infracción de tráfico' where codcertificado = 'SVDDGTEUCARISWS01';

-- Requirente_02
update core_servicio set descripcion='Consulta de propiedades' where codcertificado = 'SVDDGMMPROPIEDADESWS01';

-- End UpdateRequirente-4.70.3 a 4.71.5


-- UpdateRequirente-4.71.5 a 5.11.0

-- Requirente_01
update core_servicio set descripcion='Consulta del historial de sanciones de un conductor' where codcertificado = 'SVDDGTCONDUCTORSANCWS01';

-- End UpdateRequirente-4.71.5 a 5.11.0
