# HTTP Header Injection
If an HTTP Header is built using string concatenation or string formatting, and the components of the concatenation include user input, a user is likely to be able to manipulate the response.


## Recommendation
User input should not be included in an HTTP Header.


## Example
In the following example, the code appends a user-provided value into a header.


```python
from flask import Response, request, Flask, make_response


@app.route("/flask_Response")
def flask_Response():
    rfs_header = request.args["rfs_header"]
    response = Response()
    response.headers['HeaderName'] = rfs_header
    return response

```

## References
* OWASP: [HTTP Response Splitting](https://owasp.org/www-community/attacks/HTTP_Response_Splitting).
* Python Security: [HTTP header injection](https://python-security.readthedocs.io/vuln/http-header-injection.html).
* SonarSource: [RSPEC-5167](https://rules.sonarsource.com/python/RSPEC-5167).
* Common Weakness Enumeration: [CWE-113](https://cwe.mitre.org/data/definitions/113.html).
* Common Weakness Enumeration: [CWE-79](https://cwe.mitre.org/data/definitions/79.html).
