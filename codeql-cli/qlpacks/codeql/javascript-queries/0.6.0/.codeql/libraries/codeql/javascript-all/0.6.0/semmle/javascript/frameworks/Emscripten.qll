/**
 * Provides classes for working with Emscripten-generated code.
 */

import javascript
import semmle.javascript.GeneratedCode

/**
 * An Emscripten marker comment.
 */
abstract class EmscriptenMarkerComment extends GeneratedCodeMarkerComment { }

/**
 * An `EMSCRIPTEN_START_ASM` marker comment.
 */
class EmscriptenStartAsmComment extends EmscriptenMarkerComment {
  EmscriptenStartAsmComment() { getText().trim() = "EMSCRIPTEN_START_ASM" }
}

/** DEPRECATED: Alias for EmscriptenStartAsmComment */
deprecated class EmscriptenStartASMComment = EmscriptenStartAsmComment;

/**
 * An `EMSCRIPTEN_START_FUNCS` marker comment.
 */
class EmscriptenStartFuncsComment extends EmscriptenMarkerComment {
  EmscriptenStartFuncsComment() { getText().trim() = "EMSCRIPTEN_START_FUNCS" }
}

/**
 * An `EMSCRIPTEN_END_ASM` marker comment.
 */
class EmscriptenEndAsmComment extends EmscriptenMarkerComment {
  EmscriptenEndAsmComment() { getText().trim() = "EMSCRIPTEN_END_ASM" }
}

/** DEPRECATED: Alias for EmscriptenEndAsmComment */
deprecated class EmscriptenEndASMComment = EmscriptenEndAsmComment;

/**
 * An `EMSCRIPTEN_END_FUNCS` marker comment.
 */
class EmscriptenEndFuncsComment extends EmscriptenMarkerComment {
  EmscriptenEndFuncsComment() { getText().trim() = "EMSCRIPTEN_END_FUNCS" }
}

/**
 * A toplevel that was generated by Emscripten as indicated
 * by an Emscripten marker comment.
 */
class EmscriptenGeneratedToplevel extends TopLevel {
  EmscriptenGeneratedToplevel() { exists(EmscriptenMarkerComment emc | this = emc.getTopLevel()) }
}