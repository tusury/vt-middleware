#!/bin/sh

PROVIDER_VERSION="1.0-SNAPSHOT"

vflag=off
HOST="ldap-test-1.middleware.vt.edu"
TEST_GROUP=core
while [ $# -gt 0 ]
do
  case "$1" in
    -host)
      shift
      HOST=$1;;
    -group)
      shift
      TEST_GROUP=$1;;
    -provider)
      shift
      PROVIDER=$1;;
  esac
  shift
done

MVN_CMD="clean test"
if [ ! -z "${PROVIDER}" ]; then
  MVN_CMD="-D${PROVIDER}.version=${PROVIDER_VERSION} ${MVN_CMD}"
fi

mvn \
  -DldapTestHost=ldap://${HOST}:10389 \
  -DldapSslTestHost=ldap://${HOST}:10636 \
  -DldapTestGroup=${TEST_GROUP} \
  -DldapTestsIgnoreLock=true \
  ${MVN_CMD}

