

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class CollectionAdapter extends AbstractAdapterElement {

    private Logger log = LogManager.getLogger(this.getClass());

    public CollectionAdapter() { }

    public CollectionAdapter(AdapterFactory rootAdapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(rootAdapterFactory, parent, propertyName, value);
    }

    protected List<Node> buildChildAdapters() {
        Collection values = (Collection) getPropertyValue();
        List<Node> children = new ArrayList<>(values.size());

        for (Object value : values) {
            Node childAdapter;
            if (value == null) {
                childAdapter = getAdapterFactory().adaptNullValue(this, "item");
            } else {
                childAdapter = getAdapterFactory().adaptNode(this, "item", value);
            }
            if (childAdapter != null)
                children.add(childAdapter);

            log.debug("{} adding adapter: {}", this, childAdapter);
        }

        return children;
    }
}
