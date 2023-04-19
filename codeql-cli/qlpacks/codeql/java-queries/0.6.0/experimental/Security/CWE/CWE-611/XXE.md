# Resolving XML external entity in user-controlled data (experimental sinks)
Parsing untrusted XML files with a weakly configured XML parser may lead to an XML External Entity (XXE) attack. This type of attack uses external entity references to access arbitrary files on a system, carry out denial of service, or server side request forgery. Even when the result of parsing is not returned to the user, out-of-band data retrieval techniques may allow attackers to steal sensitive data. Denial of services can also be carried out in this situation.

There are many XML parsers for Java, and most of them are vulnerable to XXE because their default settings enable parsing of external entities. This query currently identifies vulnerable XML parsing from the following parsers: `javax.xml.validation.Validator`, `org.dom4j.DocumentHelper`, `org.rundeck.api.parser.ParserHelper`, `org.apache.commons.digester3.Digester`, `org.apache.commons.digester.Digester`, `org.apache.tomcat.util.digester.Digester`, `java.beans.XMLDecoder`.


## Recommendation
The best way to prevent XXE attacks is to disable the parsing of any Document Type Declarations (DTDs) in untrusted data. If this is not possible you should disable the parsing of external general entities and external parameter entities. This improves security but the code will still be at risk of denial of service and server side request forgery attacks. Protection against denial of service attacks may also be implemented by setting entity expansion limits, which is done by default in recent JDK and JRE implementations.


## Example
The following bad examples parses the xml data entered by the user under an unsafe configuration, which is inherently insecure and may cause xml entity injection. In good examples, the security configuration is carried out, for example: Disable DTD to protect the program from XXE attacks.


```java
import java.beans.XMLDecoder;
import java.io.BufferedReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.digester3.Digester;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class XxeController {

	@PostMapping(value = "xxe1")
	public void bad1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ServletInputStream servletInputStream = request.getInputStream();
		Digester digester = new Digester();
		digester.parse(servletInputStream);
	}

	@PostMapping(value = "xxe2")
	public void bad2(HttpServletRequest request) throws Exception {
		BufferedReader br = request.getReader();
		String str = "";
		StringBuilder listString = new StringBuilder();
		while ((str = br.readLine()) != null) {
			listString.append(str).append("\n");
		}
		Document document = DocumentHelper.parseText(listString.toString());
	}

	@PostMapping(value = "xxe3")
	public void bad3(HttpServletRequest request) throws Exception {
		ServletInputStream servletInputStream = request.getInputStream();
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema();
		Validator validator = schema.newValidator();
		StreamSource source = new StreamSource(servletInputStream);
		validator.validate(source);
	}

	@PostMapping(value = "xxe4")
	public void bad4(HttpServletRequest request) throws Exception {
		ServletInputStream servletInputStream = request.getInputStream();
		XMLDecoder xmlDecoder = new XMLDecoder(servletInputStream);
		xmlDecoder.readObject();
	}

	@PostMapping(value = "good1")
	public void good1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedReader br = request.getReader();
		String str = "";
		StringBuilder listString = new StringBuilder();
		while ((str = br.readLine()) != null) {
			listString.append(str);
		}
		Digester digester = new Digester();
		digester.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		digester.setFeature("http://xml.org/sax/features/external-general-entities", false);
		digester.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		digester.parse(listString.toString());
	}

	@PostMapping(value = "good2")
	public void good2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedReader br = request.getReader();
		String str = "";
		StringBuilder listString = new StringBuilder();
		while ((str = br.readLine()) != null) {
			listString.append(str).append("\n");
		}
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		Schema schema = factory.newSchema();
		Validator validator = schema.newValidator();
		validator.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
		validator.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
		StreamSource source = new StreamSource(listString.toString());
		validator.validate(source);
	}
}

```

## References
* OWASP vulnerability description: [XML External Entity (XXE) Processing](https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Processing).
* OWASP guidance on parsing xml files: [XXE Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#java).
* Paper by Timothy Morgen: [XML Schema, DTD, and Entity Attacks](https://research.nccgroup.com/2014/05/19/xml-schema-dtd-and-entity-attacks-a-compendium-of-known-techniques/)
* Out-of-band data retrieval: Timur Yunusov &amp; Alexey Osipov, Black hat EU 2013: [XML Out-Of-Band Data Retrieval](https://www.slideshare.net/qqlan/bh-ready-v4).
* Denial of service attack (Billion laughs): [Billion Laughs.](https://en.wikipedia.org/wiki/Billion_laughs)
* The Java Tutorials: [Processing Limit Definitions.](https://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html)
* Common Weakness Enumeration: [CWE-611](https://cwe.mitre.org/data/definitions/611.html).
