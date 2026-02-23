FROM 280421/jb5caib
ENV PINBAL_DB_DRIVER=oracle.jdbc.driver.OracleDriver
ENV PINBAL_DB_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV PINBAL_DB_USERNAME pinbaldk
ENV PINBAL_DB_PASSWORD pinbaldk
# ENV PINBAL_DB_URL jdbc:oracle:thin:@host.docker.internal:49164:xe
# ENV PINBAL_DB_USERNAME pinbal
# ENV PINBAL_DB_PASSWORD pinbal
ENV PINBAL_SEYCON_DB_DRIVER=oracle.jdbc.driver.OracleDriver
ENV PINBAL_SEYCON_DB_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV PINBAL_SEYCON_DB_USERNAME seycon
ENV PINBAL_SEYCON_DB_PASSWORD seycon
# ENV PINBAL_SEYCON_DB_URL jdbc:oracle:thin:@host.docker.internal:49161:xe
# ENV PINBAL_SEYCON_DB_USERNAME seycon
# ENV PINBAL_SEYCON_DB_PASSWORD seycon

# Per usar el mock simple sense BBDD:
# ENV JAVA_OPTS="-Xms1303m -Xmx1303m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dsun.lang.ClassLoader.allowArraySyntax=true -Djavax.net.ssl.trustStore=/opt/webapps/truststore.jks -Djavax.net.ssl.trustStorePassword=tecnologies -Dspring.profiles.active=mock-scsp"
# Per usar el mock simple amb BBDD:
# ENV JAVA_OPTS="-Xms1303m -Xmx1303m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dsun.lang.ClassLoader.allowArraySyntax=true -Djavax.net.ssl.trustStore=/opt/webapps/truststore.jks -Djavax.net.ssl.trustStorePassword=tecnologies -Dspring.profiles.active=mock-scsp-db"
# Per desactivar tots els mocks (usar el ClienteUnico real):
ENV JAVA_OPTS="-Xms1303m -Xmx1303m -XX:MaxPermSize=256m -Dorg.jboss.resolver.warning=true -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000 -Dsun.lang.ClassLoader.allowArraySyntax=true -Djavax.net.ssl.trustStore=/opt/webapps/truststore.jks -Djavax.net.ssl.trustStorePassword=tecnologies"

EXPOSE 8080
EXPOSE 8787
ADD --chown=default:default deploycaib/ /opt/jboss/server/default/deploycaib/
ADD --chown=default:default lib/ /opt/jboss/server/default/lib/
ADD --chown=default:default config/ /opt/webapps/
RUN chmod +x /opt/webapps/env_init.sh
COPY --chown=default:default jboss_conf/jboss-log4j.xml /opt/jboss/server/default/conf/
COPY --chown=default:default deploy/seycon-ds.xml /opt/jboss/server/default/deploy/
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss/server/default/deploycaib/${project.build.finalName}.ear
RUN rm /opt/jboss/server/default/lib/bcprov-jdk16.jar
WORKDIR /opt/jboss
USER default
ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh", "$JAVA_OPTS" ]