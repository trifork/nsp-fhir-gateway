#!/bin/sh

revision=$(git rev-parse --short HEAD)

version=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version | grep -v "\[")

tag="trifork/medcom-gateway:${version}-${revision}"

echo "Building and pushing ${tag}"

docker build --tag "${tag}" .

docker tag "${tag}" trifork/medcom-gateway:latest

docker push "${tag}"
docker push trifork/medcom-gateway:latest
