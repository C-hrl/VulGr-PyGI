

package org.apache.struts2.views.xslt;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapAdapter extends AbstractAdapterElement {

    public MapAdapter() { }

    public MapAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Map value) {
        setContext( adapterFactory, parent, propertyName, value );
    }

    public Map map() {
        return (Map)getPropertyValue();
    }

    protected List<Node> buildChildAdapters() {
        List<Node> children = new ArrayList<>(map().entrySet().size());

        for (Object o : map().entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            EntryElement child = new EntryElement(getAdapterFactory(), this, "entry", key, value);
            children.add(child);
        }

        return children;
    }

    static class EntryElement extends AbstractAdapterElement {
        Object key, value;

        public EntryElement(  AdapterFactory adapterFactory,
                              AdapterNode parent, String propertyName, Object key, Object value ) {
            setContext( adapterFactory, parent, propertyName, null );
            this.key = key;
            this.value = value;
        }

        protected List<Node> buildChildAdapters() {
            List<Node> children = new ArrayList<>();
            children.add( getAdapterFactory().adaptNode( this, "key", key ) );
            children.add( getAdapterFactory().adaptNode( this, "value", value ) );
            return children;
        }
    }
}
