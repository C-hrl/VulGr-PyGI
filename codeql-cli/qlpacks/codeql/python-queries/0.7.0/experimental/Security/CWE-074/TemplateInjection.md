# Server Side Template Injection
Template Injection occurs when user input is embedded in a template in an unsafe manner. When an attacker is able to use native template syntax to inject a malicious payload into a template, which is then executed server-side is results in Server Side Template Injection.


## Recommendation
To fix this, ensure that an untrusted value is not used as a template. If the application requirements do not alow this, use a sandboxed environment where access to unsafe attributes and methods is prohibited.


## Example
Consider the example given below, an untrusted HTTP parameter \`template\` is used to generate a Jinja2 template string. This can lead to remote code execution.


```python
from django.urls import path
from django.http import HttpResponse
from jinja2 import Template as Jinja2_Template
from jinja2 import Environment, DictLoader, escape


def a(request):
    # Load the template
    template = request.GET['template']
    t = Jinja2_Template(template)
    name = request.GET['name']
    # Render the template with the context data
    html = t.render(name=escape(name))
    return HttpResponse(html)


urlpatterns = [
    path('a', a),
]

```
Here we have fixed the problem by using the Jinja sandbox environment for evaluating untrusted code.


```python
from django.urls import path
from django.http import HttpResponse
from jinja2 import Template as Jinja2_Template
from jinja2 import Environment, DictLoader, escape


def a(request):
    # Load the template
    template = request.GET['template']
    env = SandboxedEnvironment(undefined=StrictUndefined)
    t = env.from_string(template)
    name = request.GET['name']
    # Render the template with the context data
    html = t.render(name=escape(name))
    return HttpResponse(html)


urlpatterns = [
    path('a', a),
]

```

## References
* Portswigger : \[Server Side Template Injection\](https://portswigger.net/web-security/server-side-template-injection)
* Common Weakness Enumeration: [CWE-74](https://cwe.mitre.org/data/definitions/74.html).
