/**
 * Provides classes and predicates for reasoning about use of broken or weak
 * cryptographic hashing algorithms on sensitive data.
 */

import swift
import codeql.swift.security.SensitiveExprs
import codeql.swift.dataflow.DataFlow
import codeql.swift.dataflow.ExternalFlow

/**
 * A dataflow sink for weak sensitive data hashing vulnerabilities. That is,
 * a `DataFlow::Node` that is passed into a weak hashing function.
 */
abstract class WeakSensitiveDataHashingSink extends DataFlow::Node {
  /**
   * Gets the name of the hashing algorithm, for display.
   */
  abstract string getAlgorithm();
}

/**
 * A sanitizer for weak sensitive data hashing vulnerabilities.
 */
abstract class WeakSensitiveDataHashingSanitizer extends DataFlow::Node { }

/**
 * A unit class for adding additional taint steps.
 */
class WeakSensitiveDataHashingAdditionalTaintStep extends Unit {
  /**
   * Holds if the step from `node1` to `node2` should be considered a taint
   * step for paths related to weak sensitive data hashing vulnerabilities.
   */
  abstract predicate step(DataFlow::Node nodeFrom, DataFlow::Node nodeTo);
}

private class WeakHashingSinks extends SinkModelCsv {
  override predicate row(string row) {
    row =
      [
        // CryptoKit
        ";Insecure.MD5;true;hash(data:);;;Argument[0];weak-hash-input-MD5",
        ";Insecure.MD5;true;update(data:);;;Argument[0];weak-hash-input-MD5",
        ";Insecure.MD5;true;update(bufferPointer:);;;Argument[0];weak-hash-input-MD5",
        ";Insecure.SHA1;true;hash(data:);;;Argument[0];weak-hash-input-SHA1",
        ";Insecure.SHA1;true;update(data:);;;Argument[0];weak-hash-input-SHA1",
        ";Insecure.SHA1;true;update(bufferPointer:);;;Argument[0];weak-hash-input-SHA1",
      ]
  }
}

/**
 * A sink defined in a CSV model.
 */
private class DefaultWeakHashingSink extends WeakSensitiveDataHashingSink {
  string algorithm;

  DefaultWeakHashingSink() { sinkNode(this, "weak-hash-input-" + algorithm) }

  override string getAlgorithm() { result = algorithm }
}
