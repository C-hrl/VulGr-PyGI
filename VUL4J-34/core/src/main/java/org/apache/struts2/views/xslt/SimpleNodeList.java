

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public class SimpleNodeList implements NodeList {
    private Logger log = LogManager.getLogger(SimpleNodeList.class);

    private List<Node> nodes;

    public SimpleNodeList(List<Node> nodes) {
        this.nodes = nodes;
    }

    public int getLength() {
        if (log.isTraceEnabled()) {
            log.trace("getLength: {}", nodes.size());
        }
        return nodes.size();
    }

    public Node item(int i) {
        log.trace("getItem: {}", i);
        return nodes.get(i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SimpleNodeList: [");
        for (int i = 0; i < getLength(); i++)
            sb.append(item(i).getNodeName()).append(',');
        sb.append("]");
        return sb.toString();
    }
}
