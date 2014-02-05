#!/bin/sh

PROVIDER_VERSION="1.0.3-SNAPSHOT"

TEST_GROUP=core
while [ $# -gt 0 ]
do
  case "$1" in
    -host)
      shift
      HOST=$1;;
    -baseDn)
      shift
      BASE_DN=$1;;
    -bindDn)
      shift
      BIND_DN=$1;;
    -bindCredential)
      shift
      BIND_CREDENTIAL=$1;;
    -group)
      shift
      TEST_GROUP=$1;;
    -provider)
      shift
      PROVIDER=$1;;
  esac
  shift
done
SSL_HOST=`echo ${HOST} |sed 's/389/636/'`

MVN_CMD="clean test"
if [ ! -z "${PROVIDER}" ]; then
  MVN_CMD="-D${PROVIDER}.version=${PROVIDER_VERSION} ${MVN_CMD}"
fi

mvn \
  -DldapTestHost=ldap://${HOST} \
  -DldapSslTestHost=ldap://${SSL_HOST} \
  -DldapBaseDn="${BASE_DN}" \
  -DldapBindDn="${BIND_DN}" \
  -DldapBindCredential="${BIND_CREDENTIAL}" \
  -DldapTestGroup=${TEST_GROUP} \
  -DldapTestsIgnoreLock=true \
  ${MVN_CMD}

