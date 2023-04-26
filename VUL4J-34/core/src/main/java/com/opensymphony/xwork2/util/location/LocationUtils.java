
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LocationUtils {
    
    
    public static final String UNKNOWN_STRING = "[unknown location]";

    private static List<WeakReference<LocationFinder>> finders = new ArrayList<>();
    
    
    public interface LocationFinder {
        
        Location getLocation(Object obj, String description);
    }

    private LocationUtils() {
        
    }
    
    
    public static String toString(Location location) {
        StringBuilder result = new StringBuilder();

        String description = location.getDescription();
        if (description != null) {
            result.append(description).append(" - ");
        }

        String uri = location.getURI();
        if (uri != null) {
            result.append(uri).append(':').append(location.getLineNumber()).append(':').append(location.getColumnNumber());
        } else {
            result.append(UNKNOWN_STRING);
        }
        
        return result.toString();
    }

    
    public static LocationImpl parse(String text) throws IllegalArgumentException {
        if (text == null || text.length() == 0) {
            return null;
        }

        
        String description;
        int uriStart = text.lastIndexOf(" - "); 
        if (uriStart > -1) {
            description = text.substring(0, uriStart);
            uriStart += 3; 
        } else {
            description = null;
            uriStart = 0;
        }
        
        try {
            int colSep = text.lastIndexOf(':');
            if (colSep > -1) {
                int column = Integer.parseInt(text.substring(colSep + 1));
                
                int lineSep = text.lastIndexOf(':', colSep - 1);
                if (lineSep > -1) {
                    int line = Integer.parseInt(text.substring(lineSep + 1, colSep));
                    return new LocationImpl(description, text.substring(uriStart, lineSep), line, column);
                }
            } else {
                
                if (text.endsWith(UNKNOWN_STRING)) {
                    return LocationImpl.UNKNOWN;
                }
            }
        } catch(Exception e) {
            
        }
        
        return LocationImpl.UNKNOWN;
    }

    
    public static boolean isKnown(Location location) {
        return location != null && !Location.UNKNOWN.equals(location);
    }

    
    public static boolean isUnknown(Location location) {
        return location == null || Location.UNKNOWN.equals(location);
    }

    
    public static void addFinder(LocationFinder finder) {
        if (finder == null) {
            return;
        }

        synchronized(LocationFinder.class) {
            
            
            List<WeakReference<LocationFinder>> newFinders = new ArrayList<>(finders);
            newFinders.add(new WeakReference<LocationFinder>(finder));
            finders = newFinders;
        }
    }
    
    
    public static Location getLocation(Object obj) {
        return getLocation(obj, null);
    }
    
    
    public static Location getLocation(Object obj, String description) {
        if (obj instanceof Location) {
            return (Location) obj;
        }
        
        if (obj instanceof Locatable) {
            return ((Locatable)obj).getLocation();
        }
        
        
        if (obj instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException)obj;
            if (spe.getSystemId() != null) {
                return new LocationImpl(description, spe.getSystemId(), spe.getLineNumber(), spe.getColumnNumber());
            } else {
                return Location.UNKNOWN;
            }
        }
        
        if (obj instanceof TransformerException) {
            TransformerException ex = (TransformerException)obj;
            SourceLocator locator = ex.getLocator();
            if (locator != null && locator.getSystemId() != null) {
                return new LocationImpl(description, locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
            } else {
                return Location.UNKNOWN;
            }
        }
        
        if (obj instanceof Locator) {
            Locator locator = (Locator)obj;
            if (locator.getSystemId() != null) {
                return new LocationImpl(description, locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber());
            } else {
                return Location.UNKNOWN;
            }
        }
        
        if (obj instanceof Element) {
            return LocationAttributes.getLocation((Element)obj);
        }

        List<WeakReference<LocationFinder>> currentFinders = finders; 
        int size = currentFinders.size();
        for (int i = 0; i < size; i++) {
            WeakReference<LocationFinder> ref = currentFinders.get(i);
            LocationFinder finder = ref.get();
            if (finder == null) {
                
                synchronized(LocationFinder.class) {
                    
                    List<WeakReference<LocationFinder>> newFinders = new ArrayList<>(finders);
                    newFinders.remove(ref);
                    finders = newFinders;
                }
            } else {
                Location result = finder.getLocation(obj, description);
                if (result != null) {
                    return result;
                }
            }
        }

        if (obj instanceof Throwable) {
        		Throwable t = (Throwable) obj;
        		StackTraceElement[] stack = t.getStackTrace();
        		if (stack != null && stack.length > 0) {
        			StackTraceElement trace = stack[0];
        			if (trace.getLineNumber() >= 0) {
	        			String uri = trace.getClassName();
	        			if (trace.getFileName() != null) {
	        				uri = uri.replace('.','/');
	        				uri = uri.substring(0, uri.lastIndexOf('/') + 1);
	        				uri = uri + trace.getFileName();
	        				URL url = ClassLoaderUtil.getResource(uri, LocationUtils.class);
	        				if (url != null) {
        						uri = url.toString();
	        				}
	        			}
	        			if (description == null) {
	        				StringBuilder sb = new StringBuilder();
	        				sb.append("Class: ").append(trace.getClassName()).append("\n");
	        				sb.append("File: ").append(trace.getFileName()).append("\n");
	        				sb.append("Method: ").append(trace.getMethodName()).append("\n");
	        				sb.append("Line: ").append(trace.getLineNumber());
	        				description = sb.toString();
	        			}
	        			return new LocationImpl(description, uri, trace.getLineNumber(), -1);
        			}
        		}
        }

        return Location.UNKNOWN;
    }
}
