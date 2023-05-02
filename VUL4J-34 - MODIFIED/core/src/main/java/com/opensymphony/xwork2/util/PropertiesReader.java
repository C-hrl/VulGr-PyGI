
package com.opensymphony.xwork2.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PropertiesReader extends LineNumberReader {
    
    private List<String> commentLines;

    
    private String propertyName;

    
    private String propertyValue;

    
    private char delimiter;

    
    static final String COMMENT_CHARS = "#!";

    
    private static final int HEX_RADIX = 16;

    
    private static final int UNICODE_LEN = 4;

    
    private static final char[] SEPARATORS = new char[]{'=', ':'};

    
    private static final char[] WHITE_SPACE = new char[]{' ', '\t', '\f'};

    
    public PropertiesReader(Reader reader) {
        this(reader, ',');
    }

    
    public PropertiesReader(Reader reader, char listDelimiter) {
        super(reader);
        commentLines = new ArrayList<String>();
        delimiter = listDelimiter;
    }

    
    boolean isCommentLine(String line) {
        String s = line.trim();
        
        return s.length() < 1 || COMMENT_CHARS.indexOf(s.charAt(0)) >= 0;
    }

    
    public String readProperty() throws IOException {
        commentLines.clear();
        StringBuilder buffer = new StringBuilder();

        while (true) {
            String line = readLine();
            if (line == null) {
                
                return null;
            }

            if (isCommentLine(line)) {
                commentLines.add(line);
                continue;
            }

            line = line.trim();

            if (checkCombineLines(line)) {
                line = line.substring(0, line.length() - 1);
                buffer.append(line);
            } else {
                buffer.append(line);
                break;
            }
        }
        return buffer.toString();
    }

    
    public boolean nextProperty() throws IOException {
        String line = readProperty();

        if (line == null) {
            return false; 
        }

        
        String[] property = parseProperty(line);
        propertyName = unescapeJava(property[0]);
        propertyValue = unescapeJava(property[1], delimiter);
        return true;
    }

    
    public List<String> getCommentLines() {
        return commentLines;
    }

    
    public String getPropertyName() {
        return propertyName;
    }

    
    public String getPropertyValue() {
        return propertyValue;
    }

    
    private boolean checkCombineLines(String line) {
        int bsCount = 0;
        for (int idx = line.length() - 1; idx >= 0 && line.charAt(idx) == '\\'; idx--) {
            bsCount++;
        }

        return bsCount % 2 == 1;
    }

    
    private String[] parseProperty(String line) {
        
        

        String[] result = new String[2];
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        
        
        
        
        
        int state = 0;

        for (int pos = 0; pos < line.length(); pos++) {
            char c = line.charAt(pos);

            switch (state) {
                case 0:
                    if (c == '\\') {
                        state = 1;
                    } else if (contains(WHITE_SPACE, c)) {
                        
                        state = 2;
                    } else if (contains(SEPARATORS, c)) {
                        
                        state = 3;
                    } else {
                        key.append(c);
                    }

                    break;

                case 1:
                    if (contains(SEPARATORS, c) || contains(WHITE_SPACE, c)) {
                        
                        key.append(c);
                    } else {
                        
                        key.append('\\');
                        key.append(c);
                    }

                    
                    state = 0;

                    break;

                case 2:
                    if (contains(WHITE_SPACE, c)) {
                        
                        state = 2;
                    } else if (contains(SEPARATORS, c)) {
                        
                        state = 3;
                    } else {
                        
                        value.append(c);

                        
                        state = 3;
                    }

                    break;

                case 3:
                    value.append(c);
                    break;
            }
        }

        result[0] = key.toString().trim();
        result[1] = value.toString().trim();

        return result;
    }

    
    protected static String unescapeJava(String str, char delimiter) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder out = new StringBuilder(sz);
        StringBuffer unicode = new StringBuffer(UNICODE_LEN);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                
                
                unicode.append(ch);
                if (unicode.length() == UNICODE_LEN) {
                    
                    
                    try {
                        int value = Integer.parseInt(unicode.toString(), HEX_RADIX);
                        out.append((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }

            if (hadSlash) {
                
                hadSlash = false;

                if (ch == '\\') {
                    out.append('\\');
                } else if (ch == '\'') {
                    out.append('\'');
                } else if (ch == '\"') {
                    out.append('"');
                } else if (ch == 'r') {
                    out.append('\r');
                } else if (ch == 'f') {
                    out.append('\f');
                } else if (ch == 't') {
                    out.append('\t');
                } else if (ch == 'n') {
                    out.append('\n');
                } else if (ch == 'b') {
                    out.append('\b');
                } else if (ch == delimiter) {
                    out.append('\\');
                    out.append(delimiter);
                } else if (ch == 'u') {
                    
                    inUnicode = true;
                } else {
                    out.append(ch);
                }

                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.append(ch);
        }

        if (hadSlash) {
            
            
            out.append('\\');
        }

        return out.toString();
    }

    
    public boolean contains(char[] array, char objectToFind) {
        if (array == null) {
            return false;
        }
        for (char anArray : array) {
            if (objectToFind == anArray) {
                return true;
            }
        }
        return false;
    }

    
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            
            ioe.printStackTrace();
            return null;
        }
    }

    
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuffer unicode = new StringBuffer(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                
                
                unicode.append(ch);
                if (unicode.length() == 4) {
                    
                    
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write('"');
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u': {
                        
                        inUnicode = true;
                        break;
                    }
                    default:
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            
            
            out.write('\\');
        }
    }
}
