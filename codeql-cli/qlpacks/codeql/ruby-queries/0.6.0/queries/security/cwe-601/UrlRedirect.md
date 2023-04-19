# URL redirection from remote source
Directly incorporating user input into a URL redirect request without validating the input can facilitate phishing attacks. In these attacks, unsuspecting users can be redirected to a malicious site that looks very similar to the real site they intend to visit, but which is controlled by the attacker.


## Recommendation
To guard against untrusted URL redirection, it is advisable to avoid putting user input directly into a redirect URL. Instead, maintain a list of authorized redirects on the server; then choose from that list based on the user input provided.


## Example
The following example shows an HTTP request parameter being used directly in a URL redirect without validating the input, which facilitates phishing attacks:


```ruby
class HelloController < ActionController::Base
  def hello
    redirect_to params[:url]
  end
end

```
One way to remedy the problem is to validate the user input against a known fixed string before doing the redirection:


```ruby
class HelloController < ActionController::Base
  VALID_REDIRECT = "http://cwe.mitre.org/data/definitions/601.html"

  def hello
    if params[:url] == VALID_REDIRECT
      redirect_to params[:url]
    else
      # error
    end
  end
end

```

## References
* OWASP: [ XSS Unvalidated Redirects and Forwards Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Unvalidated_Redirects_and_Forwards_Cheat_Sheet.html).
* Rails Guides: [ Redirection and Files](https://guides.rubyonrails.org/security.html#redirection-and-files).
* Common Weakness Enumeration: [CWE-601](https://cwe.mitre.org/data/definitions/601.html).
