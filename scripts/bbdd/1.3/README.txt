---------------------------------------------------
- Nova instal·lació: ordre d'execució dels scripts:
---------------------------------------------------

SCSP:
  1.- Executar scsp_requiriente_creacion.sql
  2.- Executar scsp_requiriente_pre-carga.sql
  3.- Editar scsp_requiriente_configuracion.sql i configurar els paràmetres keystoreFile, keystoreType i keystorePass de la taula CORE_PARAMETRO_CONFIGURACION per a accedir correctament al magatzem de claus.
  4.- Executar scsp_requiriente_configuracion.sql

ACLs:
  5.- Executar spring_security_acl_creacio.sql

PINBAL:
  6.- Executar pinbal_creacio.sql

DGTIC:
  7.- Executar pinbal_dgtic.sql


--------------------------------------
- Actualització d'una versió anterior:
--------------------------------------

Si la versió anterior és la 1.0: executar pinbal_update_1.0.sql, pinbal_update_1.1.sql i pinbal_update_1.2.sql en aquest ordre.
Si la versió anterior és la 1.1: executar pinbal_update_1.1.sql i pinbal_update_1.2.sql.
Si la versió anterior és la 1.2: executar pinbal_update_1.2.sql.
