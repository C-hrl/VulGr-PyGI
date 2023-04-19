# Database query built from user-controlled sources
If a database query (such as a SQL query) is built from user-provided data without sufficient sanitization, a user may be able to run malicious database queries. An attacker can craft the part of the query they control to change the overall meaning of the query.


## Recommendation
Most database connector libraries offer a way to safely embed untrusted data into a query using query parameters or prepared statements. You should use these features to build queries, rather than string concatenation or similar methods without sufficient sanitization.


## Example
In the following example, a SQL query is prepared using string interpolation to directly include a user-controlled value `userControlledString` in the query. An attacker could craft `userControlledString` to change the overall meaning of the SQL query.


```none
let unsafeQuery = "SELECT * FROM users WHERE username='\(userControlledString)'" // BAD

try db.execute(unsafeQuery)
```
A better way to do this is with a prepared statement, binding `userControlledString` to a parameter of that statement. An attacker who controls `userControlledString` now cannot change the overall meaning of the query.


```none
let safeQuery = "SELECT * FROM users WHERE username=?"

let stmt = try db.prepare(safeQuery, userControlledString) // GOOD
try stmt2.run()
```

## References
* Wikipedia: [SQL injection](https://en.wikipedia.org/wiki/SQL_injection).
* OWASP: [SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html).
* Common Weakness Enumeration: [CWE-89](https://cwe.mitre.org/data/definitions/89.html).
