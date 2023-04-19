EXTRACTOR_DIRS=(TRAP SCRATCH DIAGNOSTIC SOURCE_ARCHIVE)
AUTOBUILD_TRIED_STEPS="$AUTOBUILD_TMP_DIR/tried_steps.txt"

function get_dir() {
  local dir_var=CODEQL_EXTRACTOR_CPP_${1}_DIR
  echo "${!dir_var}"
}

function move_directories_to_tmp() {
  local name="$1"
  mkdir -p "$AUTOBUILD_TMP_DIR/$name"
  for dir in "${EXTRACTOR_DIRS[@]}"; do
    local dir_path="$(get_dir $dir)"
    [[ -d "$dir_path" ]] || continue
    mv -f "$dir_path" "$AUTOBUILD_TMP_DIR/$name/$dir"
    mkdir -p "$dir_path"
  done
}

function get_failed_command_output_file() {
  local name="$1"
  echo "$AUTOBUILD_TMP_DIR/$name/SCRATCH/autobuild.out"
}

function cleanup() {
  local exit_status="$?"
  [[ -d "$AUTOBUILD_TMP_DIR" ]] || return 0
  if [[ "$exit_status" == 0 ]]; then
    if [[ -s "$AUTOBUILD_TRIED_STEPS" && -d "$AUTOBUILD_TMP_DIR" ]]; then
      # keep artifacts of failed trials in the scratch dir for debugging
      mv "$AUTOBUILD_TMP_DIR" "$(get_dir SCRATCH)/autobuild_failed_trials"
    fi
  else
    # merge all artifacts from failed trials into the normal target directories
    for dir in "${EXTRACTOR_DIRS[@]}"; do
      local dir_path="$(get_dir $dir)"
      [[ -d "$dir_path" ]] || continue
      cat "$AUTOBUILD_TRIED_STEPS" | while read build; do
        local src_dir="$AUTOBUILD_TMP_DIR/$build/$dir"
        [[ -d "$src_dir" ]] && cp -faL "$src_dir/." "$dir_path"
      done
    done
    rm -rf "$AUTOBUILD_TMP_DIR"
  fi
}
