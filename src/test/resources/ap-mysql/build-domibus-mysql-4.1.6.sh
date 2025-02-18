#!/bin/sh

revision=$(git rev-parse --short HEAD)

version=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v "\[")

tag="trifork/domibus-mysql-4.1.6:${version}-${revision}"

echo "Building and pushing ${tag}"

docker build --tag "${tag}" -f Dockerfile-4.1.6 .

docker tag "${tag}" trifork/domibus-mysql-4.1.6:latest

docker push "${tag}"
docker push trifork/domibus-mysql-4.1.6:latest
