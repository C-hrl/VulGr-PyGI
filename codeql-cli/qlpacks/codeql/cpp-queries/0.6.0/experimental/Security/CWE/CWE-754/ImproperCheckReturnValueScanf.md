# Improper check of return value of scanf
The \`scanf\` family functions does not require the memory pointed to by its additional pointer arguments to be initialized before calling. The user is required to check the return value of \`scanf\` and similar functions to establish how many of the additional arguments were assigned values. Not checking the return value and reading one of the arguments not assigned a value is undefined behavior and may have unexpected consequences.


## Recommendation
The user should check the return value of \`scanf\` and related functions and check that any additional argument was assigned a value before reading the additional argument.


## Example
The first example below is correct, as value of \`i\` is only read once it is checked that \`scanf\` has read one item. The second example is incorrect, as the return value of \`scanf\` is not checked, and as \`scanf\` might have failed to read any item before returning.


```cpp
...
  r = scanf("%i", &i);
  if (r == 1) // GOOD
    return i;
  else
    return -1;
...
  scanf("%i", &i); // BAD
  return i;
...

```

## References
* CERT C Coding Standard: [EXP12-C. Do not ignore values returned by functions](https://wiki.sei.cmu.edu/confluence/display/c/EXP12-C.+Do+not+ignore+values+returned+by+functions).
* Common Weakness Enumeration: [CWE-754](https://cwe.mitre.org/data/definitions/754.html).
* Common Weakness Enumeration: [CWE-908](https://cwe.mitre.org/data/definitions/908.html).
