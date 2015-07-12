#!/bin/sh

## VARS
url="192.168.1.4"

NOW=$(date +"%m%d%Y-%H%M%S")

curl --compressed -X GET http://$url:5984/panstamp/_all_docs?include_docs=true | gzip -9 > "backup/panstamp.$NOW.tar.gz"

curl --compressed -X GET http://$url:5984/panstamp_packets/_all_docs?include_docs=true | gzip -9 > "backup/panstamp_packets.$NOW.tar.gz"

curl --compressed -X GET http://$url:5984/panstamp_events/_all_docs?include_docs=true | gzip -9 > "backup/panstamp_events.$NOW.tar.gz"

curl --compressed -X GET http://$url:5984/events/_all_docs?include_docs=true | gzip -9 > "backup/events.$NOW.tar.gz"
