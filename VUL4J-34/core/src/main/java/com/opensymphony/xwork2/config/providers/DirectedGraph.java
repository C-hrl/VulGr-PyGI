package com.opensymphony.xwork2.config.providers;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class DirectedGraph<T> implements Iterable<T> {
    private final Map<T, Set<T>> mGraph = new HashMap<T, Set<T>>();

    
    public boolean addNode(T node) {
        
        if (mGraph.containsKey(node))
            return false;

        
        mGraph.put(node, new HashSet<T>());
        return true;
    }

    
    public void addEdge(T start, T dest) {
        
        if (!mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        } else if (!mGraph.containsKey(dest)) {
            throw new NoSuchElementException("The destination node does not exist in the graph.");
        }

        
        mGraph.get(start).add(dest);
    }

    
    public void removeEdge(T start, T dest) {
        
        if (!mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        } else if (!mGraph.containsKey(dest)) {
            throw new NoSuchElementException("The destination node does not exist in the graph.");
        }

        mGraph.get(start).remove(dest);
    }

    
    public boolean edgeExists(T start, T end) {
        
        if (!mGraph.containsKey(start)) {
            throw new NoSuchElementException("The start node does not exist in the graph.");
        } else if (!mGraph.containsKey(end)) {
            throw new NoSuchElementException("The end node does not exist in the graph.");
        }

        return mGraph.get(start).contains(end);
    }

    
    public Set<T> edgesFrom(T node) {
        
        Set<T> arcs = mGraph.get(node);
        if (arcs == null)
            throw new NoSuchElementException("Source node does not exist.");

        return Collections.unmodifiableSet(arcs);
    }

    
    public Iterator<T> iterator() {
        return mGraph.keySet().iterator();
    }

    
    public int size() {
        return mGraph.size();
    }

    
    public boolean isEmpty() {
        return mGraph.isEmpty();
    }
}
