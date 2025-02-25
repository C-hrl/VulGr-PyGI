
package org.apache.struts2.util.tomcat.buf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.CharConversionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


public final class UDecoder {

    private static final Logger log = LogManager.getLogger(UDecoder.class);

    public static final boolean ALLOW_ENCODED_SLASH =
        Boolean.parseBoolean(System.getProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "false"));

    private static class DecodeException extends CharConversionException {
        private static final long serialVersionUID = 1L;
        public DecodeException(String s) {
            super(s);
        }

        @Override
        public synchronized Throwable fillInStackTrace() {
            
            return this;
        }
    }

    
    private static final IOException EXCEPTION_EOF = new DecodeException("EOF");

    
    private static final IOException EXCEPTION_NOT_HEX_DIGIT = new DecodeException(
            "isHexDigit");

    
    private static final IOException EXCEPTION_SLASH = new DecodeException(
            "noSlash");

    public UDecoder()
    {
    }

    
    public void convert( ByteChunk mb, boolean query )
        throws IOException
    {
        int start=mb.getOffset();
        byte buff[]=mb.getBytes();
        int end=mb.getEnd();

        int idx= ByteChunk.findByte( buff, start, end, (byte) '%' );
        int idx2=-1;
        if( query ) {
            idx2= ByteChunk.findByte( buff, start, (idx >= 0 ? idx : end), (byte) '+' );
        }
        if( idx<0 && idx2<0 ) {
            return;
        }

        
        if( (idx2 >= 0 && idx2 < idx) || idx < 0 ) {
            idx=idx2;
        }

        final boolean noSlash = !(ALLOW_ENCODED_SLASH || query);

        for( int j=idx; j<end; j++, idx++ ) {
            if( buff[ j ] == '+' && query) {
                buff[idx]= (byte)' ' ;
            } else if( buff[ j ] != '%' ) {
                buff[idx]= buff[j];
            } else {
                
                if( j+2 >= end ) {
                    throw EXCEPTION_EOF;
                }
                byte b1= buff[j+1];
                byte b2=buff[j+2];
                if( !isHexDigit( b1 ) || ! isHexDigit(b2 )) {
                    throw EXCEPTION_NOT_HEX_DIGIT;
                }

                j+=2;
                int res=x2c( b1, b2 );
                if (noSlash && (res == '/')) {
                    throw EXCEPTION_SLASH;
                }
                buff[idx]=(byte)res;
            }
        }

        mb.setEnd( idx );

        return;
    }

    
    

    
    public void convert( CharChunk mb, boolean query )
        throws IOException
    {
        
        int start=mb.getOffset();
        char buff[]=mb.getBuffer();
        int cend=mb.getEnd();

        int idx= CharChunk.indexOf( buff, start, cend, '%' );
        int idx2=-1;
        if( query ) {
            idx2= CharChunk.indexOf( buff, start, (idx >= 0 ? idx : cend), '+' );
        }
        if( idx<0 && idx2<0 ) {
            return;
        }

        
        if( (idx2 >= 0 && idx2 < idx) || idx < 0 ) {
            idx=idx2;
        }

        final boolean noSlash = !(ALLOW_ENCODED_SLASH || query);

        for( int j=idx; j<cend; j++, idx++ ) {
            if( buff[ j ] == '+' && query ) {
                buff[idx]=( ' ' );
            } else if( buff[ j ] != '%' ) {
                buff[idx]=buff[j];
            } else {
                
                if( j+2 >= cend ) {
                    
                    throw EXCEPTION_EOF;
                }
                char b1= buff[j+1];
                char b2=buff[j+2];
                if( !isHexDigit( b1 ) || ! isHexDigit(b2 )) {
                    throw EXCEPTION_NOT_HEX_DIGIT;
                }

                j+=2;
                int res=x2c( b1, b2 );
                if (noSlash && (res == '/')) {
                    throw EXCEPTION_SLASH;
                }
                buff[idx]=(char)res;
            }
        }
        mb.setEnd( idx );
    }

    
    public void convert(MessageBytes mb, boolean query)
        throws IOException
    {

        switch (mb.getType()) {
        case MessageBytes.T_STR:
            String strValue=mb.toString();
            if( strValue==null ) {
                return;
            }
            try {
                mb.setString( convert( strValue, query ));
            } catch (RuntimeException ex) {
                throw new DecodeException(ex.getMessage());
            }
            break;
        case MessageBytes.T_CHARS:
            CharChunk charC=mb.getCharChunk();
            convert( charC, query );
            break;
        case MessageBytes.T_BYTES:
            ByteChunk bytesC=mb.getByteChunk();
            convert( bytesC, query );
            break;
        }
    }

    
    
    public final String convert(String str, boolean query)
    {
        if (str == null) {
            return  null;
        }

        if( (!query || str.indexOf( '+' ) < 0) && str.indexOf( '%' ) < 0 ) {
            return str;
        }

        final boolean noSlash = !(ALLOW_ENCODED_SLASH || query);

        StringBuilder dec = new StringBuilder();    
        int strPos = 0;
        int strLen = str.length();

        dec.ensureCapacity(str.length());
        while (strPos < strLen) {
            int laPos;        

            
            for (laPos = strPos; laPos < strLen; laPos++) {
                char laChar = str.charAt(laPos);
                if ((laChar == '+' && query) || (laChar == '%')) {
                    break;
                }
            }

            
            if (laPos > strPos) {
                dec.append(str.substring(strPos,laPos));
                strPos = laPos;
            }

            
            if (strPos >= strLen) {
                break;
            }

            
            char metaChar = str.charAt(strPos);
            if (metaChar == '+') {
                dec.append(' ');
                strPos++;
                continue;
            } else if (metaChar == '%') {
                
                
                
                char res = (char) Integer.parseInt(
                        str.substring(strPos + 1, strPos + 3), 16);
                if (noSlash && (res == '/')) {
                    throw new IllegalArgumentException("noSlash");
                }
                dec.append(res);
                strPos += 3;
            }
        }

        return dec.toString();
    }


    
    public static String URLDecode(String str) {
        return URLDecode(str, null);
    }


    
    public static String URLDecode(String str, String enc) {
        return URLDecode(str, enc, false);
    }


    
    public static String URLDecode(String str, String enc, boolean isQuery) {
        if (str == null)
            return (null);

        
        
        
        byte[] bytes = null;
        try {
            if (enc == null) {
                bytes = str.getBytes("ISO-8859-1");
            } else {
                bytes = str.getBytes(B2CConverter.getCharset(enc));
            }
        } catch (UnsupportedEncodingException uee) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to URL decode the specified input since the encoding "+ enc + " is not supported.", uee);
            }
        }

        return URLDecode(bytes, enc, isQuery);

    }


    
    public static String URLDecode(byte[] bytes, String enc, boolean isQuery) {

        if (bytes == null)
            return null;

        int len = bytes.length;
        int ix = 0;
        int ox = 0;
        while (ix < len) {
            byte b = bytes[ix++];     
            if (b == '+' && isQuery) {
                b = (byte)' ';
            } else if (b == '%') {
                if (ix + 2 > len) {
                    throw new IllegalArgumentException(
                            "The % character must be followed by two hexademical digits");
                }
                b = (byte) ((convertHexDigit(bytes[ix++]) << 4)
                            + convertHexDigit(bytes[ix++]));
            }
            bytes[ox++] = b;
        }
        if (enc != null) {
            try {
                return new String(bytes, 0, ox, B2CConverter.getCharset(enc));
            } catch (UnsupportedEncodingException uee) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to URL decode the specified input since the encoding " + enc + " is not supported.", uee);
                }
                return null;
            }
        }
        return new String(bytes, 0, ox);

    }


    private static byte convertHexDigit( byte b ) {
        if ((b >= '0') && (b <= '9')) return (byte)(b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte)(b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte)(b - 'A' + 10);
        throw new IllegalArgumentException(((char) b) + " is not a hexadecimal digit");
    }


    private static boolean isHexDigit( int c ) {
        return ( ( c>='0' && c<='9' ) ||
                 ( c>='a' && c<='f' ) ||
                 ( c>='A' && c<='F' ));
    }


    private static int x2c( byte b1, byte b2 ) {
        int digit= (b1>='A') ? ( (b1 & 0xDF)-'A') + 10 :
            (b1 -'0');
        digit*=16;
        digit +=(b2>='A') ? ( (b2 & 0xDF)-'A') + 10 :
            (b2 -'0');
        return digit;
    }


    private static int x2c( char b1, char b2 ) {
        int digit= (b1>='A') ? ( (b1 & 0xDF)-'A') + 10 :
            (b1 -'0');
        digit*=16;
        digit +=(b2>='A') ? ( (b2 & 0xDF)-'A') + 10 :
            (b2 -'0');
        return digit;
    }
}
