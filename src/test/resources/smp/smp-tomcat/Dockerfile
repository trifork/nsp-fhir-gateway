FROM tomcat:8

# create folder for application
RUN mkdir $CATALINA_HOME/smp

# add smp folder to classpath
COPY setenv.sh $CATALINA_HOME/bin/setenv.sh

# tomcat-users.xml sets up user accounts for the Tomcat manager GUI
COPY tomcat-users.xml $CATALINA_HOME/conf/

# make manager app accessible
COPY manager.xml $CATALINA_HOME/conf/Catalina/localhost/manager.xml

# transfer truststore, keystore and private key
COPY smp-truststore.jks $CATALINA_HOME/smp/
COPY smp-keystore.jks $CATALINA_HOME/smp/
COPY encryptionPrivateKey.private $CATALINA_HOME/smp/

# MySQL driver jar
COPY mysql-connector-java-8.0.18.jar $CATALINA_HOME/lib/

# copy smp configuration into smp folder
COPY smp.config.properties $CATALINA_HOME/smp/smp.config.properties

# delete deafault tomcat root app
RUN rm -fr /usr/local/tomcat/webapps/ROOT
# deploy war application as ROOT
COPY smp-4.1.1.war $CATALINA_HOME/webapps/ROOT.war

COPY medcom-config.sh $CATALINA_HOME/smp/
COPY catalina_wrapper.sh $CATALINA_HOME/

# Need to wrap catalina.sh in order to spawn the medcom-config.sh data population script
CMD $CATALINA_HOME/catalina_wrapper.sh
