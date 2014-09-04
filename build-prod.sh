#!/bin/bash

cd `dirname $0`

rm -rf output;

mkdir -p output/webapp
mkdir -p output/etc
mkdir -p output/bin
mkdir -p output/lib

cp -r webapp output
cp -r etc output
cp -r bin output
cp -r lib output
cp output/etc/config-prod.properties output/etc/config.properties

mvn clean package
rm target/original*.jar
cp target/*.jar output/