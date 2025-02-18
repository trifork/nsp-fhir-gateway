FROM mariadb:10.1

#COPY init.sql /docker-entrypoint-initdb.d/init.sql
COPY drop-create-db.sql /docker-entrypoint-initdb.d/01_drop-create-db.sql
COPY create-test-data.sql /docker-entrypoint-initdb.d/02_create-test-data.sql