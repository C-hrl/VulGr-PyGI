
package actions.osgi;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.ResultPath;
import org.apache.struts2.osgi.interceptor.BundleContextAware;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;


@ResultPath("/content")
public class BundlesAction extends ActionSupport implements BundleContextAware {
    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public Bundle[] getBundles() {
        return bundleContext.getBundles();
    }
}
