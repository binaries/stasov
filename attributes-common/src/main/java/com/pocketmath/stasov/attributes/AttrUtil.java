package com.pocketmath.stasov.attributes;

import org.reflections.Reflections;

import java.util.*;

/**
 * Created by etucker on 3/29/15.
 */
class AttrUtil {

    static Set<Class> findAttrClasses(final AttrConfig attrCfg) {
        Set<Class> classes = new HashSet<Class>();
        Iterable<String> attrPkgs = attrCfg.getAttributesPackages();
        for (String attrPkg : attrPkgs) {
            Reflections reflections = new Reflections(attrPkg);
            classes.addAll(reflections.getSubTypesOf(AttributeHandler.class));
        }
        return classes;
    }

    static SortedMap<String,Class> findAttrNames(final Set<Class> attrClasses) throws DuplicateAttributeException {
        final SortedMap<String,Class> map = new TreeMap<String, Class>();
        for (Class cl : attrClasses) {
            final String name;
            final AttributeType annotation = (AttributeType) cl.getAnnotation(AttributeType.class);
            if (annotation != null && annotation.name() != "") name = annotation.name();
            else name = cl.getSimpleName();
            if (map.containsKey(name)) throw new DuplicateAttributeException("name: " + name);
            map.put(name, cl);
        }
        return map;
    }

}
