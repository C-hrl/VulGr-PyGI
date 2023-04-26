

package org.apache.struts2.dispatcher;


public interface DispatcherListener {

    
    public void dispatcherInitialized(Dispatcher du);

    
    public void dispatcherDestroyed(Dispatcher du);
}
