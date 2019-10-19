package com.github.inikolaev.minispring;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Context {
    // BeanDefinition
    private final Map<String, Object> beans = new HashMap<>();

    public <T> void registerBean(String name, T bean) {
        beans.put(name, bean);
    }

    public void registerBean(String name, Class clazz) {
        try {
            Object bean = clazz.newInstance();

            inject(clazz, bean);

            beans.put(name, bean);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register bean: " + name, e);
        }
    }

    public <T> T get(String name) {
        return (T) beans.get(name);
    }

    protected void inject(Class clazz, Object bean) {
        for (Field field: clazz.getDeclaredFields()) {
            if (canInject(field)) {
                final String name = field.getAnnotation(Named.class).value();

                if (beans.containsKey(name)) {
                    String setterName = getSetterName(field.getName());
                    Object value = get(name);

                    try {
                        Method setter = clazz.getDeclaredMethod(setterName, field.getType());
                        setter.invoke(bean, value);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Couldn't find setter: " + setterName);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("Couldn't inject value for a property: " + name, e);
                    }
                } else {
                    throw new RuntimeException("Couldn't resolve dependency: " + name);
                }
            }
        }
    }

    protected boolean canInject(Field field) {
        final Named named = field.getAnnotation(Named.class);

        return field.getAnnotation(Inject.class) != null
               && named != null
               && named.value().trim().length() > 0;
    }

    protected String getSetterName(String name) {
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
