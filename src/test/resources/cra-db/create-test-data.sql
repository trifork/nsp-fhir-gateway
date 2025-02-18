USE cra;

-- Testdata
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest7.trust2408.com/systemtest7.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest8.trust2408.com/systemtest8.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest9.trust2408.com/systemtest9.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest10.trust2408.com/systemtest10.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest17.trust2408.com/systemtest17.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest19.trust2408.com/systemtest19.crl', unix_timestamp()*1000, 2408449693000);
INSERT INTO `crl` (url, lastmodified, nextupdate) VALUES ('http://crl.systemtest22.trust2408.com/systemtest221.crl', unix_timestamp()*1000, 2408449693000);

-- oces2/PP/MOCES_spaerret.p12
INSERT INTO `revoked` (crlid, serialnumber, added, since) VALUES ((SELECT id FROM crl WHERE url = 'http://crl.systemtest19.trust2408.com/systemtest19.crl'), 1478025820, unix_timestamp()*1000, unix_timestamp()*1000);
-- oces2/PP/VOCES_spaerret.p12
INSERT INTO `revoked` (crlid, serialnumber, added, since) VALUES ((SELECT id FROM crl WHERE url = 'http://crl.systemtest19.trust2408.com/systemtest19.crl'), 1478025848, unix_timestamp()*1000, unix_timestamp()*1000);
-- oces2/PP/FOCES_spaerret.p12
INSERT INTO `revoked` (crlid, serialnumber, added, since) VALUES ((SELECT id FROM crl WHERE url = 'http://crl.systemtest19.trust2408.com/systemtest19.crl'), 1478025854, unix_timestamp()*1000, unix_timestamp()*1000);