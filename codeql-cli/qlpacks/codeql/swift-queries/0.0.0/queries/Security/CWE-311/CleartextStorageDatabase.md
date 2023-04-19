# Cleartext storage of sensitive information in a local database
Sensitive information that is stored unencrypted in a database is accessible to an attacker who gains access to that database. For example, the information could be accessed by any process or user in a rooted device, or exposed through another vulnerability.


## Recommendation
Either encrypt the entire database, or ensure that each piece of sensitive information is encrypted before being stored. In general, decrypt sensitive information only at the point where it is necessary for it to be used in cleartext. Avoid storing sensitive information at all if you do not need to keep it.


## Example
The following example shows three cases of storing information using the Core Data library. In the 'BAD' case, the data that is stored is sensitive (a credit card number) and is not encrypted. In the 'GOOD' cases, the data is either not sensitive, or is protected with encryption.


```none

func storeMyData(databaseObject : NSManagedObject, faveSong : String, creditCardNo : String) {
	// ...

	// GOOD: not sensitive information
	databaseObject.setValue(faveSong, forKey: "myFaveSong")

	// BAD: sensitive information saved in cleartext
	databaseObject.setValue(creditCardNo, forKey: "myCreditCardNo")

	// GOOD: encrypted sensitive information saved
	databaseObject.setValue(encrypt(creditCardNo), forKey: "myCreditCardNo")

	// ...
}

```

## References
* OWASP Top 10:2021: [A02:2021 � Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/).
* Common Weakness Enumeration: [CWE-312](https://cwe.mitre.org/data/definitions/312.html).
