package org.kuali.rice.core.api.mo.common

import java.util.AbstractMap.SimpleImmutableEntry
import org.junit.Test
import org.kuali.rice.core.util.ConcreteKeyValue
import org.kuali.rice.core.util.KeyValue
import static org.junit.Assert.*

class AttributesTest {

    @Test
    void testEmptyOptimization() {
        assertTrue Attributes.empty().is(Attributes.empty())
    }

    @Test
    void testFromMapOptimization() {
        assertTrue Attributes.fromMap([:]).is(Attributes.empty())
    }

    @Test(expected=IllegalArgumentException.class)
    void testFromMapNull() {
        Attributes.fromMap(null)
    }

    @Test
    void testFromMapNonNull() {
        def m = ["foo":"bar"]
        assertEquals Attributes.fromMap(m).toMap(), m
    }

    @Test(expected=IllegalArgumentException.class)
    void testFromMapEntryNull() {
        Attributes.fromMapEntry(null)
    }

    @Test(expected=IllegalArgumentException.class)
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

    @Test(expected=IllegalArgumentException.class)
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

    @Test(expected=IllegalArgumentException.class)
    void testFromKeyValueNull() {
        Attributes.fromKeyValue(null)
    }

    @Test(expected=IllegalArgumentException.class)
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
        def m = ["foo":"bar"]
        def attrs = Attributes.fromMap(m)

        //is it mutable?
        def view = attrs.toMap();
        view["baz"] = "bing"
        assertEquals view["baz"], "bing"

        //is it disconnected from the Attributes instance?
        assertFalse attrs.containsKey("baz")
    }

    @Test(expected=UnsupportedOperationException.class)
    void testEntrySetImmutable1(){
        def m = ["foo":"bar"]
        def attrs = Attributes.fromMap(m)
        attrs.entrySet().add(new java.util.AbstractMap.SimpleEntry("junk","poo"))
    }

    @Test(expected=UnsupportedOperationException.class)
    void testEntrySetImmutable2(){
        def m = ["foo":"bar"]
        def attrs = Attributes.fromMap(m)
        attrs.entrySet().iterator().next().setValue("baz")
    }
}
