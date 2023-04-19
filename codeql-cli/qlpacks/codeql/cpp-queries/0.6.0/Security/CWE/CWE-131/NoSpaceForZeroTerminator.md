# No space for zero terminator
This rule identifies calls to `malloc` that call `strlen` to determine the required buffer size, but do not allocate space for the zero terminator.


## Recommendation
The expression highlighted by this rule creates a buffer that is of insufficient size to contain the data being copied. This makes the code vulnerable to buffer overflow which can result in anything from a segmentation fault to a security vulnerability (particularly if the array is on stack-allocated memory).

Increase the size of the buffer being allocated by one or replace `malloc`, `strcpy` pairs with a call to `strdup`


## Example

```c

void flawed_strdup(const char *input)
{
	char *copy;

	/* Fail to allocate space for terminating '\0' */
	copy = (char *)malloc(strlen(input));
	strcpy(copy, input);
	return copy;
}


```

## References
* CERT C Coding Standard: [MEM35-C. Allocate sufficient memory for an object](https://www.securecoding.cert.org/confluence/display/c/MEM35-C.+Allocate+sufficient+memory+for+an+object).
* Common Weakness Enumeration: [CWE-131](https://cwe.mitre.org/data/definitions/131.html).
* Common Weakness Enumeration: [CWE-120](https://cwe.mitre.org/data/definitions/120.html).
* Common Weakness Enumeration: [CWE-122](https://cwe.mitre.org/data/definitions/122.html).
