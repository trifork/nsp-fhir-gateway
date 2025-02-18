# MEDCOM EDELIVERY GATEWAY

## Building

Initialize (first time only):
`mvn validate`

package:
`mvn clean package -DskipTests`

docker build:
`docker build --tag trifork/medcom-gateway:<tag> .`

docker run:
`docker run -p 8080:8080 --name gw trifork/medcom-gateway:<tag>` 

build and push to dockerhub

`./build-docker.sh`

## Releasing

Verify that unit tests run using `mvn clean install`.

Verify that the integration test is successful: Run EndToEndIT.java which spawns 
local test-container setup including all edelivery components.

Optionally: toggle breakpoints to inspect SMP configuration, in-flight messages in Domibus console etc.

Upload image to docker hub using the `build-docker.sh` script (Notice: may fail on first run until required
maven dependencies are downloaded). Fetch the new build tag from `https://hub.docker.com/r/trifork/medcom-gateway/tags` 
and update the Kubernetes configuration.

Sync to KIT environment on https://login.vconf-test.dk/auth/realms/deployment/protocol/openid-connect/auth?client_id=argocd

(Login using "External deployment")

After releasing it is good practice to execute the `KitEnvIT`, which also verifies DDS integration (that metadata is
registered correctly, and that the stored document matches the one sent to the gateway).

## KIT env setup

Resources:
* Kubernetes configuration: https://github.com/medcomdk/nsp-edelivery-k8s-services
* Argo management: https://kube-argo.vconf-test.dk/applications (login as "External")

Domibus instances are located here:

* DDS: https://ap-dds.nsptest.medcom.dk/domibus/
* Gateway: https://ap-gateway.nsptest.medcom.dk/domibus/

Notice that Domibus instances have been seen to lose their configuration from time to time if they are sync'ed
using Argo management. In such cases, they will need to have uploaded pmode at truststore manually. The currently
valid pmode is listed in `src/test/resources/ap-tomcat/pmodes/README.md`, and the required truststore is in 
`src/test/resources/ap-tomcat/domibus/keystores/cert_truststore_smp_and_intermediate2.jks` (pwd '1'). Also, nagivate
to "Message Filter" in the ap-domibus console and press "Save" in order to enable the JMS plugin.

After having configured both Domibus instances, the configuration can be verified by sending the message in
src/test/resources/ap-tomcat/test-messages/dds-to-gateway-2021-06-23.xml to the dds endpoint. This message should
be forwarded to ap-gateway, and a response should arrive a few minutes after.
