
package com.opensymphony.xwork2.util.location;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LocationImpl implements Location, Serializable {

    private final String uri;
    private final int line;
    private final int column;
    private final String description;
    
    
    static final LocationImpl UNKNOWN = new LocationImpl(null, null, -1, -1);

    
    public LocationImpl(String description, String uri) {
        this(description, uri, -1, -1);
    }

    
    public LocationImpl(String description, String uri, int line, int column) {
        if (StringUtils.isEmpty(uri)) {
            this.uri = null;
            this.line = -1;
            this.column = -1;
        } else {
            this.uri = uri;
            this.line = line;
            this.column = column;
        }
        this.description = StringUtils.trimToNull(description);
    }
    
    
    public LocationImpl(Location location) {
        this(location.getDescription(), location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }
    
    
    public LocationImpl(String description, Location location) {
        this(description, location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }
    
    
    public static LocationImpl get(Location location) {
        if (location instanceof LocationImpl) {
            return (LocationImpl)location;
        } else if (location == null) {
            return UNKNOWN;
        } else {
            return new LocationImpl(location);
        }
    }
    
    
    public String getDescription() {
        return this.description;
    }
    
    
    public String getURI() {
        return this.uri;
    }

    
    public int getLineNumber() {
        return this.line;
    }
    
    
    public int getColumnNumber() {
        return this.column;
    }
    
    
    public List<String> getSnippet(int padding) {
        List<String> snippet = new ArrayList<>();
        if (getLineNumber() > 0) {
            try {
                InputStream in = new URL(getURI()).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                
                int lineno = 0;
                int errno = getLineNumber();
                String line;
                while ((line = reader.readLine()) != null) {
                    lineno++;
                    if (lineno >= errno - padding && lineno <= errno + padding) {
                        snippet.add(line);
                    }
                }
            } catch (Exception ex) {
                
            }
        }
        return snippet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Location) {
            Location other = (Location)obj;
            return this.line == other.getLineNumber() && this.column == other.getColumnNumber()
                   && testEquals(this.uri, other.getURI())
                   && testEquals(this.description, other.getDescription());
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = line ^ column;
        if (uri != null) hash ^= uri.hashCode();
        if (description != null) hash ^= description.hashCode();
        
        return hash;
    }
    
    @Override
    public String toString() {
        return LocationUtils.toString(this);
    }
    
    
    private Object readResolve() {
        return this.equals(Location.UNKNOWN) ? Location.UNKNOWN : this;
    }
    
    private boolean testEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if ((object1 == null) || (object2 == null)) {
            return false;
        }
        return object1.equals(object2);
    }
}
