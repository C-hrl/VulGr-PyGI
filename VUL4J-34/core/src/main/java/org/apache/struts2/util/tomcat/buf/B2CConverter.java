
package org.apache.struts2.util.tomcat.buf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class B2CConverter {

    private static final Map<String, Charset> encodingToCharsetCache =
            new HashMap<String, Charset>();

    public static final Charset ISO_8859_1;
    public static final Charset UTF_8;

    
    protected static final int LEFTOVER_SIZE = 9;

    static {
        for (Charset charset: Charset.availableCharsets().values()) {
            encodingToCharsetCache.put(
                    charset.name().toLowerCase(Locale.ENGLISH), charset);
            for (String alias : charset.aliases()) {
                encodingToCharsetCache.put(
                        alias.toLowerCase(Locale.ENGLISH), charset);
            }
        }
        Charset iso88591 = null;
        Charset utf8 = null;
        try {
            iso88591 = getCharset("ISO-8859-1");
            utf8 = getCharset("UTF-8");
        } catch (UnsupportedEncodingException e) {
            
            e.printStackTrace();
        }
        ISO_8859_1 = iso88591;
        UTF_8 = utf8;
    }

    public static Charset getCharset(String enc)
            throws UnsupportedEncodingException {

        
        String lowerCaseEnc = enc.toLowerCase(Locale.ENGLISH);

        return getCharsetLower(lowerCaseEnc);
    }

    
    public static Charset getCharsetLower(String lowerCaseEnc)
            throws UnsupportedEncodingException {

        Charset charset = encodingToCharsetCache.get(lowerCaseEnc);

        if (charset == null) {
            
            throw new UnsupportedEncodingException("The character encoding " + lowerCaseEnc + " is not supported");
        }
        return charset;
    }

    private final CharsetDecoder decoder;
    private ByteBuffer bb = null;
    private CharBuffer cb = null;

    
    private final ByteBuffer leftovers;

    public B2CConverter(String encoding) throws IOException {
        this(encoding, false);
    }

    public B2CConverter(String encoding, boolean replaceOnError)
            throws IOException {
        byte[] left = new byte[LEFTOVER_SIZE];
        leftovers = ByteBuffer.wrap(left);
        CodingErrorAction action;
        if (replaceOnError) {
            action = CodingErrorAction.REPLACE;
        } else {
            action = CodingErrorAction.REPORT;
        }
        Charset charset = getCharset(encoding);
        
        
        
        if (charset.equals(UTF_8)) {
            decoder = new Utf8Decoder();
        } else {
            decoder = charset.newDecoder();
        }
        decoder.onMalformedInput(action);
        decoder.onUnmappableCharacter(action);
    }

    
    public void recycle() {
        decoder.reset();
        leftovers.position(0);
    }

    
    public void convert(ByteChunk bc, CharChunk cc, boolean endOfInput)
            throws IOException {
        if ((bb == null) || (bb.array() != bc.getBuffer())) {
            
            bb = ByteBuffer.wrap(bc.getBuffer(), bc.getStart(), bc.getLength());
        } else {
            
            bb.limit(bc.getEnd());
            bb.position(bc.getStart());
        }
        if ((cb == null) || (cb.array() != cc.getBuffer())) {
            
            cb = CharBuffer.wrap(cc.getBuffer(), cc.getEnd(),
                    cc.getBuffer().length - cc.getEnd());
        } else {
            
            cb.limit(cc.getBuffer().length);
            cb.position(cc.getEnd());
        }
        CoderResult result = null;
        
        if (leftovers.position() > 0) {
            int pos = cb.position();
            
            do {
                leftovers.put(bc.substractB());
                leftovers.flip();
                result = decoder.decode(leftovers, cb, endOfInput);
                leftovers.position(leftovers.limit());
                leftovers.limit(leftovers.array().length);
            } while (result.isUnderflow() && (cb.position() == pos));
            if (result.isError() || result.isMalformed()) {
                result.throwException();
            }
            bb.position(bc.getStart());
            leftovers.position(0);
        }
        
        
        result = decoder.decode(bb, cb, endOfInput);
        if (result.isError() || result.isMalformed()) {
            result.throwException();
        } else if (result.isOverflow()) {
            
            
            bc.setOffset(bb.position());
            cc.setEnd(cb.position());
        } else if (result.isUnderflow()) {
            
            bc.setOffset(bb.position());
            cc.setEnd(cb.position());
            
            if (bc.getLength() > 0) {
                leftovers.limit(leftovers.array().length);
                leftovers.position(bc.getLength());
                bc.substract(leftovers.array(), 0, bc.getLength());
            }
        }
    }
}