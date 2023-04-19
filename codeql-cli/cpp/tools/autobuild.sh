#!/bin/bash -xeu

# Directory where the autobuild scripts live.
export AUTOBUILD_ROOT="$CODEQL_EXTRACTOR_CPP_ROOT/tools"

# A temporary directory to store intermediate codeql artifacts.
export AUTOBUILD_TMP_DIR="$(mktemp -dt autobuild.XXXXX)"

# some default build environment settings

# Enables verbose build output for CMake >= 3.14
export VERBOSE=1
# disable ccache
export CCACHE_DISABLE=1

. "$AUTOBUILD_ROOT/lib/dirs.sh"
trap cleanup EXIT

"$AUTOBUILD_ROOT/do-prebuild"

"$AUTOBUILD_ROOT/do-build"
