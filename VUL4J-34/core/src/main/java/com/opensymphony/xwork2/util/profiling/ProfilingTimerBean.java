
package com.opensymphony.xwork2.util.profiling;

import java.util.ArrayList;
import java.util.List;


public class ProfilingTimerBean implements java.io.Serializable {

    private static final long serialVersionUID = -6180672043920208784L;

    List<ProfilingTimerBean> children = new ArrayList<>();
    ProfilingTimerBean parent = null;

    String resource;

    long startTime;
    long totalTime;

    public ProfilingTimerBean(String resource) {
        this.resource = resource;
    }

    protected void addParent(ProfilingTimerBean parent) {
        this.parent = parent;
    }

    public ProfilingTimerBean getParent() {
        return parent;
    }


    public void addChild(ProfilingTimerBean child) {
        children.add(child);
        child.addParent(this);
    }


    public void setStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    public void setEndTime() {
        this.totalTime = System.currentTimeMillis() - startTime;
    }

    public String getResource() {
        return resource;
    }

    
    public String getPrintable(long minTime) {
        return getPrintable("", minTime);
    }

    protected String getPrintable(String indent, long minTime) {
        
        if (totalTime >= minTime) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(indent);
            buffer.append("[" + totalTime + "ms] - " + resource);
            buffer.append("\n");

            for (ProfilingTimerBean aChildren : children) {
                buffer.append((aChildren).getPrintable(indent + "  ", minTime));
            }

            return buffer.toString();
        } else
            return "";
    }
}

