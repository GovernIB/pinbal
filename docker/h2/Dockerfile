# Minimal H2 TCP server image used only for the e2e flow (scripts/e2e/run-e2e.sh).
#
# IMPORTANT: the H2 server here MUST be the *exact same build* JBoss EAP 7.2
# ships as its "com.h2database.h2" module (h2-1.4.193.redhat-2.jar), not a
# modern H2 version. H2's TCP wire protocol is versioned and NOT compatible
# across major releases: JBoss's bundled 1.4.193 driver speaks protocol
# version 15, while e.g. H2 2.1.214 speaks version 17, and the connection is
# rejected outright ("Version mismatch, driver version is 15 but server
# version is 17"). Since the JDBC *driver* side is baked into the EAP
# distribution (not something we control from docker-compose), the server
# must match it instead — so this image extracts the real jar straight out
# of the same base image the app itself runs on, guaranteeing byte-for-byte
# protocol compatibility, rather than resolving a version number from Maven.
#
# (This is deliberately a DIFFERENT H2 version than the one pinned in
# pinbal-persistence/pom.xml for standalone unit tests — that one only talks
# to itself in-process and never goes over this TCP protocol, so it's free
# to stay on a modern H2 release.)

ARG BASE_IMAGE=docker.io/goib/jboss-eap72-openshift-base:latest

FROM --platform=linux/amd64 ${BASE_IMAGE} AS jboss-base

FROM docker.io/library/eclipse-temurin:11-jre
COPY --from=jboss-base /opt/eap/modules/system/layers/base/com/h2database/h2/main/h2-1.4.193.redhat-2.jar /opt/h2/h2.jar

EXPOSE 9092

# -ifNotExists no existeix a H2 1.4.193 (afegit en versions posteriors); no cal:
# el servidor TCP ja permet crear una BD en memòria nova en connectar-hi per
# defecte (nomes "-ifExists" ho restringiria, i no l'usem).
ENTRYPOINT ["java", "-cp", "/opt/h2/h2.jar", "org.h2.tools.Server", \
            "-tcp", "-tcpAllowOthers", "-tcpPort", "9092"]
