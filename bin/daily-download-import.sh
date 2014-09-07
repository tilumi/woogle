#!/bin/bash

cd `dirname $0`/..

bin/woogle download 

source etc/config-default.properties
source etc/config.properties
bin/woogle import $WORD_DIR /tmp/HtmlOutput false