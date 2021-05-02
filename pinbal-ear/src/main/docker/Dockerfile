FROM 280421/jb5caib
ENV DATABASE_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV DATABASE_USERNAME pinbaldk
ENV DATABASE_PASSWORD pinbaldk
ENV SEYCON_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV SEYCON_USERNAME seycon
ENV SEYCON_PASSWORD seycon
EXPOSE 8080
EXPOSE 8787
ADD --chown=default:default deploycaib/ /opt/jboss/server/default/deploycaib/
ADD --chown=default:default lib/ /opt/jboss/server/default/lib/
ADD --chown=default:default config/ /opt/webapps/
RUN chmod +x /opt/webapps/env_init.sh
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss/server/default/deploycaib/${project.build.finalName}.ear
RUN rm /opt/jboss/server/default/lib/bcprov-jdk16.jar
WORKDIR /opt/jboss
USER default
ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]