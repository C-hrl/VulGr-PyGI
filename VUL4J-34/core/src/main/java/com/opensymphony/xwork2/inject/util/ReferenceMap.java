

package com.opensymphony.xwork2.inject.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.opensymphony.xwork2.inject.util.ReferenceType.STRONG;


@SuppressWarnings("unchecked")
public class ReferenceMap<K, V> implements Map<K, V>, Serializable {

    private static final long serialVersionUID = 0;

    transient ConcurrentMap<Object, Object> delegate;

    final ReferenceType keyReferenceType;
    final ReferenceType valueReferenceType;

    
    public ReferenceMap(ReferenceType keyReferenceType,
                        ReferenceType valueReferenceType) {
        ensureNotNull(keyReferenceType, valueReferenceType);

        if (keyReferenceType == ReferenceType.PHANTOM || valueReferenceType == ReferenceType.PHANTOM) {
            throw new IllegalArgumentException("Phantom references not supported.");
        }

        this.delegate = new ConcurrentHashMap<>();
        this.keyReferenceType = keyReferenceType;
        this.valueReferenceType = valueReferenceType;
    }

    V internalGet(K key) {
        Object valueReference = delegate.get(makeKeyReferenceAware(key));
        return valueReference == null ? null : (V) dereferenceValue(valueReference);
    }

    public V get(final Object key) {
        ensureNotNull(key);
        return internalGet((K) key);
    }

    V execute(Strategy strategy, K key, V value) {
        ensureNotNull(key, value);
        Object keyReference = referenceKey(key);
        Object valueReference = strategy.execute(this, keyReference, referenceValue(keyReference, value));
        return valueReference == null ? null : (V) dereferenceValue(valueReference);
    }

    public V put(K key, V value) {
        return execute(putStrategy(), key, value);
    }

    public V remove(Object key) {
        ensureNotNull(key);
        Object referenceAwareKey = makeKeyReferenceAware(key);
        Object valueReference = delegate.remove(referenceAwareKey);
        return valueReference == null ? null : (V) dereferenceValue(valueReference);
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        ensureNotNull(key);
        Object referenceAwareKey = makeKeyReferenceAware(key);
        return delegate.containsKey(referenceAwareKey);
    }

    public boolean containsValue(Object value) {
        ensureNotNull(value);
        for (Object valueReference : delegate.values()) {
            if (value.equals(dereferenceValue(valueReference))) {
                return true;
            }
        }
        return false;
    }

    public void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear() {
        delegate.clear();
    }

    
    public Set<K> keySet() {
        return Collections.unmodifiableSet(dereferenceKeySet(delegate.keySet()));
    }

    
    public Collection<V> values() {
        return Collections.unmodifiableCollection(dereferenceValues(delegate.values()));
    }

    public V putIfAbsent(K key, V value) {
        
        
        return execute(putIfAbsentStrategy(), key, value);
    }

    public boolean remove(Object key, Object value) {
        ensureNotNull(key, value);
        Object referenceAwareKey = makeKeyReferenceAware(key);
        Object referenceAwareValue = makeValueReferenceAware(value);
        return delegate.remove(referenceAwareKey, referenceAwareValue);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        ensureNotNull(key, oldValue, newValue);
        Object keyReference = referenceKey(key);

        Object referenceAwareOldValue = makeValueReferenceAware(oldValue);
        return delegate.replace(keyReference, referenceAwareOldValue, referenceValue(keyReference, newValue));
    }

