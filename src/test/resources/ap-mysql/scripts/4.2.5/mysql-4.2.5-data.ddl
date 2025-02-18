--  *********************************************************************
--  Update Database Script
--  *********************************************************************
--  Change Log: src/main/resources/db/changelog-data.xml
--  Ran at: 10/28/21 11:29 AM
--  Against: null@offline:mysql?changeLogFile=/Users/cosmin/IdeaProjects/domibus415/Domibus-MSH-db/target/liquibase/changelog-4.2.5-data.mysql
--  Liquibase version: 3.5.3
--  *********************************************************************

--  Changeset src/main/resources/db/changelog-data.xml::EDELIVERY-2144::thomas dussart
--  inserts
INSERT INTO TB_USER (ID_PK, USER_NAME, USER_PASSWORD, USER_ENABLED, USER_DELETED, DEFAULT_PASSWORD) VALUES ('1', 'admin', '$2a$10$5uKS72xK2ArGDgb2CwjYnOzQcOmB7CPxK6fz2MGcDBM9vJ4rUql36', 1, 0, 1);

INSERT INTO TB_USER (ID_PK, USER_NAME, USER_PASSWORD, USER_ENABLED, USER_DELETED, DEFAULT_PASSWORD) VALUES ('2', 'user', '$2a$10$HApapHvDStTEwjjneMCvxuqUKVyycXZRfXMwjU0rRmaWMsjWQp/Zu', 1, 0, 1);

INSERT INTO TB_USER_ROLES (USER_ID, ROLE_ID) VALUES ('1', '1');

INSERT INTO TB_USER_ROLES (USER_ID, ROLE_ID) VALUES ('2', '2');

--  Changeset src/main/resources/db/changelog-data.xml::insert_ws_default_auth::idragusa
INSERT INTO TB_AUTHENTICATION_ENTRY (USERNAME, PASSWD, AUTH_ROLES, DEFAULT_PASSWORD, PASSWORD_CHANGE_DATE) VALUES ('admin', '$2a$10$5uKS72xK2ArGDgb2CwjYnOzQcOmB7CPxK6fz2MGcDBM9vJ4rUql36', 'ROLE_ADMIN', 1, NOW());

INSERT INTO TB_AUTHENTICATION_ENTRY (USERNAME, PASSWD, AUTH_ROLES, ORIGINAL_USER, DEFAULT_PASSWORD, PASSWORD_CHANGE_DATE) VALUES ('user', '$2a$10$HApapHvDStTEwjjneMCvxuqUKVyycXZRfXMwjU0rRmaWMsjWQp/Zu', 'ROLE_USER', 'urn:oasis:names:tc:ebcore:partyid-type:unregistered:C1', 1, NOW());

INSERT INTO TB_AUTHENTICATION_ENTRY (CERTIFICATE_ID, AUTH_ROLES) VALUES ('CN=blue_gw,O=eDelivery,C=BE:10370035830817850458', 'ROLE_ADMIN');
