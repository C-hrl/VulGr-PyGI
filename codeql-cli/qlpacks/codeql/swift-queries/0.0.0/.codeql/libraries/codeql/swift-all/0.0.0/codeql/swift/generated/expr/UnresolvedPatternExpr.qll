// generated by codegen/codegen.py
private import codeql.swift.generated.Synth
private import codeql.swift.generated.Raw
import codeql.swift.elements.ErrorElement
import codeql.swift.elements.expr.Expr
import codeql.swift.elements.pattern.Pattern

module Generated {
  class UnresolvedPatternExpr extends Synth::TUnresolvedPatternExpr, Expr, ErrorElement {
    override string getAPrimaryQlClass() { result = "UnresolvedPatternExpr" }

    /**
     * Gets the sub pattern of this unresolved pattern expression.
     *
     * This includes nodes from the "hidden" AST. It can be overridden in subclasses to change the
     * behavior of both the `Immediate` and non-`Immediate` versions.
     */
    Pattern getImmediateSubPattern() {
      result =
        Synth::convertPatternFromRaw(Synth::convertUnresolvedPatternExprToRaw(this)
              .(Raw::UnresolvedPatternExpr)
              .getSubPattern())
    }

    /**
     * Gets the sub pattern of this unresolved pattern expression.
     */
    final Pattern getSubPattern() { result = getImmediateSubPattern().resolve() }
  }
}