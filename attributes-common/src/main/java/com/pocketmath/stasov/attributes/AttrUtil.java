package com.pocketmath.stasov.attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.reflections.Reflections;

import javax.management.Attribute;
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
            classes.addAll(reflections.getSubTypesOf(AttributeHandler.class));
            classes.addAll(reflections.getSubTypesOf(AttributeHandlerMapAutoIdBase.class));
            classes.addAll(reflections.getSubTypesOf(AttributeHandlerMapBase.class));
        }
        return classes;
    }

    static SortedMap<String,Class> findAttrNames(final Set attrClasses) throws DuplicateAttributeException {
        final SortedMap<String,Class> map = new TreeMap<String, Class>();
        for (Object clObj : attrClasses) {
            Class cl = (Class) clObj;
            final String name;
            final AttributeType annotation = (AttributeType) cl.getAnnotation(AttributeType.class);
            if (annotation != null && annotation.name() != "") name = annotation.name();
            else name = cl.getSimpleName();
            if (map.containsKey(name)) throw new DuplicateAttributeException("name: " + name);
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
