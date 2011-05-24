package org.kuali.rice.core.api.mo.common;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.util.KeyValue;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An immutable key value class. This class is has a comparison order
 * that string case-insensitive compares value then key.
 *
 * <p>
 * This class cannot be constructed with a null key but is allowed to have null values.
 * </p>
 */
@XmlRootElement(name = ImmutableKeyValue.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = ImmutableKeyValue.Constants.TYPE_NAME, propOrder = {
        ImmutableKeyValue.Elements.KEY,
        ImmutableKeyValue.Elements.VALUE,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class ImmutableKeyValue implements KeyValue, Comparable<KeyValue>{

    @XmlElement(name = Elements.KEY, required = true)
    private final String key;

    @XmlElement(name = Elements.VALUE, required = true)
    private final String value;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * This constructor should never be called except during JAXB unmarshalling.
     */
    private ImmutableKeyValue() {
        this.key = null;
        this.value = null;
    }

    private ImmutableKeyValue(String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        this.key = key;
        this.value = value;
    }

    /**
     * Creates a ImmutableKeyValue from a {@link Map.Entry}.
     * @param entry cannot be null
     * @return key value
     * @throws IllegalArgumentException if the entry or the entry's key is null
     */
    public static ImmutableKeyValue fromMapEntry(Map.Entry<String, String> entry) {
        if (entry == null) {
            throw new IllegalArgumentException("entry is null");
        }
        return new ImmutableKeyValue(entry.getKey(), entry.getValue());
    }

    /**
     * Creates a ImmutableKeyValue from strings.
     * @param key cannot be null
     * @param value can be null
     * @return key value
     * @throws IllegalArgumentException if the key is null
     */
    public static ImmutableKeyValue fromStrings(String key, String value) {
        //key validation done in private ctor
        return new ImmutableKeyValue(key, value);
    }

    /**
     * Creates a ImmutableKeyValue from a {@link KeyValue}.
     * @param keyValue cannot be null
     * @return key value
     * @throws IllegalArgumentException if the keyValue or the keyValue's key is null
     */
    public static ImmutableKeyValue fromKeyValue(KeyValue keyValue) {
        if (keyValue == null) {
            throw new IllegalArgumentException("keyValue is null");
        }
        return new ImmutableKeyValue(keyValue.getKey(), keyValue.getValue());
    }

    /**
     * Converts key value to a mutable {@link Map} containing a single item.
     * The map returned is disconnected from this KeyValue class.
     *
     * @return a Map
     */
    public Map<String, String> toMap() {
        Map<String, String> m = new HashMap<String, String>();
        m.put(key, value);
        return m;
    }

    /**
     * Converts key value to a mutable {@link Map.Entry}.
     * The entry returned is disconnected from this KeyValue class.
     *
     * @return an entry
     */
    public Map.Entry<String, String> toMapEntry() {
        return new HashMap.SimpleEntry<String, String>(key, value);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
	public int compareTo(KeyValue o) {
		if (o == null) {
			throw new NullPointerException("o is null");
		}

		return new CompareToBuilder()
			.append(this.getValue(), o.getValue(), String.CASE_INSENSITIVE_ORDER)
			.append(this.getKey(), o.getKey(), String.CASE_INSENSITIVE_ORDER)
			.toComparison();
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
        final static String ROOT_ELEMENT_NAME = "keyValue";
        final static String TYPE_NAME = "KeyValueType";
        final static String[] HASH_CODE_EQUALS_EXCLUDE = {CoreConstants.CommonElements.FUTURE_ELEMENTS};
    }

    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String KEY = "key";
        final static String VALUE = "value";
    }
}
