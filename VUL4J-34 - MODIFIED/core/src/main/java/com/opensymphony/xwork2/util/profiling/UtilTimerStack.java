
package com.opensymphony.xwork2.util.profiling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



public class UtilTimerStack {

    
    protected static ThreadLocal<ProfilingTimerBean> current = new ThreadLocal<>();

    
    public static final String ACTIVATE_PROPERTY = "xwork.profile.activate";

    
    public static final String MIN_TIME = "xwork.profile.mintime";

    private static final Logger LOG = LogManager.getLogger(UtilTimerStack.class);

    
    private static boolean active;

    static {
        active = "true".equalsIgnoreCase(System.getProperty(ACTIVATE_PROPERTY));
    }

    
    public static void push(String name) {
        if (!isActive()) {
            return;
        }

        
        ProfilingTimerBean newTimer = new ProfilingTimerBean(name);
        newTimer.setStartTime();

        
        ProfilingTimerBean currentTimer = (ProfilingTimerBean) current.get();
        if (currentTimer != null) {
            currentTimer.addChild(newTimer);
        }

        
        current.set(newTimer);
    }

    
    public static void pop(String name) {
        if (!isActive()) {
            return;
        }

        ProfilingTimerBean currentTimer = current.get();

        
        if (currentTimer != null && name != null && name.equals(currentTimer.getResource())) {
            currentTimer.setEndTime();
            ProfilingTimerBean parent = currentTimer.getParent();
            
            if (parent == null) {
                printTimes(currentTimer);
                current.set(null); 
            } else {
                current.set(parent);
            }
        } else {
            
            if (currentTimer != null) {
                printTimes(currentTimer);
                current.set(null); 
                LOG.warn("Unmatched Timer. Was expecting {}, instead got {}", currentTimer.getResource(), name);
            }
        }
    }

    
    private static void printTimes(ProfilingTimerBean currentTimer) {
        LOG.info(currentTimer.getPrintable(getMinTime()));
    }

    
    private static long getMinTime() {
        try {
            return Long.parseLong(System.getProperty(MIN_TIME, "0"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    
    public static boolean isActive() {
        return active;
    }

    
    public static void setActive(boolean active) {
        if (active) {
            System.setProperty(ACTIVATE_PROPERTY, "true");
        } else {
            System.clearProperty(ACTIVATE_PROPERTY);
        }
        UtilTimerStack.active = active;
    }


    
    public static <T> T profile(String name, ProfilingBlock<T> block) throws Exception {
        UtilTimerStack.push(name);
        try {
            return block.doProfiling();
        } finally {
            UtilTimerStack.pop(name);
        }
    }

    
    public static interface ProfilingBlock<T> {

        
        T doProfiling() throws Exception;
    }
}
