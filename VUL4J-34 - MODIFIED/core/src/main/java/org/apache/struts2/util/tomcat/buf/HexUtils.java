
package org.apache.struts2.util.tomcat.buf;


public final class HexUtils {

    

    
    private static final int[] DEC = {
        00, 01, 02, 03, 04, 05, 06, 07,  8,  9, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, 10, 11, 12, 13, 14, 15,
    };


    
    private static final byte[] HEX =
    { (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
      (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
      (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f' };


    
    private static final char[] hex = "0123456789abcdef".toCharArray();


    

    public static int getDec(int index) {
        
        try {
            return DEC[index - '0'];
        } catch (ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }


    public static byte getHex(int index) {
        return HEX[index];
    }


    public static String toHexString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }

        StringBuilder sb = new StringBuilder(bytes.length << 1);

        for(int i = 0; i < bytes.length; ++i) {
            sb.append(hex[(bytes[i] & 0xf0) >> 4])
                .append(hex[(bytes[i] & 0x0f)])
                ;
        }

        return sb.toString();
    }


    public static byte[] fromHexString(String input) {
        if (input == null) {
            return null;
        }

        if ((input.length() & 1) == 1) {
            
            throw new IllegalArgumentException("The input must consist of an even number of hex digits");
        }

        char[] inputChars = input.toCharArray();
        byte[] result = new byte[input.length() >> 1];
        for (int i = 0; i < result.length; i++) {
            int upperNibble = getDec(inputChars[2*i]);
            int lowerNibble =  getDec(inputChars[2*i + 1]);
            if (upperNibble < 0 || lowerNibble < 0) {
                
                throw new IllegalArgumentException("The input must consist only of hex digits");
            }
            result[i] = (byte) ((upperNibble << 4) + lowerNibble);
        }
        return result;
    }
}
