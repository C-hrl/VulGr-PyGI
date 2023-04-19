#!/bin/bash -eu

# emit json diagnostics taking data from a file named after the ID provided as first argument
# following arguments are interpolated in the format string defined by MARKDOWN_MESSAGE or
# PLAINTEXT_MESSAGE in the file itself

ID=$1
DATA="$(dirname "$0")/$ID"

shift

if [[ ! -f "$DATA" ]]; then
  echo "No data file found for diagnostic $ID" >&2
  exit 1
fi

# shellcheck source=/dev/null
source "$DATA"

mkdir -p "$CODEQL_EXTRACTOR_CPP_DIAGNOSTIC_DIR"

echo "cpp/autobuilder: $SOURCE_NAME." >&2

exec >> "$CODEQL_EXTRACTOR_CPP_DIAGNOSTIC_DIR/autobuilder-$$.jsonl"

echo "{"
echo "  \"timestamp\": \"$(date -u +"%Y-%m-%dT%TZ")\","
echo "  \"source\": {"
echo "    \"id\": \"$SOURCE_ID\","
echo "    \"name\": \"$SOURCE_NAME\","
echo "    \"extractorName\": \"cpp\""
echo "  },"
echo "  \"severity\": \"$SEVERITY\","
# replace newlines in messages with literal '\n' using `${X//$'\n'/\\\\n}`
# replacement `\n` need to be doubly escaped because they are used in `printf`
# shellcheck disable=SC2059
if [[ -n "$MARKDOWN_MESSAGE" ]]; then
  printf "  \"markdownMessage\": \"${MARKDOWN_MESSAGE//$'\n'/\\\\n}\",\n" "$@"
elif [[ -n "$PLAINTEXT_MESSAGE" ]]; then
  printf "  \"plaintextMessage\": \"${PLAINTEXT_MESSAGE//$'\n'/\\\\n}\",\n" "$@"
fi
if [[ -n "${HELP_LINKS:-}" ]]; then
  echo "  \"helpLinks\": ["
  echo -n "    \"${HELP_LINKS[0]}\""
  for link in "${HELP_LINKS[@]:1}"; do
    echo -en ",\n    \"$link\""
  done
  echo
  echo "  ],"
fi
echo "  \"visibility\": {"
echo "    \"statusPage\": ${STATUS_PAGE:-true},"
echo "    \"cliSummaryTable\": ${CLI_SUMMARY_TABLE:-true},"
echo "    \"telemetry\": ${TELEMETRY:-true}"
echo "  }"
echo "}"
