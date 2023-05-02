package org.apache.struts2.util;

import org.apache.struts2.util.tomcat.buf.UDecoder;


public class URLDecoderUtil {

    
    public static String decode(String sequence, String charset) {
        return UDecoder.URLDecode(sequence, charset);
    }

}
