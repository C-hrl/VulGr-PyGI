/**
 * Provides classes and predicates for reasoning about use of constant salts
 * for password hashing.
 */

import swift
import codeql.swift.dataflow.DataFlow
import codeql.swift.dataflow.ExternalFlow

/**
 * A dataflow sink for constant salt vulnerabilities. That is,
 * a `DataFlow::Node` of something that is used as a salt.
 */
abstract class ConstantSaltSink extends DataFlow::Node { }

/**
 * A sanitizer for constant salt vulnerabilities.
 */
abstract class ConstantSaltSanitizer extends DataFlow::Node { }

/**
 * A unit class for adding additional taint steps.
 */
class ConstantSaltAdditionalTaintStep extends Unit {
  /**
   * Holds if the step from `node1` to `node2` should be considered a taint
   * step for paths related to constant salt vulnerabilities.
   */
  abstract predicate step(DataFlow::Node nodeFrom, DataFlow::Node nodeTo);
}

/**
 * A sink for the CryptoSwift library.
 */
private class CryptoSwiftSaltSink extends ConstantSaltSink {
  CryptoSwiftSaltSink() {
    // `salt` arg in `init` is a sink
    exists(NominalTypeDecl c, ConstructorDecl f, CallExpr call |
      c.getName() = ["HKDF", "PBKDF1", "PBKDF2", "Scrypt"] and
      c.getAMember() = f and
      call.getStaticTarget() = f and
      call.getArgumentWithLabel("salt").getExpr() = this.asExpr()
    )
  }
}

/**
 * A sink for the RNCryptor library.
 */
private class RnCryptorSaltSink extends ConstantSaltSink {
  RnCryptorSaltSink() {
    exists(NominalTypeDecl c, MethodDecl f, CallExpr call |
      c.getFullName() =
        [
          "RNCryptor", "RNEncryptor", "RNDecryptor", "RNCryptor.EncryptorV3",
          "RNCryptor.DecryptorV3"
        ] and
      c.getAMember() = f and
      call.getStaticTarget() = f and
      call.getArgumentWithLabel(["salt", "encryptionSalt", "hmacSalt", "HMACSalt"]).getExpr() =
        this.asExpr()
    )
  }
}

/**
 * A sink defined in a CSV model.
 */
private class DefaultSaltSink extends ConstantSaltSink {
  DefaultSaltSink() { sinkNode(this, "encryption-salt") }
}
