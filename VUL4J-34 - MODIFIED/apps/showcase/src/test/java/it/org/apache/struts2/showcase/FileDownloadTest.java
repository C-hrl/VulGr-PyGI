
package it.org.apache.struts2.showcase;

import java.net.URL;
import java.net.MalformedURLException;

public class FileDownloadTest extends ITBaseTest {
    public void testImage() throws InterruptedException, MalformedURLException {
        beginAt("/filedownload/download.action");

        URL url = new URL("http:
        assertDownloadedFileEquals(url);
    }

     public void testZip() throws InterruptedException, MalformedURLException {
        beginAt("/filedownload/download2.action");

        URL url = new URL("http:
        assertDownloadedFileEquals(url);
    }
}
