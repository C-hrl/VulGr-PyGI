

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;



public class ArrayAdapter extends AbstractAdapterElement {

    private Logger log = LogManager.getLogger(this.getClass());

    public ArrayAdapter() {
    }

    public ArrayAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    protected List<Node> buildChildAdapters() {
        List<Node> children = new ArrayList<>();
        Object[] values = (Object[]) getPropertyValue();

        for (Object value : values) {
            Node childAdapter = getAdapterFactory().adaptNode(this, "item", value);
            if (childAdapter != null)
                children.add(childAdapter);

            log.debug("{} adding adapter: {}", this, childAdapter);
        }

        return children;
    }
}
