# Use of a broken or weak cryptographic hashing algorithm on sensitive data
Using a broken or weak cryptographic hash function can leave data vulnerable, and should not be used in security related code.

A strong cryptographic hash function should be resistant to:

* **Pre-image attacks**. If you know a hash value `h(x)`, you should not be able to easily find the input `x`.
* **Collision attacks**. If you know a hash value `h(x)`, you should not be able to easily find a different input `y` with the same hash value `h(x) = h(y)`.
As an example, both MD5 and SHA-1 are known to be vulnerable to collision attacks.

Since it's OK to use a weak cryptographic hash function in a non-security context, this query only alerts when these are used to hash sensitive data (such as passwords, certificates, usernames).


## Recommendation
Ensure that you use a strong, modern cryptographic hash function, such as:

* Argon2, scrypt, bcrypt, or PBKDF2 for passwords and other data with limited input space where a dictionary-like attack is feasible.
* SHA-2, or SHA-3 in other cases.

## Example
The following examples show a function for checking whether the hash of a certificate matches a known value -- to prevent tampering. In the first case the MD5 hashing algorithm is used that is known to be vulnerable to collision attacks.


```none
typealias Hasher = Crypto.Insecure.MD5

func checkCertificate(cert: Array[UInt8], hash: Array[UInt8]) -> Bool
  return Hasher.hash(data: cert) == hash  // BAD
}

```
Here is the same function using SHA-512, which is a strong cryptographic hashing function.


```none
typealias Hasher = Crypto.SHA512

func checkCertificate(cert: Array[UInt8], hash: Array[UInt8]) -> Bool
  return Hasher.hash(data: cert) == hash  // GOOD

```

## References
* OWASP: [Password Storage Cheat Sheet ](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html) and [ Transport Layer Protection Cheat Sheet ](https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Protection_Cheat_Sheet.html#use-strong-cryptographic-hashing-algorithms)
* Common Weakness Enumeration: [CWE-327](https://cwe.mitre.org/data/definitions/327.html).
* Common Weakness Enumeration: [CWE-328](https://cwe.mitre.org/data/definitions/328.html).
