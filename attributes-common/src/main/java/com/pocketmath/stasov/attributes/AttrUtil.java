package com.pocketmath.stasov.attributes;

import com.google.common.collect.ImmutableSortedSet;
import org.reflections.Configuration;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by etucker on 3/29/15.
 */
class AttrUtil {

/*    private static Set<Class> allSubClasses(final Set<Class> classes) {
        final Reflections reflections = new Reflections();

        Set<Class> subset = new HashSet<Class>();
        for (final Object clazz : classes.toArray()) {
            subset.addAll(reflections.getSubTypesOf((Class) clazz));
        }
        subset.addAll(allSubClasses(subset));

        return subset;
    }
*/

    static Set<Class> findAttrClasses(final AttrConfig attrCfg) {
        Set<Class> classes = new HashSet<Class>();
        Iterable<String> attrPkgs = attrCfg.getAttributesPackages();
        for (String attrPkg : attrPkgs) {
            Reflections reflections = new Reflections(attrPkg);
            //classes.addAll(reflections.getSubTypesOf(AttributeHandler.class));
            //classes.addAll(reflections.getSubTypesOf(AttributeHandlerMapAutoId.class));
            //classes.addAll(reflections.getSubTypesOf(AttributeHandlerMap.class));
            classes.addAll(reflections.getTypesAnnotatedWith(AttributeType.class));
        }
        // check that classes are subtypes of AttributeHandler
        Reflections reflections = new Reflections();
        for (Class clazz : classes) {
            try {
                final Object o = clazz.newInstance();
                if (!(o instanceof AttributeHandler)) {
                    throw new IllegalStateException("class " + clazz.getName() + " was not a subtype of " + AttributeHandler.class.getName());
                }
            } catch (InstantiationException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        return classes;
    }

    static SortedMap<String,Class> findAttrNames(final Set attrClasses) throws DuplicateAttributeException {
        final SortedMap<String,Class> map = new TreeMap<String, Class>();
        for (Object clObj : attrClasses) {
            Class cl = (Class) clObj;
            final String name;
            final AttributeType annotation = (AttributeType) cl.getAnnotation(AttributeType.class);
            if (annotation != null && !annotation.name().isEmpty()) name = annotation.name();
            else name = cl.getSimpleName();
            if (map.containsKey(name)) throw new DuplicateAttributeException("name: " + name + " for class: " + cl);
            map.put(name, cl);
        }
        return map;
    }

    static Set<Class> findAttrClasses() throws DuplicateAttributeException {
        Reflections reflections = new Reflections();
        Class<? extends Annotation> attributeTypeAnnotation = AttributeType.class;
        final Map<String, Class> map = findAttrNames(reflections.getTypesAnnotatedWith(attributeTypeAnnotation));
        return ImmutableSortedSet.<Class>copyOf(map.values());
    }

}
