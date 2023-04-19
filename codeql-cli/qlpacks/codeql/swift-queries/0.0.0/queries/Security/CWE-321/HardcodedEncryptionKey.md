# Hard-coded encryption key
Hardcoded keys should not be used for creating encryption ciphers. Data encrypted using hardcoded keys are more vulnerable to the possibility of recovering them.


## Recommendation
Use randomly generated key material to initialize the encryption cipher.


## Example
The following example shows a few cases of instantiating a cipher with various encryption keys. In the 'BAD' cases, the key material is hardcoded, making the encrypted data vulnerable to recovery. In the 'GOOD' cases, the key material is randomly generated and not hardcoded, which protects the encrypted data against recovery.


```none

func encrypt(padding : Padding) {
	// ...

	// BAD: Using hardcoded keys for encryption
	let key: Array<UInt8> = [0x2a, 0x3a, 0x80, 0x05]
	let keyString = "this is a constant string"
	let ivString = getRandomIV()
	_ = try AES(key: key, blockMode: CBC(), padding: padding)
	_ = try AES(key: keyString, iv: ivString)
	_ = try Blowfish(key: key, blockMode: CBC(), padding: padding)
	_ = try Blowfish(key: keyString, iv: ivString)


	// GOOD: Using randomly generated keys for encryption
	let key = (0..<10).map({ _ in UInt8.random(in: 0...UInt8.max) })
	let keyString = String(cString: key)
	let ivString = getRandomIV()
	_ = try AES(key: key, blockMode: CBC(), padding: padding)
	_ = try AES(key: keyString, iv: ivString)
	_ = try Blowfish(key: key, blockMode: CBC(), padding: padding)
	_ = try Blowfish(key: keyString, iv: ivString)

	// ...
}

```

## References
* OWASP: [Key Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Key_Management_Cheat_Sheet.html)
* Common Weakness Enumeration: [CWE-321](https://cwe.mitre.org/data/definitions/321.html).
