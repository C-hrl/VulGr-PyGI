

package org.apache.struts2.util;



public class Timer {

    
    long current = System.currentTimeMillis();
    long start = current;


    
    public long getTime() {
        
        long now = System.currentTimeMillis();
        long time = now - current;

        
        current = now;

        return time;
    }

    public long getTotal() {
        
        return System.currentTimeMillis() - start;
    }
}
