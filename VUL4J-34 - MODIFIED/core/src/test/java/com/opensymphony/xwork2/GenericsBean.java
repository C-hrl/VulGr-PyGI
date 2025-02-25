package com.opensymphony.xwork2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GenericsBean {
    private List<Double> blubb;
    private List<Double> getterList;
    private Map<Double, Integer> genericMap = new HashMap<>();
    private Map<Double, List<Integer>> extendedMap = new HashMap<>();

    
    public List<Double> getDoubles() {
        return blubb;
    }

    
    public void setDoubles(List<Double> doubles) {
        this.blubb = doubles;
    }

    public Map<Double, Integer> getGenericMap() {
        return genericMap;
    }

    public void setGenericMap(Map<Double, Integer> genericMap) {
        this.genericMap = genericMap;
    }

    public List<Double> getGetterList() {
        if ( getterList == null ) {
            getterList = new ArrayList<>(1);
            getterList.add(42.42);
        }
        return getterList;
    }

    public Map<Double, List<Integer>> getExtendedMap() {
        return extendedMap;
    }

    public void setExtendedMap(Map<Double, List<Integer>> extendedMap) {
        this.extendedMap = extendedMap;
    }
}
