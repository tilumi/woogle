#/bin/bash

yum update
yum install -y curl
yum install -y wget
yum install -y vim
yum install -y git
yum install -y sqlite-devel

wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u20-b26/jdk-8u20-linux-x64.rpm
rpm -Uvh jdk-8u20-linux-x64.rpm
alternatives --install /usr/bin/java java /usr/java/latest/jre/bin/java 200000
alternatives --install /usr/bin/jps jps /usr/java/latest/bin/jps 200000

wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
yum  -y  install apache-maven
ln  -s   /usr/share/apache-maven/bin/mvn  /usr/bin/mvn
echo 'export JAVA_HOME=/usr/java/latest/' > ~/.mavenrc

curl -L get.rvm.io | bash -s stable
source /etc/profile.d/rvm.sh
rvm requirements
rvm cron setup