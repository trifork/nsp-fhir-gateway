-- insert users
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (1, 'system', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SYSTEM_ADMIN', 1, NOW(), NOW());
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (2, 'smp', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SMP_ADMIN', 1, NOW(), NOW());
insert into SMP_USER (ID, USERNAME, PASSWORD, ROLE, ACTIVE, CREATED_ON, LAST_UPDATED_ON) values (3, 'user', '$2a$06$FDmjewn/do3C219uysNm9.XG8mIn.ubHnMydAzC8lsv61HsRpOR36', 'SERVICE_GROUP_ADMIN', 1, NOW(), NOW());
update SMP_USER_SEQ set next_val=4;

-- insert domain
insert into SMP_DOMAIN (ID, DOMAIN_CODE, SML_SUBDOMAIN, SML_SMP_ID, SIGNATURE_KEY_ALIAS, SML_BLUE_COAT_AUTH,SML_REGISTERED, CREATED_ON, LAST_UPDATED_ON) values (1, 'testDomain','test','CEF-SMP-002', 'sample_key', 1,0, NOW(), NOW());
update SMP_DOMAIN_SEQ set next_val=2;

# -- SMP CONFIGURATION TABLE FROM MYSQLDUMP (from 4/12/2019)
DROP TABLE IF EXISTS `SMP_CONFIGURATION`;
CREATE TABLE `SMP_CONFIGURATION` (
  `PROPERTY` varchar(512) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'Property name/key',
  `CREATED_ON` datetime NOT NULL COMMENT 'Row inserted on date',
  `DESCRIPTION` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'Property description',
  `LAST_UPDATED_ON` datetime NOT NULL COMMENT 'Row modified on date',
  `VALUE` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'Property value',
  PRIMARY KEY (`PROPERTY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='SMP user certificates';
LOCK TABLES `SMP_CONFIGURATION` WRITE;
INSERT INTO `SMP_CONFIGURATION` VALUES ('authentication.blueCoat.enabled','2019-12-04 10:29:52','Authentication with Blue Coat means that all HTTP requests having \'Client-Cert\' header will be authenticated as username placed in the header.Never expose SMP to the WEB without properly configured reverse-proxy and active blue coat.','2019-12-04 10:29:52','false'),
('bdmsl.integration.enabled','2019-12-04 10:29:52','BDMSL (SML) integration ON/OFF switch','2019-12-04 10:29:52','false'),
('bdmsl.integration.logical.address','2019-12-04 10:29:52','Logical SMP endpoint which will be registered on SML when registering new domain','2019-12-04 10:29:52','http://dk.acc.edelivery.tech.ec.europa.eu'),
('bdmsl.integration.physical.address','2019-12-04 10:29:52','Physical SMP endpoint which will be registered on SML when registering new domain.','2019-12-04 10:29:52','127.0.0.1'),
('bdmsl.integration.tls.disableCNCheck','2019-12-04 10:29:52','If SML Url is HTTPs - Disable CN check if needed.','2019-12-04 10:29:52','false'),
('bdmsl.integration.tls.serverSubjectRegex','2019-12-04 10:29:52','Regular expression for server TLS certificate subject verification  CertEx. .*CN=acc.edelivery.tech.ec.europa.eu.*.','2019-12-04 10:29:52','.*'),
('bdmsl.integration.url','2019-12-04 10:29:52','BDMSL (SML) endpoint','2019-12-04 10:29:52','https://acc.edelivery.tech.ec.europa.eu/edelivery-sml/'),
('bdmsl.participant.multidomain.enabled','2019-12-04 10:29:52','Set to true if SML support participant on multidomain','2019-12-04 10:29:52','false'),
('configuration.dir','2019-12-04 10:29:50','Path to the folder containing all the configuration files (keystore and encryption key)','2019-12-04 10:29:50','/usr/local/tomcat/smp'),
('contextPath.output','2019-12-04 10:29:52','This property controls pattern of URLs produced by SMP in GET ServiceGroup responses.','2019-12-04 10:29:52','true'),
('encryption.key.filename','2019-12-04 10:29:50','Key filename to encrypt passwords','2019-12-04 10:29:50','encryptionPrivateKey.private'),
('identifiersBehaviour.ParticipantIdentifierScheme.validationRegex','2019-12-04 10:29:52','Participant Identifier Schema of each PUT ServiceGroup request is validated against this schema.','2019-12-04 10:29:52','^((?!^.{26})([a-z0-9]+-[a-z0-9]+-[a-z0-9]+)|urn:oasis:names:tc:ebcore:partyid-type:(iso6523|unregistered)(:.+)?$)'),
('identifiersBehaviour.ParticipantIdentifierScheme.validationRegexMessage','2019-12-04 10:29:52','Error message for UI','2019-12-04 10:29:52','Participant scheme must start with:urn:oasis:names:tc:ebcore:partyid-type:(iso6523:|unregistered:) OR must be up to 25 characters long with form [domain]-[identifierArea]-[identifierType] (ex.: \'busdox-actorid-upis\') and may only contain the following characters: [a-z0-9].'),
('identifiersBehaviour.caseSensitive.DocumentIdentifierSchemes','2019-12-04 10:29:52','Specifies schemes of document identifiers that must be considered CASE-SENSITIVE.','2019-12-04 10:29:52','casesensitive-doc-scheme1|casesensitive-doc-scheme2'),
('identifiersBehaviour.caseSensitive.ParticipantIdentifierSchemes','2019-12-04 10:29:52','Specifies schemes of participant identifiers that must be considered CASE-SENSITIVE.','2019-12-04 10:29:52','sensitive-participant-sc1|sensitive-participant-sc2'),
('smp.certificate.crl.force','2019-12-04 10:29:52','If false then if CRL is not reachable ignore CRL validation','2019-12-04 10:29:52','false'),
('smp.keystore.filename','2019-12-04 10:29:50','Keystore filename ','2019-12-04 10:29:50','smp-keystore.jks'),
('smp.keystore.password','2019-12-04 10:29:50','Encrypted keystore (and keys) password ','2019-12-04 10:29:50','cGQPezsYUEL3atiaBr6SYAPC5fsJS6e6iOecMDSb3nY='),
('smp.noproxy.hosts','2019-12-04 10:29:52','list of nor proxy hosts. Ex.: localhost|127.0.0.1','2019-12-04 10:29:52','localhost|127.0.0.1'),
('smp.property.refresh.cronJobExpression','2019-12-04 10:29:52','Property refresh cron expression (def 12 minutes to each hour). Property change is refreshed at restart!','2019-12-04 10:29:52','0 0/2 * * * *'),
('smp.proxy.host','2019-12-04 10:29:52','The http proxy host','2019-12-04 10:29:52',''),
('smp.proxy.password','2019-12-04 10:29:52','Base64 encrypted password for Proxy.','2019-12-04 10:29:52',''),
('smp.proxy.port','2019-12-04 10:29:52','The http proxy port','2019-12-04 10:29:52','80'),
('smp.proxy.user','2019-12-04 10:29:52','The proxy user','2019-12-04 10:29:52',''),
('smp.truststore.filename','2019-12-04 10:29:50','Truststore filename ','2019-12-04 10:29:50','smp-truststore.jks'),
('smp.truststore.password','2019-12-04 10:29:50','Encrypted truststore password ','2019-12-04 10:29:50','D2Z5GjQQYSurB4DYWLKyExD5NA1S1UM7d651po1tork=');
UNLOCK TABLES;