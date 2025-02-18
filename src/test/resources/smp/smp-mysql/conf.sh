#!/usr/bin/env bash
# set -ex
echo "[INFO] inside conf.sh file"

# Create a smp_schema and an smp_dbuser with (all) privileges to the smp_schema.
mysql -h localhost -u root -p$MYSQL_ROOT_PASSWORD -e "drop schema if exists $MYSQL_DATABASE; create schema $MYSQL_DATABASE;alter database $MYSQL_DATABASE charset=utf8; create user $MYSQL_USER@localhost identified by '$MYSQL_PASSWORD'; grant all on $MYSQL_DATABASE.* to $MYSQL_USER@localhost;"
# Create the required objects (tables, etc.) in the database
mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /scripts/mysql5innodb.ddl

# Import configuration data...
mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /scripts/mysql5innodb-data.sql
# ... or data from backup
# mysql -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE < /var/lib/mysql/mysql-backup.sql

# Need some sort of cronjob to use mysqldump to backup $MYSQL_DATABASE and store the resulting file on the host machine as 'backup.sql'
# mysqldump -h localhost -u $MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE > /var/lib/mysql/mysql-backup.sql