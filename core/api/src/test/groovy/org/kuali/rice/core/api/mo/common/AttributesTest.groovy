package org.kuali.rice.core.api.mo.common

import java.util.AbstractMap.SimpleImmutableEntry
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlType
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.junit.Test
import org.kuali.rice.core.api.CoreConstants
import org.kuali.rice.core.test.JAXBAssert
import org.kuali.rice.core.util.ConcreteKeyValue
import org.kuali.rice.core.util.KeyValue
import static org.junit.Assert.*

class AttributesTest {

    private static final String XML = """
	<root xmlns="http://rice.kuali.org/core/v2_0">
		<attributes>
		    <entry key="foo">bar</entry>
	    </attributes>
	</root>
    """

    @Test
    void testEmptyOptimization() {
        assertTrue Attributes.empty().is(Attributes.empty())
    }

    @Test
    void testFromMapOptimization() {
        assertTrue Attributes.fromMap([:]).is(Attributes.empty())
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromMapNull() {
        Attributes.fromMap(null)
    }

    @Test
    void testFromMapNonNull() {
        def m = ["foo": "bar"]
        assertEquals Attributes.fromMap(m).toMap(), m
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromMapEntryNull() {
        Attributes.fromMapEntry(null)
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromMapEntryNullKey() {
        Attributes.fromMapEntry(new SimpleImmutableEntry(null, "bar"))
    }

    @Test
    void testFromMapEntry() {
        def e = new SimpleImmutableEntry("foo", "bar");
        def set = Attributes.fromMapEntry(e).entrySet();
        assertEquals set.size(), 1
        assertEquals set.iterator().next(), e
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromStringsNullKey() {
        Attributes.fromStrings(null, "bar")
    }

    @Test
    void testFromStrings() {
        def e = new SimpleImmutableEntry("foo", "bar");
        def set = Attributes.fromStrings(e.getKey(), e.getValue()).entrySet()
        assertEquals set.size(), 1
        assertEquals set.iterator().next(), e
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromKeyValueNull() {
        Attributes.fromKeyValue(null)
    }

    @Test(expected = IllegalArgumentException.class)
    void testFromKeyValueNullKey() {
        Attributes.fromKeyValue([getKey: { null }] as KeyValue)
    }

    @Test
    void testFromKeyValue() {
        def kv = new ConcreteKeyValue("foo", "bar");
        def set = Attributes.fromKeyValue(kv).entrySet()
        assertEquals set.size(), 1

        def e = set.iterator().next();

        assertEquals kv.getKey(), e.getKey()
        assertEquals kv.getValue(), e.getValue()
    }

    @Test
    void testToMapMutableView() {
        def m = ["foo": "bar"]
        def attrs = Attributes.fromMap(m)

        //is it mutable?
        def view = attrs.toMap();
        view["baz"] = "bing"
        assertEquals view["baz"], "bing"

        //is it disconnected from the Attributes instance?
        assertFalse attrs.containsKey("baz")
    }

    @Test(expected = UnsupportedOperationException.class)
    void testEntrySetImmutable1() {
        def m = ["foo": "bar"]
        def attrs = Attributes.fromMap(m)
        attrs.entrySet().add(new java.util.AbstractMap.SimpleEntry("junk", "poo"))
    }

    @Test(expected = UnsupportedOperationException.class)
    void testEntrySetImmutable2() {
        def m = ["foo": "bar"]
        def attrs = Attributes.fromMap(m)
        attrs.entrySet().iterator().next().setValue("baz")
    }

    @Test
    void test_Xml_Marshal_Unmarshal() {
        JAXBAssert.assertEqualXmlMarshalUnmarshal(this.create(), XML, Root.class)
    }

    private static create() {
        return new Root();
    }

    @XmlRootElement(name = "root", namespace = CoreConstants.Namespaces.CORE_NAMESPACE_2_0)
    @XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.NONE)
    @XmlType(name = "RootType", propOrder = [
    "attributes"
    ])
    static class Root {
        @XmlElement(name = "attributes", required = true)
        private final Attributes attributes = Attributes.fromMap(["foo": "bar"])

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(obj, this);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
