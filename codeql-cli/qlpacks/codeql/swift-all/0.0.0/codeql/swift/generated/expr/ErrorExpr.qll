// generated by codegen/codegen.py
private import codeql.swift.generated.Synth
private import codeql.swift.generated.Raw
import codeql.swift.elements.ErrorElement
import codeql.swift.elements.expr.Expr

module Generated {
  class ErrorExpr extends Synth::TErrorExpr, Expr, ErrorElement {
    override string getAPrimaryQlClass() { result = "ErrorExpr" }
  }
}