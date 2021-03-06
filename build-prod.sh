#!/bin/bash

cd `dirname $0`

rm -rf output;

mkdir -p output/webapp
mkdir -p output/etc
mkdir -p output/bin
mkdir -p output/lib
mkdir -p output/libexec
if [ ! -f /var/log/woogle ]; then
    sudo mkdir -p /var/log/woogle
fi
mkdir -p output/pid

cp -r webapp output
cp -r etc output
cp -r bin output
cp -r lib output
cp -r libexec output
cp output/etc/config-prod.properties output/etc/config.properties

mvn clean package
rm target/original*.jar
cp target/*.jar output/