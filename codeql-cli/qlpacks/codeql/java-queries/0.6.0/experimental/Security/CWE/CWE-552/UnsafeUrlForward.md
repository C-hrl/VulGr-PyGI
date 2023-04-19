# Unsafe URL forward, dispatch, or load from remote source
Constructing a server-side redirect path with user input could allow an attacker to download application binaries (including application classes or jar files) or view arbitrary files within protected directories.


## Recommendation
Unsanitized user provided data must not be used to construct the path for URL forwarding. In order to prevent untrusted URL forwarding, it is recommended to avoid concatenating user input directly into the forwarding URL. Instead, user input should be checked against allowed (e.g., must come within `user_content/`) or disallowed (e.g. must not come within `/internal`) paths, ensuring that neither path traversal using `../` or URL encoding are used to evade these checks.


## Example
The following examples show the bad case and the good case respectively. The `bad` methods show an HTTP request parameter being used directly in a URL forward without validating the input, which may cause file leakage. In the `good1` method, ordinary forwarding requests are shown, which will not cause file leakage.


```java
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UnsafeUrlForward {

	@GetMapping("/bad1")
	public ModelAndView bad1(String url) {
		return new ModelAndView(url);
	}

	@GetMapping("/bad2")
	public void bad2(String url, HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/WEB-INF/jsp/" + url + ".jsp").include(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/good1")
	public void good1(String url, HttpServletRequest request, HttpServletResponse response) {
		try {
			request.getRequestDispatcher("/index.jsp?token=" + url).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

```
The following examples show an HTTP request parameter or request path being used directly in a request dispatcher of Java EE without validating the input, which allows sensitive file exposure attacks. It also shows how to remedy the problem by validating the user input.


```java
// BAD: no URI validation
String returnURL = request.getParameter("returnURL");
RequestDispatcher rd = sc.getRequestDispatcher(returnURL);
rd.forward(request, response);

// GOOD: check for a trusted prefix, ensuring path traversal is not used to erase that prefix:
// (alternatively use `Path.normalize` instead of checking for `..`)
if (!returnURL.contains("..") && returnURL.hasPrefix("/pages")) { ... }
// Also GOOD: check for a forbidden prefix, ensuring URL-encoding is not used to evade the check:
// (alternatively use `URLDecoder.decode` before `hasPrefix`)
if (returnURL.hasPrefix("/internal") && !returnURL.contains("%")) { ... }
```
The following examples show an HTTP request parameter or request path being used directly to retrieve a resource of a Java EE application without validating the input, which allows sensitive file exposure attacks. It also shows how to remedy the problem by validating the user input.


```java
// BAD: no URI validation
URL url = request.getServletContext().getResource(requestUrl);
url = getClass().getResource(requestUrl);
InputStream in = url.openStream();

InputStream in = request.getServletContext().getResourceAsStream(requestPath);
in = getClass().getClassLoader().getResourceAsStream(requestPath);

// GOOD: check for a trusted prefix, ensuring path traversal is not used to erase that prefix:
// (alternatively use `Path.normalize` instead of checking for `..`)
if (!requestPath.contains("..") && requestPath.startsWith("/trusted")) {
	InputStream in = request.getServletContext().getResourceAsStream(requestPath);
}

Path path = Paths.get(requestUrl).normalize().toRealPath();
if (path.startsWith("/trusted")) {
	URL url = request.getServletContext().getResource(path.toString());
}

```
The following examples show an HTTP request parameter being used directly to retrieve a resource of a Java Spring application without validating the input, which allows sensitive file exposure attacks. It also shows how to remedy the problem by validating the user input.


```java
//BAD: no path validation in Spring resource loading
@GetMapping("/file")
public String getFileContent(@RequestParam(name="fileName") String fileName) {
	ClassPathResource clr = new ClassPathResource(fileName);

	File file = ResourceUtils.getFile(fileName);

	Resource resource = resourceLoader.getResource(fileName);
}

//GOOD: check for a trusted prefix, ensuring path traversal is not used to erase that prefix in Spring resource loading:
@GetMapping("/file")
public String getFileContent(@RequestParam(name="fileName") String fileName) {
	if (!fileName.contains("..") && fileName.hasPrefix("/public-content")) {
		ClassPathResource clr = new ClassPathResource(fileName);

		File file = ResourceUtils.getFile(fileName);

		Resource resource = resourceLoader.getResource(fileName);
	}
}

```

## References
* File Disclosure: [Unsafe Url Forward](https://vulncat.fortify.com/en/detail?id=desc.dataflow.java.file_disclosure_spring).
* Jakarta Javadoc: [Security vulnerability with unsafe usage of RequestDispatcher](https://jakarta.ee/specifications/webprofile/9/apidocs/jakarta/servlet/servletrequest#getRequestDispatcher-java.lang.String-).
* Micro Focus: [File Disclosure: J2EE](https://vulncat.fortify.com/en/detail?id=desc.dataflow.java.file_disclosure_j2ee)
* CVE-2015-5174: [Apache Tomcat 6.0/7.0/8.0/9.0 Servletcontext getResource/getResourceAsStream/getResourcePaths Path Traversal](https://vuldb.com/?id.81084)
* CVE-2019-3799: [CVE-2019-3799 - Spring-Cloud-Config-Server Directory Traversal &lt; 2.1.2, 2.0.4, 1.4.6](https://github.com/mpgn/CVE-2019-3799)
