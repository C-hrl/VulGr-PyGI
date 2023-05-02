

package com.opensymphony.xwork2.inject;

import com.opensymphony.xwork2.inject.util.ReferenceCache;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.security.AccessControlException;
import java.util.*;
import java.util.Map.Entry;


class ContainerImpl implements Container {

    final Map<Key<?>, InternalFactory<?>> factories;
    final Map<Class<?>, Set<String>> factoryNamesByType;

    ContainerImpl(Map<Key<?>, InternalFactory<?>> factories) {
        this.factories = factories;
        Map<Class<?>, Set<String>> map = new HashMap<>();
        for (Key<?> key : factories.keySet()) {
            Set<String> names = map.get(key.getType());
            if (names == null) {
                names = new HashSet<>();
                map.put(key.getType(), names);
            }
            names.add(key.getName());
        }

        for (Entry<Class<?>, Set<String>> entry : map.entrySet()) {
            entry.setValue(Collections.unmodifiableSet(entry.getValue()));
        }

        this.factoryNamesByType = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    <T> InternalFactory<? extends T> getFactory(Key<T> key) {
        return (InternalFactory<T>) factories.get(key);
    }

    
    final Map<Class<?>, List<Injector>> injectors =
            new ReferenceCache<Class<?>, List<Injector>>() {
                @Override
                protected List<Injector> create(Class<?> key) {
                    List<Injector> injectors = new ArrayList<>();
                    addInjectors(key, injectors);
                    return injectors;
                }
            };

    
    void addInjectors(Class clazz, List<Injector> injectors) {
        if (clazz == Object.class) {
            return;
        }

        
        addInjectors(clazz.getSuperclass(), injectors);

        
        addInjectorsForFields(clazz.getDeclaredFields(), false, injectors);
        addInjectorsForMethods(clazz.getDeclaredMethods(), false, injectors);
    }

    void injectStatics(List<Class<?>> staticInjections) {
        final List<Injector> injectors = new ArrayList<>();

        for (Class<?> clazz : staticInjections) {
            addInjectorsForFields(clazz.getDeclaredFields(), true, injectors);
            addInjectorsForMethods(clazz.getDeclaredMethods(), true, injectors);
        }

        callInContext(new ContextualCallable<Void>() {
            public Void call(InternalContext context) {
                for (Injector injector : injectors) {
                    injector.inject(context, null);
                }
                return null;
            }
        });
    }

    void addInjectorsForMethods(Method[] methods, boolean statics, List<Injector> injectors) {
        addInjectorsForMembers(Arrays.asList(methods), statics, injectors,
                new InjectorFactory<Method>() {
                    public Injector create(ContainerImpl container, Method method,
                                           String name) throws MissingDependencyException {
                        return new MethodInjector(container, method, name);
                    }
                });
    }

    void addInjectorsForFields(Field[] fields, boolean statics, List<Injector> injectors) {
        addInjectorsForMembers(Arrays.asList(fields), statics, injectors,
                new InjectorFactory<Field>() {
                    public Injector create(ContainerImpl container, Field field,
                                           String name) throws MissingDependencyException {
                        return new FieldInjector(container, field, name);
                    }
                });
    }

    <M extends Member & AnnotatedElement> void addInjectorsForMembers(
            List<M> members, boolean statics, List<Injector> injectors, InjectorFactory<M> injectorFactory) {
        for (M member : members) {
            if (isStatic(member) == statics) {
                Inject inject = member.getAnnotation(Inject.class);
                if (inject != null) {
                    try {
                        injectors.add(injectorFactory.create(this, member, inject.value()));
                    } catch (MissingDependencyException e) {
                        if (inject.required()) {
                            throw new DependencyException(e);
                        }
                    }
                }
            }
        }
    }

    interface InjectorFactory<M extends Member & AnnotatedElement> {

        Injector create(ContainerImpl container, M member, String name)
                throws MissingDependencyException;
    }

    private boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    static class FieldInjector implements Injector {

        final Field field;
        final InternalFactory<?> factory;
        final ExternalContext<?> externalContext;

        public FieldInjector(ContainerImpl container, Field field, String name)
                throws MissingDependencyException {
            this.field = field;
            if (!field.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    field.setAccessible(true);
                } catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access field: "
                            + field.getDeclaringClass().getName() + "(" + field.getName() + ")", e);
                }
            }

            Key<?> key = Key.newInstance(field.getType(), name);
            factory = container.getFactory(key);
            if (factory == null) {
                throw new MissingDependencyException("No mapping found for dependency " + key + " in " + field + ".");
            }

