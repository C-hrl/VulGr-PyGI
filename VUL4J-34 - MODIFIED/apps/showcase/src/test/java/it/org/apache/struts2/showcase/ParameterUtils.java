
package it.org.apache.struts2.showcase;

public class ParameterUtils {

    public static String getBaseUrl() {
        String port = System.getProperty("http.port");
        if (port == null) {
            port = "8080";
        }
        return "http:
    }
}
