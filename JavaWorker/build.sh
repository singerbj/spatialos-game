# !/usr/bin/env bash

# This script builds the JavaWorker executable to bin
echo "========================="
echo "Building JavaWorker......"
echo "========================="

# Download the dependenties in case they are not present
../SpatialOS/scripts/download_dependencies.sh

OUT_DIR="$(pwd)/src/generated/java/"
rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"


# Why the f arent these set anymore?
SDK_VERSION="13.5.1"
TOOLS_DIR="$(pwd)/../SpatialOS/tools/${SDK_VERSION}"
SCHEMA_DIR="$(pwd)/../SpatialOS/schema"

"${TOOLS_DIR}"/schema_compiler/schema_compiler \
  --schema_path="${SCHEMA_DIR}" \
  --java_out="${OUT_DIR}" \
  --load_all_schema_on_schema_path \
  "${SCHEMA_DIR}"/*.schema \
  "${SCHEMA_DIR}"/improbable/*.schema

./gradlew buildJar
if [ $? -ne 0 ]; then
    echo "========================="
    echo "JavaWorker Build FAIL!!!!"
    echo "========================="
    exit 666
fi

echo "========================="
echo "JavaWorker Build Complete"
echo "========================="
