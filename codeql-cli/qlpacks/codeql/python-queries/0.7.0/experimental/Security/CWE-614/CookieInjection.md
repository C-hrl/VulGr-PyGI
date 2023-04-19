# Construction of a cookie using user-supplied input.
Constructing cookies from user input may allow an attacker to perform a Cookie Poisoning attack. It is possible, however, to perform other parameter-like attacks through cookie poisoning techniques, such as SQL Injection, Directory Traversal, or Stealth Commanding, etc. Additionally, cookie injection may relate to attempts to perform Access of Administrative Interface.


## Recommendation
Do not use raw user input to construct cookies.


## Example
This example shows two ways of adding a cookie to a Flask response. The first way uses `set_cookie`'s and the second sets a cookie's raw value through a header, both using user-supplied input.


```python
from flask import request, make_response


@app.route("/1")
def true():
    resp = make_response()
    resp.set_cookie(request.args["name"],
                    value=request.args["name"])
    return resp


@app.route("/2")
def flask_make_response():
    resp = make_response("hello")
    resp.headers['Set-Cookie'] = f"{request.args['name']}={request.args['name']};"
    return resp

```

## References
* Imperva: [Cookie injection](https://docs.imperva.com/bundle/on-premises-knowledgebase-reference-guide/page/cookie_injection.htm).
* Common Weakness Enumeration: [CWE-614](https://cwe.mitre.org/data/definitions/614.html).
