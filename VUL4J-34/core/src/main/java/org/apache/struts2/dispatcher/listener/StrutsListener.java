
package org.apache.struts2.dispatcher.listener;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


public class StrutsListener implements ServletContextListener {
    private PrepareOperations prepare;

    public void contextInitialized(ServletContextEvent sce) {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ListenerHostConfig config = new ListenerHostConfig(sce.getServletContext());
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(dispatcher);
            sce.getServletContext().setAttribute(StrutsStatics.SERVLET_DISPATCHER, dispatcher);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        prepare.cleanupDispatcher();
    }
}
