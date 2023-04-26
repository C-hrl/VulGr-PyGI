

package com.opensymphony.xwork2.inject;

import java.lang.reflect.Member;
import java.util.*;
import java.util.logging.Logger;


public final class ContainerBuilder {

    final Map<Key<?>, InternalFactory<?>> factories = new HashMap<>();
    final List<InternalFactory<?>> singletonFactories = new ArrayList<>();
    final List<Class<?>> staticInjections = new ArrayList<>();
    boolean created;
    boolean allowDuplicates = false;

    private static final InternalFactory<Container> CONTAINER_FACTORY =
            new InternalFactory<Container>() {
                public Container create(InternalContext context) {
                    return context.getContainer();
                }
            };

    private static final InternalFactory<Logger> LOGGER_FACTORY =
            new InternalFactory<Logger>() {
                public Logger create(InternalContext context) {
                    Member member = context.getExternalContext().getMember();
                    return member == null ? Logger.getAnonymousLogger()
                            : Logger.getLogger(member.getDeclaringClass().getName());
                }
            };

    
    public ContainerBuilder() {
        
        factories.put(Key.newInstance(Container.class, Container.DEFAULT_NAME), CONTAINER_FACTORY);

        
        factories.put(Key.newInstance(Logger.class, Container.DEFAULT_NAME), LOGGER_FACTORY);
    }

    
    private <T> ContainerBuilder factory(final Key<T> key,
                                         InternalFactory<? extends T> factory, Scope scope) {
        ensureNotCreated();
        checkKey(key);
        final InternalFactory<? extends T> scopedFactory = scope.scopeFactory(key.getType(), key.getName(), factory);
        factories.put(key, scopedFactory);
        if (scope == Scope.SINGLETON) {
            singletonFactories.add(new InternalFactory<T>() {
                public T create(InternalContext context) {
                    try {
                        context.setExternalContext(ExternalContext.newInstance(null, key, context.getContainerImpl()));
                        return scopedFactory.create(context);
                    } finally {
                        context.setExternalContext(null);
                    }
                }
            });
        }
        return this;
    }

    
    private void checkKey(Key<?> key) {
        if (factories.containsKey(key) && !allowDuplicates) {
            throw new DependencyException("Dependency mapping for " + key + " already exists.");
        }
    }

    
    public <T> ContainerBuilder factory(final Class<T> type, final String name,
                                        final Factory<? extends T> factory, Scope scope) {
        InternalFactory<T> internalFactory = new InternalFactory<T>() {

            public T create(InternalContext context) {
                try {
                    Context externalContext = context.getExternalContext();
                    return factory.create(externalContext);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String toString() {
                return new LinkedHashMap<String, Object>() {{
                    put("type", type);
                    put("name", name);
                    put("factory", factory);
                }}.toString();
            }
        };

        return factory(Key.newInstance(type, name), internalFactory, scope);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, Factory<? extends T> factory, Scope scope) {
        return factory(type, Container.DEFAULT_NAME, factory, scope);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, String name, Factory<? extends T> factory) {
        return factory(type, name, factory, Scope.PROTOTYPE);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, Factory<? extends T> factory) {
        return factory(type, Container.DEFAULT_NAME, factory, Scope.PROTOTYPE);
    }

    
    public <T> ContainerBuilder factory(final Class<T> type, final String name,
                                        final Class<? extends T> implementation, final Scope scope) {
        
        
        
        InternalFactory<? extends T> factory = new InternalFactory<T>() {

            volatile ContainerImpl.ConstructorInjector<? extends T> constructor;

            @SuppressWarnings("unchecked")
            public T create(InternalContext context) {
                if (constructor == null) {
                    this.constructor =
                            context.getContainerImpl().getConstructor(implementation);
                }
                return (T) constructor.construct(context, type);
            }

            @Override
            public String toString() {
                return new LinkedHashMap<String, Object>() {{
                    put("type", type);
                    put("name", name);
                    put("implementation", implementation);
                    put("scope", scope);
                }}.toString();
            }
        };

        return factory(Key.newInstance(type, name), factory, scope);
    }

    
    public <T> ContainerBuilder factory(final Class<T> type, String name,
                                        final Class<? extends T> implementation) {
        Scoped scoped = implementation.getAnnotation(Scoped.class);
        Scope scope = scoped == null ? Scope.PROTOTYPE : scoped.value();
        return factory(type, name, implementation, scope);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, Class<? extends T> implementation) {
        return factory(type, Container.DEFAULT_NAME, implementation);
    }

    
    public <T> ContainerBuilder factory(Class<T> type) {
        return factory(type, Container.DEFAULT_NAME, type);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, String name) {
        return factory(type, name, type);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, Class<? extends T> implementation, Scope scope) {
        return factory(type, Container.DEFAULT_NAME, implementation, scope);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, Scope scope) {
        return factory(type, Container.DEFAULT_NAME, type, scope);
    }

    
    public <T> ContainerBuilder factory(Class<T> type, String name, Scope scope) {
        return factory(type, name, type, scope);
    }

    
    public <T> ContainerBuilder alias(Class<T> type, String alias) {
        return alias(type, Container.DEFAULT_NAME, alias);
    }

    
    public <T> ContainerBuilder alias(Class<T> type, String name, String alias) {
        return alias(Key.newInstance(type, name), Key.newInstance(type, alias));
    }

    
    private <T> ContainerBuilder alias(final Key<T> key,
                                       final Key<T> aliasKey) {
        ensureNotCreated();
        checkKey(aliasKey);

        final InternalFactory<? extends T> scopedFactory = (InternalFactory<? extends T>) factories.get(key);
        if (scopedFactory == null) {
            throw new DependencyException("Dependency mapping for " + key + " doesn't exists.");
        }
        factories.put(aliasKey, scopedFactory);
        return this;
    }

    
    public ContainerBuilder constant(String name, String value) {
        return constant(String.class, name, value);
    }

    
    public ContainerBuilder constant(String name, int value) {
        return constant(int.class, name, value);
    }

    
    public ContainerBuilder constant(String name, long value) {
        return constant(long.class, name, value);
    }

    
    public ContainerBuilder constant(String name, boolean value) {
        return constant(boolean.class, name, value);
    }

    
    public ContainerBuilder constant(String name, double value) {
        return constant(double.class, name, value);
    }

    
    public ContainerBuilder constant(String name, float value) {
        return constant(float.class, name, value);
    }

    
    public ContainerBuilder constant(String name, short value) {
        return constant(short.class, name, value);
    }

    
    public ContainerBuilder constant(String name, char value) {
        return constant(char.class, name, value);
    }

    
    public ContainerBuilder constant(String name, Class value) {
        return constant(Class.class, name, value);
    }

    
    public <E extends Enum<E>> ContainerBuilder constant(String name, E value) {
        return constant(value.getDeclaringClass(), name, value);
    }

    
    private <T> ContainerBuilder constant(final Class<T> type, final String name, final T value) {
        InternalFactory<T> factory = new InternalFactory<T>() {
            public T create(InternalContext ignored) {
                return value;
            }

            @Override
            public String toString() {
                return new LinkedHashMap<String, Object>() {
                    {
                        put("type", type);
                        put("name", name);
                        put("value", value);
                    }
                }.toString();
            }
        };

        return factory(Key.newInstance(type, name), factory, Scope.PROTOTYPE);
    }

    
    public ContainerBuilder injectStatics(Class<?>... types) {
        staticInjections.addAll(Arrays.asList(types));
        return this;
    }

    
    public boolean contains(Class<?> type, String name) {
        return factories.containsKey(Key.newInstance(type, name));
    }

    
    public boolean contains(Class<?> type) {
        return contains(type, Container.DEFAULT_NAME);
    }

    
    public Container create(boolean loadSingletons) {
        ensureNotCreated();
        created = true;
        final ContainerImpl container = new ContainerImpl(new HashMap<>(factories));
        if (loadSingletons) {
            container.callInContext(new ContainerImpl.ContextualCallable<Void>() {
                public Void call(InternalContext context) {
                    for (InternalFactory<?> factory : singletonFactories) {
                        factory.create(context);
                    }
                    return null;
                }
            });
        }
        container.injectStatics(staticInjections);
        return container;
    }

    
    private void ensureNotCreated() {
        if (created) {
            throw new IllegalStateException("Container already created.");
        }
    }

    public void setAllowDuplicates(boolean val) {
        allowDuplicates = val;
    }

    
    public interface Command {

        
        void build(ContainerBuilder builder);
    }
}
