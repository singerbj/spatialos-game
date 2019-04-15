# !/usr/bin/env bash

# This script builds the JavaWorker executable to bin
echo "========================="
echo "Building JavaWorker......"
echo "========================="

# Download the dependenties in case they are not present
if [ ! -d "lib" ]; then
  ../SpatialOS/scripts/download_dependencies.sh
fi

./gradlew jar
if [ $? -ne 0 ]; then
    echo "========================="
    echo "JavaWorker Build FAIL!!!!"
    echo "========================="
    exit 666
fi

echo "========================="
echo "JavaWorker Build Complete"
echo "========================="
