# Use of a hardcoded key for signing JWT
A JSON Web Token (JWT) is used for authenticating and managing users in an application.

Using a hard-coded secret key for signing JWT tokens in open source projects can leave the application using the token vulnerable to authentication bypasses.

A JWT token is safe for enforcing authentication and access control as long as it can't be forged by a malicious actor. However, when a project exposes this secret publicly, these seemingly unforgeable tokens can now be easily forged. Since the authentication as well as access control is typically enforced through these JWT tokens, an attacker armed with the secret can create a valid authentication token for any user and may even gain access to other privileged parts of the application.


## Recommendation
Generating a cryptographically secure secret key during application initialization and using this generated key for future JWT signing requests can prevent this vulnerability.


## Example
The following code uses a hard-coded string as a secret for signing the tokens. In this case, an attacker can very easily forge a token by using the hard-coded secret.


```go
package main

import "time"

func bad() {
	mySigningKey := []byte("AllYourBase")

	claims := &jwt.RegisteredClaims{
		ExpiresAt: jwt.NewNumericDate(time.Unix(1516239022, 0)),
		Issuer:    "test",
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	ss, err := token.SignedString(mySigningKey)
}

```

## Example
In the following case, the application uses a programatically generated string as a secret for signing the tokens. In this case, since the secret can't be predicted, the code is secure. A function like \`GenerateCryptoString\` can be run to generate a secure secret key at the time of application installation/initialization. This generated key can then be used for all future signing requests.


```go
package main

import (
	"math/big"
	"time"
)

func GenerateCryptoString(n int) (string, error) {
	const chars = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-"
	ret := make([]byte, n)
	for i := range ret {
		num, err := crand.Int(crand.Reader, big.NewInt(int64(len(chars))))
		if err != nil {
			return "", err
		}
		ret[i] = chars[num.Int64()]
	}
	return string(ret), nil
}

func good() {
	mySigningKey := GenerateCryptoString(64)

	claims := &jwt.RegisteredClaims{
		ExpiresAt: jwt.NewNumericDate(time.Unix(1516239022, 0)),
		Issuer:    "test",
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	ss, err := token.SignedString(mySigningKey)
}

```

## References
* CVE-2022-0664: [Use of Hard-coded Cryptographic Key in Go github.com/gravitl/netmaker prior to 0.8.5,0.9.4,0.10.0,0.10.1. ](https://nvd.nist.gov/vuln/detail/CVE-2022-0664)
* Common Weakness Enumeration: [CWE-321](https://cwe.mitre.org/data/definitions/321.html).
