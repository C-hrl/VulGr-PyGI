
package com.opensymphony.xwork2.util.classloader;


public interface ResourceStore {

    void write(final String pResourceName, final byte[] pResourceData);

    byte[] read(final String pResourceName);
}

