FROM mysql:8.0

RUN mkdir -p /scripts
WORKDIR /scripts

# Copy the database initialize script: 
COPY mysql5innodb.ddl /scripts/
COPY mysql5innodb-data.sql /scripts/
COPY conf.sh /docker-entrypoint-initdb.d/

VOLUME ./shared:/var/lib/mysql
