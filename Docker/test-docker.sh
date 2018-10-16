#!/bin/bash

set -e
cp -p "../target/$(ls -t ../target/*.jar | grep -v /orig | head -1)" app.jar
docker build -t microservice-baseline .
docker run -it --rm -p 8080:8080 microservice-baseline