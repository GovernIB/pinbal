FROM 280421/jb5caib
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