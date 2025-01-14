# Uncontrolled command line (experimental sinks)
Code that passes user input directly to `Runtime.exec`, or some other library routine that executes a command, allows the user to execute malicious code.


## Recommendation
If possible, use hard-coded string literals to specify the command to run or library to load. Instead of passing the user input directly to the process or library function, examine the user input and then choose among hard-coded string literals.

If the applicable libraries or commands cannot be determined at compile time, then add code to verify that the user input string is safe before using it.


## Example
The following example shows code that takes a shell script that can be changed maliciously by a user, and passes it straight to `Runtime.exec` without examining it first.


```java
class Test {
    public static void main(String[] args) {
        String script = System.getenv("SCRIPTNAME");
        if (script != null) {
            // BAD: The script to be executed is controlled by the user.
            Runtime.getRuntime().exec(script);
        }
    }
}
```

## References
* OWASP: [Command Injection](https://www.owasp.org/index.php/Command_Injection).
* SEI CERT Oracle Coding Standard for Java: [IDS07-J. Sanitize untrusted data passed to the Runtime.exec() method](https://wiki.sei.cmu.edu/confluence/display/java/IDS07-J.+Sanitize+untrusted+data+passed+to+the+Runtime.exec()+method).
* Common Weakness Enumeration: [CWE-78](https://cwe.mitre.org/data/definitions/78.html).
* Common Weakness Enumeration: [CWE-88](https://cwe.mitre.org/data/definitions/88.html).
