# Potential exposure of sensitive system data to an unauthorized control sphere
Exposing system data or debugging information may help a malicious user learn about the system and form an attack plan. An attacker can use error messages that reveal technologies, operating systems, and product versions to tune their attack against known vulnerabilities in the software.

This query finds locations where system configuration information that is particularly sensitive might be revealed to a user.


## Recommendation
Do not expose system configuration information to users. Be wary of the difference between information that could be helpful to users, and unnecessary details that could be useful to a malicious user.


## Example
In this example the value of the `PATH` environment variable is revealed in full to the user when a particular error occurs. This might reveal information such as the software installed on your system to a malicious user who does not have legitimate access to that information.


```cpp
char* key = getenv("APP_KEY");

//...

fprintf(stderr, "Key not recognized: %s\n", key);
```
The message should be rephrased without this information, for example:


```cpp
char* key = getenv("APP_KEY");

//...

fprintf(stderr, "Application key not recognized. Please ensure the key is correct or contact a system administrator.\n", key);
```

## References
* Common Weakness Enumeration: [CWE-497](https://cwe.mitre.org/data/definitions/497.html).