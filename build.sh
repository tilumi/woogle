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

echo 'export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_05.jdk/Contents/Home' >> ~/.mavenrc
mvn clean package
rm ~/.mavenrc
rm target/original*.jar
cp target/*.jar output/