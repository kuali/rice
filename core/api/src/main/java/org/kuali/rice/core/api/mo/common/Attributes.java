package org.kuali.rice.core.api.mo.common;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.jaxb.StringMapEntry;
import org.kuali.rice.core.util.jaxb.StringMapEntryList;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a generic attributes class in rice.  It is essentially a list of key-value
 * pairs where the key & value are strings & the keys are unique.
 */
@XmlJavaTypeAdapter(Attributes.Adapter.class)
public final class Attributes implements Serializable {

    private static final long serialVersionUID = -2804341886674598357L;

	private static final Attributes EMPTY = new Attributes(Collections.<String, String>emptyMap());

    private final Map<String, String> keyValues;

    private final Object lock = new Object();
    private Set<Map.Entry<String, String>> cache;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private Attributes() {
        this.keyValues = null;
    }

    private Attributes(final Map<String, String> map) {
        this.keyValues = new HashMap<String, String>(map);
    }

    /**
     * Creates empty attributes.
     *
     * @return Attributes
     */
    public static Attributes empty() {
        return EMPTY;
    }

    /**
     * Creates attributes from a {@link Map}.  Map cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if map is null
     */
    public static Attributes fromMap(Map<String, String> map) {
        if (map == null) {
            throw new IllegalArgumentException("map is null");
        }

        if (map.isEmpty()) {
            return empty();
        }

        return new Attributes(map);
    }

    /**
     * Creates attributes from a {@link Map.Entry}.  Map.Entry cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if entry is null or entry.key is null
     */
    public static Attributes fromMapEntry(Map.Entry<String, String> entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry is null");
        }

        if (entry.getKey() == null) {
            throw new IllegalArgumentException("entry.key is null");
        }

        return fromMap(Collections.singletonMap(entry.getKey(), entry.getValue()));
    }

    /**
     * Creates attributes from strings.  Key cannot be null. Value can be null
     *
     * @return Attributes
     * @throws IllegalArgumentException if key is null
     */
    public static Attributes fromStrings(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        return fromMap(Collections.singletonMap(key, value));
    }

    /**
     * Creates attributes from a {@link KeyValue}.  KeyValue cannot be null.
     *
     * @return Attributes
     * @throws IllegalArgumentException if keyValue is null
     */
    public static Attributes fromKeyValue(KeyValue keyValue) {
        if (keyValue == null) {
            throw new IllegalArgumentException("keyValue is null");
        }

        if (keyValue.getKey() == null) {
            throw new IllegalArgumentException("keyValue.key is null");
        }

        return fromMap(Collections.singletonMap(keyValue.getKey(), keyValue.getValue()));
    }

    /**
     * Converts key value to a mutable {@link Map}.
     * The map returned is disconnected from this Attributes class and is mutable.
     *
     * @return a Map
     */
    public Map<String, String> toMap() {
        return new HashMap<String, String>(keyValues);
    }

    //map-like methods

    /**
     * Returns the amount of attributes in this data structure.
     *
     * @return the size
     */
    public int size() {
        return keyValues.size();
    }

    /**
     * Whether this data structure does not contain any attributes.
     *
     * @return true if empty false if not
     */
    public boolean isEmpty() {
        return keyValues.isEmpty();
    }

    /**
     * Whether this data structure contains an attribute with a given key. Key may not be null.
     *
     * @return true if the key exists false if not
     * @throws IllegalArgumentException if the key is null
     */
    public boolean containsKey(String key) {
        validateKey(key);
        return keyValues.containsKey(key);
    }

    /**
     * Whether this data structure contains an attribute with a given value. Value may be null.
     *
     * @return true if the value exists false if not
     */
    public boolean containsValue(String value) {
        return keyValues.containsValue(value);
    }

    /**
     * Gets an attribute value from a key. Key may not be null.
     *
     * @return true if the value exists false if not
     */
    public String get(String key) {
        validateKey(key);
        return keyValues.get(key);
    }

    /**
     * Gets a mutable {@link Set} of all attribute keys.
     *
     * @return the set.
     */
    public Set<String> keySet() {
        return keyValues.keySet();
    }

    /**
     * Gets a mutable {@link Collection} of all attribute values.  Could contain null values.
     *
     * @return the collection.
     */
    public Collection<String> values() {
        return keyValues.values();
    }

    /**
     * Gets a immutable {@link Set} of immutable {@link Map.Entry} instances.
     *
     * @return the set.
     */
    public Set<Map.Entry<String, String>> entrySet() {
        //not sure if we really need caching here - but adding it
        //b/c it is easy enough to implement
        synchronized (lock) {
            if (cache == null) {
                final Set<Map.Entry<String, String>> temp = new HashSet<Map.Entry<String, String>>();
                for (Map.Entry<String, String> e : keyValues.entrySet()) {
                    if (e != null) {
                        temp.add(new AbstractMap.SimpleImmutableEntry<String, String>(e.getKey(), e.getValue()));
                    }
                }
                cache = Collections.unmodifiableSet(temp);
            }
        }
        return cache;
    }

    private static void validateKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this, Constants.HASH_CODE_EQUALS_EXCLUDE);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {"cache", "lock"};
    }
    
    public static class Adapter extends XmlAdapter<StringMapEntryList, Attributes> {

    	@Override
    	public StringMapEntryList marshal(Attributes attributes) throws Exception {
    		if (attributes == null || attributes.keyValues == null) {
    			return null;
    		}
    		List<StringMapEntry> entries = new ArrayList<StringMapEntry>(attributes.keyValues.size());
    		for (Map.Entry<String, String> entry : attributes.keyValues.entrySet()) {
    			entries.add(new StringMapEntry(entry));
    		}
    		return new StringMapEntryList(entries);
    	}

    	@Override
    	public Attributes unmarshal(StringMapEntryList entries) throws Exception {
    		if (entries == null || entries.getEntries() == null) {
    			return null;
    		}
    		Map<String, String> resultMap = new HashMap<String, String>();
    		for (StringMapEntry entry : entries.getEntries()) {
    			resultMap.put(entry.getKey(), entry.getValue());
    		}
    		return new Attributes(resultMap);
    	}

    }

}
