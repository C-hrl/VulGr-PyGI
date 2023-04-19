# Wrong type of arguments to formatting function
Each call to the `printf` function or a related function should include the type and sequence of arguments defined by the format. If the function is passed arguments of a different type or in a different sequence then the arguments are reinterpreted to fit the type and sequence expected, resulting in unpredictable behavior.


## Recommendation
Review the format and arguments expected by the highlighted function calls. Update either the format or the arguments so that the expected type and sequence of arguments are passed to the function.


## Example

```cpp
int main() {
  printf("%s\n", 42); //printf will treat 42 as a char*, will most likely segfault
  return 0;
}

```

## References
* CERT C Coding Standard: [FIO30-C. Exclude user input from format strings](https://www.securecoding.cert.org/confluence/display/c/FIO30-C.+Exclude+user+input+from+format+strings).
* cplusplus.com: [C++ Functions](http://www.tutorialspoint.com/cplusplus/cpp_functions.htm).
* CRT Alphabetical Function Reference: [printf, _printf_l, wprintf, _wprintf_l](https://docs.microsoft.com/en-us/cpp/c-runtime-library/reference/printf-printf-l-wprintf-wprintf-l).
* Common Weakness Enumeration: [CWE-686](https://cwe.mitre.org/data/definitions/686.html).
