#!/bin/sh

APP_NAME=pinbal
DS_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-ds.xml
SERVICE_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-service.xml
LOG_CONFIG_FILE=/opt/jboss/server/default/conf/jboss-log4j.xml

echo "Substituint variables del datasource"
if [[ -n "$DATABASE_URL" ]]; then
	echo "Substituint DATABASE_URL per $DATABASE_URL"
	sed -i "s|DATABASE_URL|$DATABASE_URL|g" $DS_FILE
fi
if [[ -n "$DATABASE_USERNAME" ]]; then
	echo "Substituint DATABASE_USERNAME per $DATABASE_USERNAME"
	sed -i "s/DATABASE_USERNAME/$DATABASE_USERNAME/g" $DS_FILE
fi
if [[ -n "$DATABASE_PASSWORD" ]]; then
	echo "Substituint DATABASE_PASSWORD per $DATABASE_PASSWORD"
	sed -i "s/DATABASE_PASSWORD/$DATABASE_PASSWORD/g" $DS_FILE
fi

echo "Substituint variables del datasource d'USUARIS"
if [[ -n "$SEYCON_URL" ]]; then
	echo "Substituint SEYCON_URL per $SEYCON_URL"
	sed -i "s|SEYCON_URL|$SEYCON_URL|g" $DS_FILE
fi
if [[ -n "$SEYCON_USERNAME" ]]; then
	echo "Substituint SEYCON_USERNAME per $SEYCON_USERNAME"
	sed -i "s/SEYCON_USERNAME/$SEYCON_USERNAME/g" $DS_FILE
fi
if [[ -n "$SEYCON_PASSWORD" ]]; then
	echo "Substituint SEYCON_PASSWORD per $SEYCON_PASSWORD"
	sed -i "s/SEYCON_PASSWORD/$SEYCON_PASSWORD/g" $DS_FILE
fi

echo "Establint permisos per a poder accedir al volum de log"
su -c "chown default.default /opt/jboss/server/default/log" -

# echo "Configurant categories de log"
#sed -i 's#<root>#<category name="es.caib.pinbal.webapp.controller.ConsultaController"><priority value="TRACE" /></category><root>#g' $LOG_CONFIG_FILE

#echo "Substituint variables de properties"
#if [[ -n "$NOTIFICA_VERSIO" ]]; then
#	echo "Substituint NOTIFICA_VERSIO per $NOTIFICA_VERSIO"
#	sed -i "s/NOTIFICA_VERSIO/$NOTIFICA_VERSIO/g" $SERVICE_FILE
#fi

