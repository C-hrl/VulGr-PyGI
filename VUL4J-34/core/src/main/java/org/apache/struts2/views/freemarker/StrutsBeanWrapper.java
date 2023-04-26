

package org.apache.struts2.views.freemarker;

import java.util.Map;
import java.util.Set;

import freemarker.core.CollectionAndSequence;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.MapModel;
import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;


public class StrutsBeanWrapper extends BeansWrapper {
    private boolean altMapWrapper;

    public StrutsBeanWrapper(boolean altMapWrapper) {
        this.altMapWrapper = altMapWrapper;
    }

    protected ModelFactory getModelFactory(Class clazz) {
        
        if (altMapWrapper && Map.class.isAssignableFrom(clazz)) {
            return FriendlyMapModel.FACTORY;
        }

        return super.getModelFactory(clazz);
    }

    
    private final static class FriendlyMapModel extends MapModel implements TemplateHashModelEx {
        static final ModelFactory FACTORY = new ModelFactory() {
            public TemplateModel create(Object object, ObjectWrapper wrapper) {
                return new FriendlyMapModel((Map) object, (BeansWrapper) wrapper);
            }
        };

        public FriendlyMapModel(Map map, BeansWrapper wrapper) {
            super(map, wrapper);
        }

        public boolean isEmpty() {
            return ((Map) object).isEmpty();
        }

        protected Set keySet() {
            return ((Map) object).keySet();
        }

        public TemplateCollectionModel values() {
            return new CollectionAndSequence(new SimpleSequence(((Map) object).values(), wrapper));
        }
    }
}
