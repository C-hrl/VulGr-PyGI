
package org.apache.struts2.util.tomcat.buf;


public final class Ascii {
    

    private static final byte[] toUpper = new byte[256];
    private static final byte[] toLower = new byte[256];

    

    private static final boolean[] isAlpha = new boolean[256];
    private static final boolean[] isUpper = new boolean[256];
    private static final boolean[] isLower = new boolean[256];
    private static final boolean[] isWhite = new boolean[256];
    private static final boolean[] isDigit = new boolean[256];

    private static final long OVERFLOW_LIMIT = Long.MAX_VALUE / 10;

    
    static {
        for (int i = 0; i < 256; i++) {
            toUpper[i] = (byte)i;
            toLower[i] = (byte)i;
        }

        for (int lc = 'a'; lc <= 'z'; lc++) {
            int uc = lc + 'A' - 'a';

            toUpper[lc] = (byte)uc;
            toLower[uc] = (byte)lc;
            isAlpha[lc] = true;
            isAlpha[uc] = true;
            isLower[lc] = true;
            isUpper[uc] = true;
        }

        isWhite[ ' '] = true;
        isWhite['\t'] = true;
        isWhite['\r'] = true;
        isWhite['\n'] = true;
        isWhite['\f'] = true;
        isWhite['\b'] = true;

        for (int d = '0'; d <= '9'; d++) {
            isDigit[d] = true;
        }
    }

    
    @Deprecated
    public static int toUpper(int c) {
        return toUpper[c & 0xff] & 0xff;
    }

    

    public static int toLower(int c) {
        return toLower[c & 0xff] & 0xff;
    }

    
    @Deprecated
    public static boolean isAlpha(int c) {
        return isAlpha[c & 0xff];
    }

    
    @Deprecated
    public static boolean isUpper(int c) {
        return isUpper[c & 0xff];
    }

    
    @Deprecated
    public static boolean isLower(int c) {
        return isLower[c & 0xff];
    }

    
    @Deprecated
    public static boolean isWhite(int c) {
        return isWhite[c & 0xff];
    }

    

    public static boolean isDigit(int c) {
        return isDigit[c & 0xff];
    }

    
    @Deprecated
    public static int parseInt(byte[] b, int off, int len)
            throws NumberFormatException
    {
        int c;

        if (b == null || len <= 0 || !isDigit(c = b[off++])) {
            throw new NumberFormatException();
        }

        int n = c - '0';

        while (--len > 0) {
            if (!isDigit(c = b[off++])) {
                throw new NumberFormatException();
            }
            n = n * 10 + c - '0';
        }

        return n;
    }

    
    @Deprecated
    public static int parseInt(char[] b, int off, int len)
            throws NumberFormatException
    {
        int c;

        if (b == null || len <= 0 || !isDigit(c = b[off++])) {
            throw new NumberFormatException();
        }

        int n = c - '0';

        while (--len > 0) {
            if (!isDigit(c = b[off++])) {
                throw new NumberFormatException();
            }
            n = n * 10 + c - '0';
        }

        return n;
    }

    
    public static long parseLong(byte[] b, int off, int len)
            throws NumberFormatException
    {
        int c;

        if (b == null || len <= 0 || !isDigit(c = b[off++])) {
            throw new NumberFormatException();
        }

        long n = c - '0';
        while (--len > 0) {
            if (isDigit(c = b[off++]) &&
                    (n < OVERFLOW_LIMIT || (n == OVERFLOW_LIMIT && (c - '0') < 8))) {
                n = n * 10 + c - '0';
            } else {
                throw new NumberFormatException();
            }
        }

        return n;
    }

    
    @Deprecated
    public static long parseLong(char[] b, int off, int len)
            throws NumberFormatException
    {
        int c;

        if (b == null || len <= 0 || !isDigit(c = b[off++])) {
            throw new NumberFormatException();
        }

        long n = c - '0';
        long m;

        while (--len > 0) {
            if (!isDigit(c = b[off++])) {
                throw new NumberFormatException();
            }
            m = n * 10 + c - '0';

            if (m < n) {
                
                throw new NumberFormatException();
            } else {
                n = m;
            }
        }

        return n;
    }

}