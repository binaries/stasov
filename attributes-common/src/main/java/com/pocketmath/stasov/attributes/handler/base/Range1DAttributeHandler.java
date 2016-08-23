package com.pocketmath.stasov.attributes.handler.base;

import java.io.Serializable;

/**
 * Created by etucker on 3/4/16.
 */
public abstract class Range1DAttributeHandler<IdType extends Serializable & Comparable<IdType>, ValueType extends Serializable & Comparable<ValueType>>
        extends AttributeHandler<IdType,Object,ValueType> {

}
