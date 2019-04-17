#!/usr/bin/env bash

# This script retrieves the project dependencies from the SpatialOS SDK

set -e -x
pushd "$( dirname "${BASH_SOURCE[0]}" )"
source ./utils.sh

retrievePackage() {
  TYPE=$1
  PACKAGE=$2
  TARGETDIR=$3
  if [ ! -d "${TARGETDIR}" ]; then
    echo '========================================='
    echo "TYPE=${TYPE}"
    echo "PACKAGE=${PACKAGE}"
    echo "SDK_VERSION=${SDK_VERSION}"
    echo "TARGETDIR=${TARGETDIR}"
    echo '========================================='
    echo 'package get --force --unzip "${TYPE}" "${PACKAGE}" "${SDK_VERSION}" "${TARGETDIR}"'
    spatial package get --force --unzip "${TYPE}" "${PACKAGE}" "${SDK_VERSION}" "${TARGETDIR}"
    echo '========================================='
    echo '========================================='
    echo '========================================='
  fi
}

# Download and unzip the project dependencies from the SpatialOS SDK:
# * Schema compiler (tools folder): Generates component API code for workers and the schema descriptor
# * Snapshot converter (tools folder): Convert the format of a snapshot file from binary to text or vice versa, not needed for building a project
# * C# SDK library (lib folder): Functionalities for letting a program participate in a SpatialOS simulation as a worker
# * Core SDK library for Windows, MacOs and Linux (lib folder): Functionalities for letting a program participate in a SpatialOS simulation as a worker
# * Standard schema library (schema/improbable folder): Common SpatialOS component definitions

mkdir -p "${TOOLS_DIR}"
pushd "${TOOLS_DIR}"
retrievePackage "tools" "schema_compiler-x86_64-${PLATFORM_NAME}" "schema_compiler"
retrievePackage "tools" "snapshot_converter-x86_64-${PLATFORM_NAME}" "snapshot_converter"
popd

mkdir -p "${LIB_DIR}"
pushd "${LIB_DIR}"
retrievePackage "worker_sdk" "java" "java"
retrievePackage "worker_sdk" "csharp" "csharp"
retrievePackage "worker_sdk" "core-dynamic-x86_64-win32" "core-dynamic-win64"
retrievePackage "worker_sdk" "core-dynamic-x86_64-macos" "core-dynamic-macos64"
retrievePackage "worker_sdk" "core-dynamic-x86_64-linux" "core-dynamic-linux64"
retrievePackage "worker_sdk" "java_native-dynamic-x86_64-win32" "java_native-dynamic-win64"
retrievePackage "worker_sdk" "java_native-dynamic-x86_64-macos" "java_native-dynamic-macos64"
retrievePackage "worker_sdk" "java_native-dynamic-x86_64-linux" "java_native-dynamic-linux64"

# Move the Worker SDK libraries into the lib folder of each worker project
for WORKER_DIR in "${WORKER_DIRS[@]}"; do
  mkdir -p "${WORKER_DIR}/lib/improbable/sdk/${SDK_VERSION}"
  cp -r "${LIB_DIR}" "${WORKER_DIR}/lib/improbable/sdk"
done
popd

pushd "${SCHEMA_DIR}"
retrievePackage "schema" "standard_library" ""
popd

popd
