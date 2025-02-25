/**
 * @name User-controlled data used in permissions check
 * @description Using user-controlled data in a permissions check may result in inappropriate
 *              permissions being granted.
 * @kind path-problem
 * @problem.severity error
 * @security-severity 7.8
 * @precision high
 * @id java/tainted-permissions-check
 * @tags security
 *       external/cwe/cwe-807
 *       external/cwe/cwe-290
 */

import java
import semmle.code.java.dataflow.FlowSources
import semmle.code.java.dataflow.TaintTracking

class TypeShiroSubject extends RefType {
  TypeShiroSubject() { this.getQualifiedName() = "org.apache.shiro.subject.Subject" }
}

class TypeShiroWCPermission extends RefType {
  TypeShiroWCPermission() {
    this.getQualifiedName() = "org.apache.shiro.authz.permission.WildcardPermission"
  }
}

abstract class PermissionsConstruction extends Top {
  abstract Expr getInput();
}

class PermissionsCheckMethodAccess extends MethodAccess, PermissionsConstruction {
  PermissionsCheckMethodAccess() {
    exists(Method m | m = this.getMethod() |
      m.getDeclaringType() instanceof TypeShiroSubject and
      m.getName() = "isPermitted"
      or
      m.getName().toLowerCase().matches("%permitted%") and
      m.getNumberOfParameters() = 1
    )
  }

  override Expr getInput() { result = this.getArgument(0) }
}

class WCPermissionConstruction extends ClassInstanceExpr, PermissionsConstruction {
  WCPermissionConstruction() {
    this.getConstructor().getDeclaringType() instanceof TypeShiroWCPermission
  }

  override Expr getInput() { result = this.getArgument(0) }
}

module TaintedPermissionsCheckFlowConfig implements DataFlow::ConfigSig {
  predicate isSource(DataFlow::Node source) { source instanceof UserInput }

  predicate isSink(DataFlow::Node sink) {
    sink.asExpr() = any(PermissionsConstruction p).getInput()
  }
}

module TaintedPermissionsCheckFlow = TaintTracking::Global<TaintedPermissionsCheckFlowConfig>;

import TaintedPermissionsCheckFlow::PathGraph

from
  TaintedPermissionsCheckFlow::PathNode source, TaintedPermissionsCheckFlow::PathNode sink,
  PermissionsConstruction p
where sink.getNode().asExpr() = p.getInput() and TaintedPermissionsCheckFlow::flowPath(source, sink)
select p, source, sink, "Permissions check depends on a $@.", source.getNode(),
  "user-controlled value"
