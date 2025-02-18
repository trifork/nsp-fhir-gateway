## Procedure for generating keys/certificates/keystores for local test setup (for EndToEndIT.java)

Important: use password "Test1234" everywhere.

### Generate key and certificate (use req.cnf to create x509 v3 certificates - Domibus doesn't allow v1):

    openssl req -x509 -newkey rsa:4096 -keyout domibus-gateway-key.pem -out domibus-gateway-cert.pem -days 2000 -config req-gw.cnf
    openssl req -x509 -newkey rsa:4096 -keyout ap1-key.pem -out ap1-cert.pem -days 2000 -config req-ap1.cnf
    openssl req -x509 -newkey rsa:4096 -keyout ap2-key.pem -out ap2-cert.pem -days 2000 -config req-ap2.cnf

### Create p12

    openssl pkcs12 -export -out domibus-gateway.p12 -inkey domibus-gateway-key.pem -in domibus-gateway-cert.pem -name domibus-gateway
    openssl pkcs12 -export -out ap1.p12 -inkey ap1-key.pem -in ap1-cert.pem -name ap1
    openssl pkcs12 -export -out ap2.p12 -inkey ap2-key.pem -in ap2-cert.pem -name ap2

### Create jks keystores for the individual APs (each instance has its own keystore with its own private key):

    keytool -importkeystore -srckeystore domibus-gateway.p12 -srcstoretype pkcs12 -srcalias domibus-gateway -destkeystore gateway-ks.jks -deststoretype jks -deststorepass Test1234 -destalias domibus-gateway
    keytool -importkeystore -srckeystore ap1.p12 -srcstoretype pkcs12 -srcalias ap1 -destkeystore ap1-ks.jks -deststoretype jks -deststorepass Test1234 -destalias ap1
    keytool -importkeystore -srckeystore ap2.p12 -srcstoretype pkcs12 -srcalias ap2 -destkeystore ap2-ks.jks -deststoretype jks -deststorepass Test1234 -destalias ap2

### Extract public certificates from keystores (because we need only the public part in the truststores):

    keytool -export -keystore gateway-ks.jks -alias domibus-gateway -file domibus-gateway.cer
    keytool -export -keystore ap1-ks.jks -alias ap1 -file ap1.cer
    keytool -export -keystore ap2-ks.jks -alias ap2 -file ap2.cer

### Create truststore with the right certificates (use truststore_smp_only.jks as a starting point):

    cp truststore_smp_only.jks truststore.jks
    keytool -importcert -file "domibus-gateway.cer" -keystore truststore.jks -alias "domibus-gateway"
    keytool -importcert -file "ap1.cer" -keystore truststore.jks -alias "ap1"
    keytool -importcert -file "ap2.cer" -keystore truststore.jks -alias "ap2"

### Make sure the keystores are of type JKS (although the above commands say "-deststoretype jks", it doesn't seem to do the trick)

    keytool -importkeystore -srckeystore ap1-ks.jks -destkeystore ap1-ks.jks -deststoretype jks
    keytool -importkeystore -srckeystore ap2-ks.jks -destkeystore ap2-ks.jks -deststoretype jks
    keytool -importkeystore -srckeystore gateway-ks.jks -destkeystore gateway-ks.jks -deststoretype jks
    keytool -importkeystore -srckeystore truststore.jks -destkeystore truststore.jks -deststoretype jks

### Use generated JKS keystores in it-config

    cp ap1-ks.jks ../../it-config/
    cp ap2-ks.jks ../../it-config/
    cp gateway-ks.jks ../../it-config/
    cp truststore.jks ../../it-config/

### Update SMP configuration

    Add contents from ap1-cert.pem, ap2-cert.pem, and domibus-gatewaycert.pem to relevant variables in SMPGenerator.java
    Run main method in SMPGenerator to create a new medcom-config.sh
    Verify setup by runnint EndToEndIT.java

