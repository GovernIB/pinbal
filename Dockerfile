ARG BASE_IMAGE=docker.io/goib/jboss-eap72-openshift-base:latest

ARG BASE_PLATFORM=linux/amd64
FROM --platform=${BASE_PLATFORM} ${BASE_IMAGE}

LABEL org.opencontainers.image.source="https://github.com/GovernIB/pinbal"

WORKDIR /home/jboss

USER root

COPY pinbal-ear/src/main/docker/config/standalone-openshift.xml \
    $JBOSS_HOME/standalone/configuration/standalone-openshift.xml

#COPY pinbal-ear/src/main/docker/transformers/ \
#    $JBOSS_HOME/apps/pinbal/

COPY pinbal-ear/src/main/docker/config/pinbal/ \
    $JBOSS_HOME/apps/pinbal/

#COPY pinbal-ear/src/main/docker/oracle/ \
#    $JBOSS_HOME/modules/system/layers/base/com/oracle/main/

COPY pinbal-ear/target/pinbal.ear \
    $JBOSS_HOME/standalone/deployments/

RUN rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history \
    && mkdir -p $JBOSS_HOME/standalone/configuration/standalone_xml_history/current \
    && mkdir -p $JBOSS_HOME/standalone/data/content \
    && mkdir -p $JBOSS_HOME/standalone/tmp \
    && mkdir -p $JBOSS_HOME/standalone/log \
    && chmod -R g=u $JBOSS_HOME/standalone/data \
    && chmod -R g=u $JBOSS_HOME/standalone/tmp \
    && chmod -R g=u $JBOSS_HOME/standalone/log \
    && chmod -R g=u $JBOSS_HOME/standalone/configuration/standalone_xml_history \
    && test ! -d $JBOSS_HOME/config || chmod -R g=u $JBOSS_HOME/config \
    && mkdir -p /opt/eap/apps/pinbal \
    && chmod -R g=u /opt/eap/apps/pinbal \
    && mkdir -p /opt/webapps/pinbal \
    && chmod -R g=u /opt/webapps/pinbal

USER jboss

WORKDIR /home/jboss

EXPOSE 8080 8787
