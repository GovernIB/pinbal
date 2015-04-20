--------------------------------------------------------
--  Pre carga de esquema scsp requirente 
--------------------------------------------------------

--------------------------------------------------------
--  Inserts for Table CORE_TIPO_MENSAJE
--------------------------------------------------------
Insert into CORE_TIPO_MENSAJE (TIPO,DESCRIPCION) values (0,'Peticion');
Insert into CORE_TIPO_MENSAJE (TIPO,DESCRIPCION) values (1,'ConfirmacionPeticion');
Insert into CORE_TIPO_MENSAJE (TIPO,DESCRIPCION) values (2,'SolicitudRespuesta');
Insert into CORE_TIPO_MENSAJE (TIPO,DESCRIPCION) values (3,'Respuesta');
Insert into CORE_TIPO_MENSAJE (TIPO,DESCRIPCION) values (4,'Fault');

--------------------------------------------------------
--  Inserts for Table SCSP_ESTADO_PETICION
--------------------------------------------------------
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0001', 'Pendiente');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0002', 'En proceso');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0003', 'Tramitada');
Insert into SCSP_ESTADO_PETICION (CODIGO,MENSAJE) values('0004', 'En proceso Polling');

--------------------------------------------------------
--  Inserts for Table SCSP_CODIGO_ERROR
--------------------------------------------------------
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0101','Error al contactar con el servicio Web especificado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0102','Comunicación sin respuesta {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0103','Servidor responde mensaje que no es XML');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0201','Error al generar el identificativo de petición');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0202','Error al insertar la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0203','Error al actualizar el estado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0204','Error al actualizar el TER {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0205','Error al actualizar la fecha de último sondeo {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0206','Error al actualizar el fichero de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0207','Error al recuperar el estado de la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0208','Fichero de respuesta caducado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0209','Error al comprobar las transmisiones insertadas {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0210','Error al recuperar el CIF del Organismo Requirente {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0211','Error al recuperar el TER {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0212','Error al descomponer el fichero de petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0213','Error al recuperar peticiones pendientes. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0214','Error al insertar las transmisiones. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0215','Error al actualizar el campo error {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0216','Error al actualizar el mensaje de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0217','Error al recuperar la CADUCIDAD {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0218','Error al descomponer el mensaje de petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0219','Error al recuperar el fichero de respuesta {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0220','Error al enviar la alarma de la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0221','Error al comprobar las peticiones pendientes.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0222','Error al borrar las respuestas caducadas.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0223','Error al escribir el fichero de errores {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0224','Error al borrar el fichero de error {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0225','Se ha alcanzado el número máximo de respuestas para la petición servidas.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0226','Error al parsear el XML {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0227','Error al generar la respuesta. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0229','La petición ya ha sido tramitada o ya existe en el sistema, está repetida');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0230','El timestamp de la petición debe ser válido y de hoy o de ayer. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0231','Documento Incorrecto {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0232','Documento con más de un identificador.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0233','Titular no identificado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0234','{0}. No se ha encontrado en base de datos configuración alguna para algún certificado asociado al código pasado por parámetro.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0235','El NIF del certificado no coincide con el tag NifSolicitante.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0236','Consentimiento del solicitante inválido. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0237','Tag NumElementos inválido. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0238','Información no disponible.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0239','Error al tratar los datos específicos. {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0240','Formato de documento inválido para NIE');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0241','Certificado o Respuesta Caducada');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0242','Error Genérico de la AEAT');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0243','No todas las solicitudes de transmisión hacen referencia al mismo certificado especificado en nodo <Atributos>');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0246','Error con los id transmision asignados por el backoffice. O todas las transmisiones poseen identificador o ninguna.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0247','Error Tag TER no valido');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0248','Error respuesta con un numero de transmisiones diferente a las solicitudes incluidas en la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0301','Organismo no autorizado {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0302','Certificado caducado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0303','Certificado revocado {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0304','El DN del Organismo Requirente no coincide con el almacenado para la petición {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0305','Firma no válida {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0306','Error al generar la firma del mensaje {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0307','No se ha encontrado el nodo firma.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0308','Error al obtener la firma del mensaje SOAP {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0309','Error general al verificar el certificado :{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0310','No se ha podido verificar la CA del certificado.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0311','No se ha encontrado el certificado firmante en el documento XML.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0312','NIF del emisor especificado no coincide con el Organismo Emisor');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0313','Error al Cifrar o descifrar el mensaje');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0401','La estructura del fichero recibido no corresponde con el esquema.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0402','Falta informar campo obligatorio {0} {1}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0403','Imposible obtener el contenido XML del mensaje SOAP.{0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0404','Tipo de documento del titular inválido.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0405','Error al transformar el XML en texto plano a partir de la plantilla {0}.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0406','Contenido del mensaje SOAP no esperado');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0501','Error de Base de Datos: {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0502','Error de sistema: {0}');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0901','Servicio no disponible');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0902','Modo síncrono no soportado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0903','Modo asíncrono no soportado.');
Insert into SCSP_CODIGO_ERROR (CODIGO,DESCRIPCION) values ('0904','Error general Indefinido {0}');