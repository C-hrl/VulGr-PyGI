

package com.opensymphony.xwork2.inject;

import java.util.concurrent.Callable;


public enum Scope {

    
    PROTOTYPE {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name,
                                                      InternalFactory<? extends T> factory) {
            return factory;
        }
    },

    
    SINGLETON {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>() {
                T instance;

                public T create(InternalContext context) {
                    synchronized (context.getContainer()) {
                        if (instance == null) {
                            instance = factory.create(context);
                        }
                        return instance;
                    }
                }

                @Override
                public String toString() {
                    return factory.toString();
                }
            };
        }
    },

    
    THREAD {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(Class<T> type, String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>() {
                final ThreadLocal<T> threadLocal = new ThreadLocal<>();

                public T create(final InternalContext context) {
                    T t = threadLocal.get();
                    if (t == null) {
                        t = factory.create(context);
                        threadLocal.set(t);
                    }
                    return t;
                }

                @Override
                public String toString() {
                    return factory.toString();
                }
            };
        }
    },

    
    REQUEST {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>() {
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInRequest(
                                type, name, toCallable(context, factory));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String toString() {
                    return factory.toString();
                }
            };
        }
    },

    
    SESSION {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>() {
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInSession(
                                type, name, toCallable(context, factory));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String toString() {
                    return factory.toString();
                }
            };
        }
    },

    
    WIZARD {
        @Override
        <T> InternalFactory<? extends T> scopeFactory(final Class<T> type, final String name, final InternalFactory<? extends T> factory) {
            return new InternalFactory<T>() {
                public T create(InternalContext context) {
                    Strategy strategy = context.getScopeStrategy();
                    try {
                        return strategy.findInWizard(
                                type, name, toCallable(context, factory));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String toString() {
                    return factory.toString();
                }
            };
        }
    };

    <T> Callable<? extends T> toCallable(final InternalContext context,
                                         final InternalFactory<? extends T> factory) {
        return new Callable<T>() {
            public T call() throws Exception {
                return factory.create(context);
            }
        };
    }

    
    abstract <T> InternalFactory<? extends T> scopeFactory(
            Class<T> type, String name, InternalFactory<? extends T> factory);

    
    public interface Strategy {

        
        <T> T findInRequest(Class<T> type, String name,
                            Callable<? extends T> factory) throws Exception;

        
        <T> T findInSession(Class<T> type, String name,
                            Callable<? extends T> factory) throws Exception;

        
        <T> T findInWizard(Class<T> type, String name,
                           Callable<? extends T> factory) throws Exception;
    }
}
