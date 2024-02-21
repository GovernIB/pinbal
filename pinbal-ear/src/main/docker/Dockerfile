FROM 280421/jb5caib
ENV PINBAL_DB_DRIVER=oracle.jdbc.driver.OracleDriver
ENV PINBAL_DB_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV PINBAL_DB_USERNAME pinbaldk
ENV PINBAL_DB_PASSWORD pinbaldk
#ENV PINBAL_DB_URL jdbc:oracle:thin:@host.docker.internal:49161:xe
#ENV PINBAL_DB_USERNAME pinbal
#ENV PINBAL_DB_PASSWORD pinbal
ENV PINBAL_SEYCON_DB_DRIVER=oracle.jdbc.driver.OracleDriver
ENV PINBAL_SEYCON_DB_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV PINBAL_SEYCON_DB_USERNAME seycon
ENV PINBAL_SEYCON_DB_PASSWORD seycon
#ENV PINBAL_SEYCON_DB_URL jdbc:oracle:thin:@host.docker.internal:49161:xe
#ENV PINBAL_SEYCON_DB_USERNAME seycon
#ENV PINBAL_SEYCON_DB_PASSWORD seycon
EXPOSE 8080
EXPOSE 8787
ADD --chown=default:default deploycaib/ /opt/jboss/server/default/deploycaib/
ADD --chown=default:default lib/ /opt/jboss/server/default/lib/
ADD --chown=default:default config/ /opt/webapps/
RUN chmod +x /opt/webapps/env_init.sh
COPY --chown=default:default jboss_conf/jboss-log4j.xml /opt/jboss/server/default/conf/
#COPY --chown=default:default deploy/seycon-ds.xml /opt/jboss/server/default/deploy/
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss/server/default/deploycaib/${project.build.finalName}.ear
RUN rm /opt/jboss/server/default/lib/bcprov-jdk16.jar
WORKDIR /opt/jboss
USER default
ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]