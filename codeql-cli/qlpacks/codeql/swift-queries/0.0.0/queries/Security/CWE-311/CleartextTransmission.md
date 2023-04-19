# Cleartext transmission of sensitive information
Sensitive information that is transmitted without encryption may be accessible to an attacker.


## Recommendation
Ensure that sensitive information is always encrypted before being transmitted over the network. In general, decrypt sensitive information only at the point where it is necessary for it to be used in cleartext. Avoid transmitting sensitive information when it is not necessary to.


## Example
The following example shows three cases of transmitting information. In the 'BAD' case, the data transmitted is sensitive (a credit card number) and is not encrypted. In the 'GOOD' cases, the data is either not sensitive, or is protected with encryption.


```none

func transmitMyData(connection : NWConnection, faveSong : String, creditCardNo : String) {
	// ...

	// GOOD: not sensitive information
	connection.send(content: faveSong, completion: .idempotent)

	// BAD: sensitive information saved in cleartext
	connection.send(content: creditCardNo, completion: .idempotent)

	// GOOD: encrypted sensitive information saved
	connection.send(content: encrypt(creditCardNo), completion: .idempotent

	// ...
}

```

## References
* OWASP Top 10:2021: [A02:2021 � Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/).
* Common Weakness Enumeration: [CWE-319](https://cwe.mitre.org/data/definitions/319.html).
