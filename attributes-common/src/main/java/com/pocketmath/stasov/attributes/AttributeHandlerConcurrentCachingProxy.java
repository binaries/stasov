package com.pocketmath.stasov.attributes;

import com.pocketmath.stasov.attributes.handler.base.ExactMatchAttributeHandler;

/**
 * Created by etucker on 1/12/16.
 */
public class AttributeHandlerConcurrentCachingProxy extends ExactMatchAttributeHandler {

    private ExactMatchAttributeHandler source;
    private ExactMatchAttributeHandler cache;

    private AttributeHandlerConcurrentCachingProxy(final ExactMatchAttributeHandler source, final ExactMatchAttributeHandler cache) {
        this.source = source;
        this.cache = cache;
    }

    public final static AttributeHandlerConcurrentCachingProxy newCache(final ExactMatchAttributeHandler source, final ExactMatchAttributeHandler cache) {
        return new AttributeHandlerConcurrentCachingProxy(source, cache);
    }

    public final static AttributeHandlerConcurrentCachingProxy newFIFOMemoryCache(final ExactMatchAttributeHandler source, final int capacity) {
        return new AttributeHandlerConcurrentCachingProxy(source, new AttributeHandlerFIFOCacheMap(capacity){});
    }

    public final static AttributeHandlerConcurrentCachingProxy newCaffeineMemoryCache(final ExactMatchAttributeHandler source, final int capacity) {
        return new AttributeHandlerConcurrentCachingProxy(source, new AttributeHandlerCaffeineCacheMap(capacity));
    }

    public final static AttributeHandlerConcurrentCachingProxy newDefaultMemoryCache(final ExactMatchAttributeHandler source, final int capacity) {
        return newCaffeineMemoryCache(source, capacity);
    }

    @Override
    public void put(String name, long id) {
        synchronized(this) {
            source.put(name, id);
            cache.put(name, id);
        }
    }

    @Override
    public long find(String input) {
        long r = cache.find(input);
        if (r != NOT_FOUND) {
            return r;
        } else if (r == CACHED_NOT_FOUND) {
            return NOT_FOUND;
        } else {
            synchronized(this) {
                r = cache.find(input);
                if (r != NOT_FOUND) {
                    return r;
                } else if ( r == CACHED_NOT_FOUND) {
                    return NOT_FOUND;
                } else {
                    r = source.find(input);
                    if (r == NOT_FOUND)
                        cache.put(input, CACHED_NOT_FOUND);
                    else
                        cache.put(input, r);
                    return r;
                }
            }
        }
    }

    @Override
    public Iterable<String> sampleValues(Order order) throws UnsupportedOperationException {
        return source.sampleValues(order);
    }

}
