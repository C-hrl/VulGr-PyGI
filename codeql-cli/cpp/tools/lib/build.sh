. "$AUTOBUILD_ROOT/lib/dirs.sh"

function join_lines() {
  sed -e:x -e'$!N; s/\n/'"$1"'/; tx'
}

function diagnose() {
  "$CODEQL_EXTRACTOR_CPP_ROOT/tools/diagnostics/diagnose-build.sh" "$@"
}

function try_running() {
  local name=$1
  local out="$(get_dir SCRATCH)/autobuild.out"
  echo "trying to run $*" >&2
  echo "$name" >> "$AUTOBUILD_TRIED_STEPS"
  "$@" 2>&1 | tee "$out"
  local status=${PIPESTATUS[0]}
  # for the moment, we don't emit diagnostics if the step succeeded. We might reconsider this in the future
  [[ "$status" == 0 ]] && return 0
  diagnose "$name" "$out"
  move_directories_to_tmp "$name"
  return $status
}

function emit_diagnostics() {
  "$AUTOBUILD_ROOT/diagnostics/emit.sh" "$@"
}

function tried_everything() {
  if [[ -s "$AUTOBUILD_TRIED_STEPS" ]]; then
    emit_diagnostics build-command-failed '`'"$(uniq "$AUTOBUILD_TRIED_STEPS" | join_lines '`, `')"'`'
  else
    emit_diagnostics no-build-command
  fi
  exit 1
}
