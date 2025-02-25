
package org.apache.struts2.util.tomcat.buf;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;


public class Utf8Decoder extends CharsetDecoder {

    
    
    
    
    
    
    
    
    
    
    
    
    private static final int remainingBytes[] = {
            
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            
            -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            
            3, 3, 3, 3, 3, -1, -1, -1,
            
            -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int remainingNumbers[] = {0, 
            4224, 
            401536, 
            29892736 
            
    };
    private static final int lowerEncodingLimit[] = {-1, 0x80, 0x800, 0x10000};


    public Utf8Decoder() {
        super(B2CConverter.UTF_8, 1.0f, 1.0f);
    }


    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return decodeHasArray(in, out);
        }
        return decodeNotHasArray(in, out);
    }


    private CoderResult decodeNotHasArray(ByteBuffer in, CharBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        try {
            while (pos < limit) {
                if (outRemaining == 0) {
                    return CoderResult.OVERFLOW;
                }
                int jchar = in.get();
                if (jchar < 0) {
                    jchar = jchar & 0x7F;
                    int tail = remainingBytes[jchar];
                    if (tail == -1) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (limit - pos < 1 + tail) {
                        
                        
                        return CoderResult.UNDERFLOW;
                    }
                    int nextByte;
                    for (int i = 0; i < tail; i++) {
                        nextByte = in.get() & 0xFF;
                        if ((nextByte & 0xC0) != 0x80) {
                            return CoderResult.malformedForLength(1 + i);
                        }
                        jchar = (jchar << 6) + nextByte;
                    }
                    jchar -= remainingNumbers[tail];
                    if (jchar < lowerEncodingLimit[tail]) {
                        
                        return CoderResult.malformedForLength(1);
                    }
                    pos += tail;
                }
                
                if (jchar >= 0xD800 && jchar <= 0xDFFF) {
                    return CoderResult.unmappableForLength(3);
                }
                
                if (jchar > 0x10FFFF) {
                    return CoderResult.unmappableForLength(4);
                }
                if (jchar <= 0xffff) {
                    out.put((char) jchar);
                    outRemaining--;
                } else {
                    if (outRemaining < 2) {
                        return CoderResult.OVERFLOW;
                    }
                    out.put((char) ((jchar >> 0xA) + 0xD7C0));
                    out.put((char) ((jchar & 0x3FF) + 0xDC00));
                    outRemaining -= 2;
                }
                pos++;
            }
            return CoderResult.UNDERFLOW;
        } finally {
            in.position(pos);
        }
    }


    private CoderResult decodeHasArray(ByteBuffer in, CharBuffer out) {
        int outRemaining = out.remaining();
        int pos = in.position();
        int limit = in.limit();
        final byte[] bArr = in.array();
        final char[] cArr = out.array();
        final int inIndexLimit = limit + in.arrayOffset();
        int inIndex = pos + in.arrayOffset();
        int outIndex = out.position() + out.arrayOffset();
        
        
        for (; inIndex < inIndexLimit && outRemaining > 0; inIndex++) {
            int jchar = bArr[inIndex];
            if (jchar < 0) {
                jchar = jchar & 0x7F;
                
                int tail = remainingBytes[jchar];
                if (tail == -1) {
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                
                
                
                int tailAvailable = inIndexLimit - inIndex - 1;
                if (tailAvailable > 0) {
                    
                    if (jchar > 0x41 && jchar < 0x60 &&
                            (bArr[inIndex + 1] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar == 0x60 && (bArr[inIndex + 1] & 0xE0) != 0xA0) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar > 0x60 && jchar < 0x6D &&
                            (bArr[inIndex + 1] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar == 0x6D && (bArr[inIndex + 1] & 0xE0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar > 0x6D && jchar < 0x70 &&
                            (bArr[inIndex + 1] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar == 0x70 &&
                            ((bArr[inIndex + 1] & 0xFF) < 0x90 ||
                                    (bArr[inIndex + 1] & 0xFF) > 0xBF)) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar > 0x70 && jchar < 0x74 &&
                            (bArr[inIndex + 1] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                    
                    if (jchar == 0x74 &&
                            (bArr[inIndex + 1] & 0xF0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1);
                    }
                }
                
                if (tailAvailable > 1 && tail > 1) {
                    if ((bArr[inIndex + 2] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(2);
                    }
                }
                
                if (tailAvailable > 2 && tail > 2) {
                    if ((bArr[inIndex + 3] & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(3);
                    }
                }
                if (tailAvailable < tail) {
                    break;
                }
                for (int i = 0; i < tail; i++) {
                    int nextByte = bArr[inIndex + i + 1] & 0xFF;
                    if ((nextByte & 0xC0) != 0x80) {
                        in.position(inIndex - in.arrayOffset());
                        out.position(outIndex - out.arrayOffset());
                        return CoderResult.malformedForLength(1 + i);
                    }
                    jchar = (jchar << 6) + nextByte;
                }
                jchar -= remainingNumbers[tail];
                if (jchar < lowerEncodingLimit[tail]) {
                    
                    in.position(inIndex - in.arrayOffset());
                    out.position(outIndex - out.arrayOffset());
                    return CoderResult.malformedForLength(1);
                }
                inIndex += tail;
            }
            
            if (jchar >= 0xD800 && jchar <= 0xDFFF) {
                return CoderResult.unmappableForLength(3);
            }
            
            if (jchar > 0x10FFFF) {
                return CoderResult.unmappableForLength(4);
            }
            if (jchar <= 0xffff) {
                cArr[outIndex++] = (char) jchar;
                outRemaining--;
            } else {
                if (outRemaining < 2) {
                    return CoderResult.OVERFLOW;
                }
                cArr[outIndex++] = (char) ((jchar >> 0xA) + 0xD7C0);
                cArr[outIndex++] = (char) ((jchar & 0x3FF) + 0xDC00);
                outRemaining -= 2;
            }
        }
        in.position(inIndex - in.arrayOffset());
        out.position(outIndex - out.arrayOffset());
        return (outRemaining == 0 && inIndex < inIndexLimit) ?
                CoderResult.OVERFLOW :
                CoderResult.UNDERFLOW;
    }
}