# Uncontrolled data used in path expression
Accessing paths controlled by users can allow an attacker to access unexpected resources. This can result in sensitive information being revealed or deleted, or an attacker being able to influence behavior by modifying unexpected files.

Paths that are naively constructed from data controlled by a user may contain unexpected special characters, such as "..". Such a path may potentially point to any directory on the file system.


## Recommendation
Validate user input before using it to construct a file path. Ideally, follow these rules:

* Do not allow more than a single "." character.
* Do not allow directory separators such as "/" or "\\" (depending on the file system).
* Do not rely on simply replacing problematic sequences such as "../". For example, after applying this filter to ".../...//" the resulting string would still be "../".
* Ideally use a whitelist of known good patterns.

## Example
In this example, a file name is read from a `java.net.Socket` and then used to access a file in the user's home directory and send it back over the socket. However, a malicious user could enter a file name which contains special characters. For example, the string "../../etc/passwd" will result in the code reading the file located at "/home/\[user\]/../../etc/passwd", which is the system's password file. This file would then be sent back to the user, giving them access to all the system's passwords.


```java
public void sendUserFile(Socket sock, String user) {
	BufferedReader filenameReader = new BufferedReader(
			new InputStreamReader(sock.getInputStream(), "UTF-8"));
	String filename = filenameReader.readLine();
	// BAD: read from a file using a path controlled by the user
	BufferedReader fileReader = new BufferedReader(
			new FileReader("/home/" + user + "/" + filename));
	String fileLine = fileReader.readLine();
	while(fileLine != null) {
		sock.getOutputStream().write(fileLine.getBytes());
		fileLine = fileReader.readLine();
	}
}

public void sendUserFileFixed(Socket sock, String user) {
	// ...
	
	// GOOD: remove all dots and directory delimiters from the filename before using
	String filename = filenameReader.readLine().replaceAll("\\.", "").replaceAll("/", "");
	BufferedReader fileReader = new BufferedReader(
			new FileReader("/home/" + user + "/" + filename));

	// ...
}

```

## References
* OWASP: [Path Traversal](https://owasp.org/www-community/attacks/Path_Traversal).
* Common Weakness Enumeration: [CWE-22](https://cwe.mitre.org/data/definitions/22.html).
* Common Weakness Enumeration: [CWE-23](https://cwe.mitre.org/data/definitions/23.html).
* Common Weakness Enumeration: [CWE-36](https://cwe.mitre.org/data/definitions/36.html).
* Common Weakness Enumeration: [CWE-73](https://cwe.mitre.org/data/definitions/73.html).