            this.externalContext = ExternalContext.newInstance(field, key, container);
        }

        public void inject(InternalContext context, Object o) {
            ExternalContext<?> previous = context.getExternalContext();
            context.setExternalContext(externalContext);
            try {
                field.set(o, factory.create(context));
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            } finally {
                context.setExternalContext(previous);
            }
        }
    }

    
    <M extends AccessibleObject & Member> ParameterInjector<?>[]
    getParametersInjectors(M member, Annotation[][] annotations, Class[] parameterTypes, String defaultName) throws MissingDependencyException {
        List<ParameterInjector<?>> parameterInjectors = new ArrayList<>();

        Iterator<Annotation[]> annotationsIterator = Arrays.asList(annotations).iterator();
        for (Class<?> parameterType : parameterTypes) {
            Inject annotation = findInject(annotationsIterator.next());
            String name = annotation == null ? defaultName : annotation.value();
            Key<?> key = Key.newInstance(parameterType, name);
            parameterInjectors.add(createParameterInjector(key, member));
        }

        return toArray(parameterInjectors);
    }

    <T> ParameterInjector<T> createParameterInjector(Key<T> key, Member member) throws MissingDependencyException {
        InternalFactory<? extends T> factory = getFactory(key);
        if (factory == null) {
            throw new MissingDependencyException("No mapping found for dependency " + key + " in " + member + ".");
        }

        ExternalContext<T> externalContext = ExternalContext.newInstance(member, key, this);
        return new ParameterInjector<T>(externalContext, factory);
    }

    @SuppressWarnings("unchecked")
    private ParameterInjector<?>[] toArray(List<ParameterInjector<?>> parameterInjections) {
        return parameterInjections.toArray(new ParameterInjector[parameterInjections.size()]);
    }

    
    Inject findInject(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Inject.class) {
                return Inject.class.cast(annotation);
            }
        }
        return null;
    }

    static class MethodInjector implements Injector {

        final Method method;
        final ParameterInjector<?>[] parameterInjectors;

        public MethodInjector(ContainerImpl container, Method method, String name) throws MissingDependencyException {
            this.method = method;
            if (!method.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    method.setAccessible(true);
                } catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access method: "
                            + name + "(" + method.getName() + ")", e);
                }
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                throw new DependencyException(method + " has no parameters to inject.");
            }
            parameterInjectors = container.getParametersInjectors(
                    method, method.getParameterAnnotations(), parameterTypes, name);
        }

        public void inject(InternalContext context, Object o) {
            try {
                method.invoke(o, getParameters(method, context, parameterInjectors));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    Map<Class<?>, ConstructorInjector> constructors =
            new ReferenceCache<Class<?>, ConstructorInjector>() {
                @Override
                @SuppressWarnings("unchecked")
                protected ConstructorInjector<?> create(Class<?> implementation) {
                    return new ConstructorInjector(ContainerImpl.this, implementation);
                }
            };

    static class ConstructorInjector<T> {

        final Class<T> implementation;
        final List<Injector> injectors;
        final Constructor<T> constructor;
        final ParameterInjector<?>[] parameterInjectors;

        ConstructorInjector(ContainerImpl container, Class<T> implementation) {
            this.implementation = implementation;

            constructor = findConstructorIn(implementation);
            if (!constructor.isAccessible()) {
                SecurityManager sm = System.getSecurityManager();
                try {
                    if (sm != null) {
                        sm.checkPermission(new ReflectPermission("suppressAccessChecks"));
                    }
                    constructor.setAccessible(true);
                } catch (AccessControlException e) {
                    throw new DependencyException("Security manager in use, could not access constructor: "
                            + implementation.getName() + "(" + constructor.getName() + ")", e);
                }
            }

            MissingDependencyException exception = null;
            Inject inject = null;
            ParameterInjector<?>[] parameters = null;

            try {
                inject = constructor.getAnnotation(Inject.class);
                parameters = constructParameterInjector(inject, container, constructor);
            } catch (MissingDependencyException e) {
                exception = e;
            }
            parameterInjectors = parameters;

            if (exception != null) {
                if (inject != null && inject.required()) {
                    throw new DependencyException(exception);
                }
            }
            injectors = container.injectors.get(implementation);
        }

        ParameterInjector<?>[] constructParameterInjector(
                Inject inject, ContainerImpl container, Constructor<T> constructor) throws MissingDependencyException {
            return constructor.getParameterTypes().length == 0
                    ? null 
                    : container.getParametersInjectors(
                    constructor,
                    constructor.getParameterAnnotations(),
                    constructor.getParameterTypes(),
                    inject.value()
            );
        }

        @SuppressWarnings("unchecked")
        private Constructor<T> findConstructorIn(Class<T> implementation) {
            Constructor<T> found = null;
            Constructor<T>[] declaredConstructors = (Constructor<T>[]) implementation.getDeclaredConstructors();
            for (Constructor<T> constructor : declaredConstructors) {
                if (constructor.getAnnotation(Inject.class) != null) {
                    if (found != null) {
                        throw new DependencyException("More than one constructor annotated"
                                + " with @Inject found in " + implementation + ".");
                    }
                    found = constructor;
                }
            }
            if (found != null) {
                return found;
            }

            
            
            try {
                return implementation.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new DependencyException("Could not find a suitable constructor in " + implementation.getName() + ".");
            }
        }

        
        Object construct(InternalContext context, Class<? super T> expectedType) {
            ConstructionContext<T> constructionContext = context.getConstructionContext(this);

            
            if (constructionContext.isConstructing()) {
                
                
                return constructionContext.createProxy(expectedType);
            }

            
            
            T t = constructionContext.getCurrentReference();
            if (t != null) {
                return t;
            }

            try {
                
                constructionContext.startConstruction();
                try {
                    Object[] parameters = getParameters(constructor, context, parameterInjectors);
                    t = constructor.newInstance(parameters);
                    constructionContext.setProxyDelegates(t);
                } finally {
                    constructionContext.finishConstruction();
                }

                
                
                constructionContext.setCurrentReference(t);

                
                for (Injector injector : injectors) {
                    injector.inject(context, t);
                }

                return t;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } finally {
                constructionContext.removeCurrentReference();
            }
        }
    }

    static class ParameterInjector<T> {

        final ExternalContext<T> externalContext;
        final InternalFactory<? extends T> factory;

        public ParameterInjector(ExternalContext<T> externalContext, InternalFactory<? extends T> factory) {
            this.externalContext = externalContext;
            this.factory = factory;
        }

        T inject(Member member, InternalContext context) {
            ExternalContext<?> previous = context.getExternalContext();
            context.setExternalContext(externalContext);
            try {
                return factory.create(context);
            } finally {
                context.setExternalContext(previous);
            }
        }
    }

    private static Object[] getParameters(Member member, InternalContext context, ParameterInjector[] parameterInjectors) {
        if (parameterInjectors == null) {
            return null;
        }

        Object[] parameters = new Object[parameterInjectors.length];
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = parameterInjectors[i].inject(member, context);
        }
        return parameters;
    }

    void inject(Object o, InternalContext context) {
        List<Injector> injectors = this.injectors.get(o.getClass());
        for (Injector injector : injectors) {
            injector.inject(context, o);
        }
    }

    <T> T inject(Class<T> implementation, InternalContext context) {
        try {
            ConstructorInjector<T> constructor = getConstructor(implementation);
            return implementation.cast(constructor.construct(context, implementation));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <T> T getInstance(Class<T> type, String name, InternalContext context) {
        ExternalContext<?> previous = context.getExternalContext();
        Key<T> key = Key.newInstance(type, name);
        context.setExternalContext(ExternalContext.newInstance(null, key, this));
        try {
            InternalFactory o = getFactory(key);
            if (o != null) {
                return getFactory(key).create(context);
            } else {
                return null;
            }
        } finally {
            context.setExternalContext(previous);
        }
    }

    <T> T getInstance(Class<T> type, InternalContext context) {
        return getInstance(type, DEFAULT_NAME, context);
    }

    public void inject(final Object o) {
        callInContext(new ContextualCallable<Void>() {
            public Void call(InternalContext context) {
                inject(o, context);
                return null;
            }
        });
    }

    public <T> T inject(final Class<T> implementation) {
        return callInContext(new ContextualCallable<T>() {
            public T call(InternalContext context) {
                return inject(implementation, context);
            }
        });
    }

    public <T> T getInstance(final Class<T> type, final String name) {
        return callInContext(new ContextualCallable<T>() {
            public T call(InternalContext context) {
                return getInstance(type, name, context);
            }
        });
    }

    public <T> T getInstance(final Class<T> type) {
        return callInContext(new ContextualCallable<T>() {
            public T call(InternalContext context) {
                return getInstance(type, context);
            }
        });
    }

    public Set<String> getInstanceNames(final Class<?> type) {
        Set<String> names = factoryNamesByType.get(type);
        if (names == null) {
            names = Collections.emptySet();
        }
        return names;
    }

    ThreadLocal<Object[]> localContext = new ThreadLocal<Object[]>() {
        @Override
        protected Object[] initialValue() {
            return new Object[1];
        }
    };

    
    <T> T callInContext(ContextualCallable<T> callable) {
        Object[] reference = localContext.get();
        if (reference[0] == null) {
            reference[0] = new InternalContext(this);
            try {
                return callable.call((InternalContext) reference[0]);
            } finally {
                
                reference[0] = null;
                
                localContext.remove();
            }
        } else {
            
            return callable.call((InternalContext) reference[0]);
        }
    }

    interface ContextualCallable<T> {
        T call(InternalContext context);
    }

    
    @SuppressWarnings("unchecked")
    <T> ConstructorInjector<T> getConstructor(Class<T> implementation) {
        return constructors.get(implementation);
    }

    final ThreadLocal<Object> localScopeStrategy = new ThreadLocal<>();

    public void setScopeStrategy(Scope.Strategy scopeStrategy) {
        this.localScopeStrategy.set(scopeStrategy);
    }

    public void removeScopeStrategy() {
        this.localScopeStrategy.remove();
    }

    
    interface Injector extends Serializable {
        void inject(InternalContext context, Object o);
    }

    static class MissingDependencyException extends Exception {
        MissingDependencyException(String message) {
            super(message);
        }
    }
}
