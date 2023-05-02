

package com.opensymphony.xwork2.inject.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.*;

import static com.opensymphony.xwork2.inject.util.ReferenceType.STRONG;


public abstract class ReferenceCache<K, V> extends ReferenceMap<K, V> {

    private static final long serialVersionUID = 0;

    transient ConcurrentMap<Object, Future<V>> futures = new ConcurrentHashMap<>();
    transient ThreadLocal<Future<V>> localFuture = new ThreadLocal<>();

    public ReferenceCache(ReferenceType keyReferenceType, ReferenceType valueReferenceType) {
        super(keyReferenceType, valueReferenceType);
    }

    
    public ReferenceCache() {
        super(STRONG, STRONG);
    }

    
    protected abstract V create(K key);

    V internalCreate(K key) {
        try {
            FutureTask<V> futureTask = new FutureTask<>(new CallableCreate(key));

            
            Object keyReference = referenceKey(key);
            Future<V> future = futures.putIfAbsent(keyReference, futureTask);
            if (future == null) {
                
                try {
                    if (localFuture.get() != null) {
                        throw new IllegalStateException("Nested creations within the same cache are not allowed.");
                    }
                    localFuture.set(futureTask);
                    futureTask.run();
                    V value = futureTask.get();
                    putStrategy().execute(this, keyReference, referenceValue(keyReference, value));
                    return value;
                } finally {
                    localFuture.remove();
                    futures.remove(keyReference);
                }
            } else {
                
                return future.get();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    
    @SuppressWarnings("unchecked")
    @Override
    public V get(final Object key) {
        V value = super.get(key);
        return (value == null) ? internalCreate((K) key) : value;
    }

    
    protected void cancel() {
        Future<V> future = localFuture.get();
        if (future == null) {
            throw new IllegalStateException("Not in create().");
        }
        future.cancel(false);
    }

    class CallableCreate implements Callable<V> {

        K key;

        public CallableCreate(K key) {
            this.key = key;
        }

        public V call() {
            
            V value = internalGet(key);
            if (value != null) {
                return value;
            }

            
            value = create(key);
            if (value == null) {
                throw new NullPointerException("create(K) returned null for: " + key);
            }
            return value;
        }
    }

    
    public static <K, V> ReferenceCache<K, V> of(
            ReferenceType keyReferenceType,
            ReferenceType valueReferenceType,
            final Function<? super K, ? extends V> function) {
        ensureNotNull(function);
        return new ReferenceCache<K, V>(keyReferenceType, valueReferenceType) {
            @Override
            protected V create(K key) {
                return function.apply(key);
            }

            private static final long serialVersionUID = 0;
        };
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        in.defaultReadObject();
        this.futures = new ConcurrentHashMap<>();
        this.localFuture = new ThreadLocal<>();
    }

}