    public V replace(K key, V value) {
        
        
        return execute(replaceStrategy(), key, value);
    }

    
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entrySet = new HashSet<>();
        for (Map.Entry<Object, Object> entry : delegate.entrySet()) {
            Map.Entry<K, V> dereferenced = dereferenceEntry(entry);
            if (dereferenced != null) {
                entrySet.add(dereferenced);
            }
        }
        return Collections.unmodifiableSet(entrySet);
    }

    
    Entry dereferenceEntry(Map.Entry<Object, Object> entry) {
        K key = dereferenceKey(entry.getKey());
        V value = dereferenceValue(entry.getValue());
        return (key == null || value == null) ? null : new Entry(key, value);
    }

    
    Object referenceKey(K key) {
        switch (keyReferenceType) {
            case STRONG:
                return key;
            case SOFT:
                return new SoftKeyReference(key);
            case WEAK:
                return new WeakKeyReference(key);
            default:
                throw new AssertionError();
        }
    }

    
    K dereferenceKey(Object o) {
        return (K) dereference(keyReferenceType, o);
    }

    
    V dereferenceValue(Object o) {
        return (V) dereference(valueReferenceType, o);
    }

    
    Object dereference(ReferenceType referenceType, Object reference) {
        return referenceType == STRONG ? reference : ((Reference) reference).get();
    }

    
    Object referenceValue(Object keyReference, Object value) {
        switch (valueReferenceType) {
            case STRONG:
                return value;
            case SOFT:
                return new SoftValueReference(keyReference, value);
            case WEAK:
                return new WeakValueReference(keyReference, value);
            default:
                throw new AssertionError();
        }
    }

    
    Set<K> dereferenceKeySet(Set keyReferences) {
        return keyReferenceType == STRONG
                ? keyReferences
                : dereferenceCollection(keyReferenceType, keyReferences, new HashSet());
    }

    
    Collection<V> dereferenceValues(Collection valueReferences) {
        return valueReferenceType == STRONG
                ? valueReferences
                : dereferenceCollection(valueReferenceType, valueReferences,
                new ArrayList(valueReferences.size()));
    }

    
    Object makeKeyReferenceAware(Object o) {
        return keyReferenceType == STRONG ? o : new KeyReferenceAwareWrapper(o);
    }

    
    Object makeValueReferenceAware(Object o) {
        return valueReferenceType == STRONG ? o : new ReferenceAwareWrapper(o);
    }

    
    <T extends Collection<Object>> T dereferenceCollection(ReferenceType referenceType, T in, T out) {
        for (Object reference : in) {
            out.add(dereference(referenceType, reference));
        }
        return out;
    }

    
    interface InternalReference {
    }

    static int keyHashCode(Object key) {
        return System.identityHashCode(key);
    }

    
    static boolean referenceEquals(Reference r, Object o) {
        
        if (o instanceof InternalReference) {
            
            if (o == r) {
                return true;
            }

            
            Object referent = ((Reference) o).get();
            return referent != null && referent == r.get();
        }

        
        return ((ReferenceAwareWrapper) o).unwrap() == r.get();
    }

    
    static class ReferenceAwareWrapper {

        Object wrapped;

        ReferenceAwareWrapper(Object wrapped) {
            this.wrapped = wrapped;
        }

        Object unwrap() {
            return wrapped;
        }

        @Override
        public int hashCode() {
            return wrapped.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            
            return obj.equals(this);
        }
    }

    
    static class KeyReferenceAwareWrapper extends ReferenceAwareWrapper {

        public KeyReferenceAwareWrapper(Object wrapped) {
            super(wrapped);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(wrapped);
        }
    }

    class SoftKeyReference extends FinalizableSoftReference<Object> implements InternalReference {

        int hashCode;

        public SoftKeyReference(Object key) {
            super(key);
            this.hashCode = keyHashCode(key);
        }

        public void finalizeReferent() {
            delegate.remove(this);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object o) {
            return referenceEquals(this, o);
        }
    }

    class WeakKeyReference extends FinalizableWeakReference<Object> implements InternalReference {

        int hashCode;

        public WeakKeyReference(Object key) {
            super(key);
            this.hashCode = keyHashCode(key);
        }

        public void finalizeReferent() {
            delegate.remove(this);
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        public boolean equals(Object o) {
            return referenceEquals(this, o);
        }
    }

    class SoftValueReference extends FinalizableSoftReference<Object> implements InternalReference {

        Object keyReference;

        public SoftValueReference(Object keyReference, Object value) {
            super(value);
            this.keyReference = keyReference;
        }

        public void finalizeReferent() {
            delegate.remove(keyReference, this);
        }

        @Override
        public boolean equals(Object obj) {
            return referenceEquals(this, obj);
        }
    }

    class WeakValueReference extends FinalizableWeakReference<Object> implements InternalReference {

        Object keyReference;

        public WeakValueReference(Object keyReference, Object value) {
            super(value);
            this.keyReference = keyReference;
        }

        public void finalizeReferent() {
            delegate.remove(keyReference, this);
        }

        @Override
        public boolean equals(Object obj) {
            return referenceEquals(this, obj);
        }
    }

    protected interface Strategy {
        public Object execute(ReferenceMap map, Object keyReference, Object valueReference);
    }

    protected Strategy putStrategy() {
        return PutStrategy.PUT;
    }

    protected Strategy putIfAbsentStrategy() {
        return PutStrategy.PUT_IF_ABSENT;
    }

    protected Strategy replaceStrategy() {
        return PutStrategy.REPLACE;
    }

    private enum PutStrategy implements Strategy {
        PUT {
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.put(keyReference, valueReference);
            }
        },

        REPLACE {
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.replace(keyReference, valueReference);
            }
        },

        PUT_IF_ABSENT {
            public Object execute(ReferenceMap map, Object keyReference, Object valueReference) {
                return map.delegate.putIfAbsent(keyReference, valueReference);
            }
        };
    }

    private static PutStrategy defaultPutStrategy;

    protected PutStrategy getPutStrategy() {
        return defaultPutStrategy;
    }


    class Entry implements Map.Entry<K, V> {

        K key;
        V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            return put(key, value);
        }

        @Override
        public int hashCode() {
            return key.hashCode() * 31 + value.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ReferenceMap.Entry)) {
                return false;
            }

            Entry entry = (Entry) o;
            return key.equals(entry.key) && value.equals(entry.value);
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    static void ensureNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
    }

    static void ensureNotNull(Object... array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                throw new NullPointerException("Argument #" + i + " is null.");
            }
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        for (Map.Entry<Object, Object> entry : delegate.entrySet()) {
            Object key = dereferenceKey(entry.getKey());
            Object value = dereferenceValue(entry.getValue());

            
            if (key != null && value != null) {
                out.writeObject(key);
                out.writeObject(value);
            }
        }
        out.writeObject(null);
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        int size = in.readInt();
        this.delegate = new ConcurrentHashMap<Object, Object>(size);
        while (true) {
            K key = (K) in.readObject();
            if (key == null) {
                break;
            }
            V value = (V) in.readObject();
            put(key, value);
        }
    }

}
