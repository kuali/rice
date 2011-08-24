package org.kuali.rice.core.api.cache;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlRootElement(name = CacheTarget.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = CacheTarget.Constants.TYPE_NAME, propOrder = {
        CacheTarget.Elements.CACHE,
        CacheTarget.Elements.KEY,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
/**
 * A class that represents a target of a cache operation.  If the cache key
 * is not specified then the entire cache is the target.
 */
public final class CacheTarget extends AbstractDataTransferObject {

    @XmlElement(name = Elements.CACHE, required = true)
    private final String cache;
    @XmlElement(name = Elements.KEY, required = false)
    private final String key;
    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private CacheTarget() {
        this.cache = null;
        this.key = null;
    }

    private CacheTarget(String cache, String key) {
        if (StringUtils.isBlank(cache)) {
            throw new IllegalArgumentException("cache is blank or null");
        }

        this.cache = cache;
        this.key = key;
    }

    /**
     * Creates an instance targeting a entire cache.
     * @param cache the name of the cache.  cannot be a null of blank string.
     * @return an instance
     * @throws IllegalArgumentException if the cache is null or blank
     */
    public static CacheTarget entireCache(String cache) {
        return new CacheTarget(cache, null);
    }

    /**
     * Creates an instance targeting a single item in a cache.
     * @param cache The name of the cache.  cannot be a null of blank string.
     * @param key The key of the item in the cache.  cannot be a null of blank string.
     * @return an instance
     * @throws IllegalArgumentException if the cache or key is null or blank
     */
    public static CacheTarget singleEntry(String cache, String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("a blank or null key does not target a single entry");
        }

        return new CacheTarget(cache, key);
    }

    /**
     * Checks if an instance contains a key.
     * @return true if a key exists
     */
    public boolean containsKey() {
        return key != null;
    }

    /**
     * The name of the cache to target.  will not return a null of blank string.
     * @return the name.
     */
    public String getCache() {
        return cache;
    }

    /**
     * The key of an item in the cache.  optional. can return null or blank string.
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "cacheTarget";
        final static String TYPE_NAME = "CacheTargetType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String CACHE = "cache";
        final static String KEY = "key";
    }
}
