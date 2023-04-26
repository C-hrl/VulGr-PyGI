

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



class DelegatingSettings implements Settings {

    
    List<Settings> delegates;

    
    public DelegatingSettings(List<Settings> delegates) {
        this.delegates = delegates;
    }

    public String get(String name) throws IllegalArgumentException {
        for (Settings delegate : delegates) {
            String value = delegate.get(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public Iterator list() {
        boolean workedAtAll = false;

        Set<Object> settingList = new HashSet<>();
        UnsupportedOperationException e = null;

        for (Settings delegate : delegates) {
            try {
                Iterator list = delegate.list();

                while (list.hasNext()) {
                    settingList.add(list.next());
                }

                workedAtAll = true;
            } catch (UnsupportedOperationException ex) {
                e = ex;
                
            }
        }

        if (!workedAtAll) {
            throw (e == null) ? new UnsupportedOperationException() : e;
        } else {
            return settingList.iterator();
        }
    }

    public Location getLocation(String name) {
        for (Settings delegate : delegates) {
            Location loc = delegate.getLocation(name);
            if (loc != null) {
                return loc;
            }
        }
        return Location.UNKNOWN;
    }
}
