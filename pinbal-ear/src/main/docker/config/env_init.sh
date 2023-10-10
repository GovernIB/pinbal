#!/bin/sh

APP_NAME=pinbal
DS_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-ds.xml
SERVICE_FILE=/opt/jboss/server/default/deploycaib/$APP_NAME-service.xml
LOG_CONFIG_FILE=/opt/jboss/server/default/conf/jboss-log4j.xml

echo "Substituint variables del datasource"
if [[ -n "$PINBAL_DB_DRIVER" ]]; then
	echo "Substituint PINBAL_DB_DRIVER per $PINBAL_DB_DRIVER"
	sed -i "s|PINBAL_DB_DRIVER|$PINBAL_DB_DRIVER|g" $DS_FILE
fi
if [[ -n "$PINBAL_DB_URL" ]]; then
	echo "Substituint PINBAL_DB_URL per $PINBAL_DB_URL"
	sed -i "s|PINBAL_DB_URL|$PINBAL_DB_URL|g" $DS_FILE
fi
if [[ -n "$PINBAL_DB_USERNAME" ]]; then
	echo "Substituint PINBAL_DB_USERNAME per $PINBAL_DB_USERNAME"
	sed -i "s/PINBAL_DB_USERNAME/$PINBAL_DB_USERNAME/g" $DS_FILE
fi
if [[ -n "$PINBAL_DB_PASSWORD" ]]; then
	echo "Substituint PINBAL_DB_PASSWORD per $PINBAL_DB_PASSWORD"
	sed -i "s/PINBAL_DB_PASSWORD/$PINBAL_DB_PASSWORD/g" $DS_FILE
fi

echo "Substituint variables del datasource d'USUARIS"
if [[ -n "$PINBAL_SEYCON_DB_DRIVER" ]]; then
	echo "Substituint PINBAL_SEYCON_DB_DRIVER per $PINBAL_SEYCON_DB_DRIVER"
	sed -i "s|PINBAL_SEYCON_DB_DRIVER|$PINBAL_SEYCON_DB_DRIVER|g" $DS_FILE
fi
if [[ -n "$PINBAL_SEYCON_DB_URL" ]]; then
	echo "Substituint PINBAL_SEYCON_DB_URL per $PINBAL_SEYCON_DB_URL"
	sed -i "s|PINBAL_SEYCON_DB_URL|$PINBAL_SEYCON_DB_URL|g" $DS_FILE
fi
if [[ -n "$PINBAL_SEYCON_DB_USERNAME" ]]; then
	echo "Substituint PINBAL_SEYCON_DB_USERNAME per $PINBAL_SEYCON_DB_USERNAME"
	sed -i "s/PINBAL_SEYCON_DB_USERNAME/$PINBAL_SEYCON_DB_USERNAME/g" $DS_FILE
fi
if [[ -n "$PINBAL_SEYCON_DB_PASSWORD" ]]; then
	echo "Substituint PINBAL_SEYCON_DB_PASSWORD per $PINBAL_SEYCON_DB_PASSWORD"
	sed -i "s/PINBAL_SEYCON_DB_PASSWORD/$PINBAL_SEYCON_DB_PASSWORD/g" $DS_FILE
fi

echo "Establint permisos per a poder accedir al volum de log"
su -c "chown default.default /opt/jboss/server/default/log" -

# echo "Configurant categories de log"
#sed -i 's#<root>#<category name="es.caib.pinbal.webapp.controller.ConsultaController"><priority value="TRACE" /></category><root>#g' $LOG_CONFIG_FILE

#echo "Substituint variables de properties"
#if [[ -n "$PINBAL_VERSION" ]]; then
#	echo "Substituint PINBAL_VERSION per $PINBAL_VERSION"
#	sed -i "s/PINBAL_VERSION/$PINBAL_VERSION/g" $SERVICE_FILE
#fi

