package org.kuali.rice.core.api.uif

import org.junit.Test
import static org.junit.Assert.*
import org.kuali.rice.core.test.JAXBAssert;

class RemotableMultiSelectTest {
            private static final String XML =
        """<multiSelect xmlns="http://rice.kuali.org/core/v2_0">
            <keyLabels>
		        <entry key="foo">bar</entry>
	        </keyLabels>
            <size>2</size>
          </multiSelect>""";

    @Test
    void testHappyPath() {
        RemotableMultiSelect o = RemotableMultiSelect.Builder.create(["foo":"bar"]).build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableMultiSelect.Builder o = create();
        o.size = 2

        assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotableMultiSelect.Builder o = create();
        o.size = null

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadSize() {
        RemotableMultiSelect.Builder o = create();
        o.size = 0

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testEmptyKeyLabels() {
        RemotableMultiSelect.Builder o = RemotableMultiSelect.Builder.create([:])
    }

    @Test(expected=IllegalArgumentException.class)
    void testNullKeyLabels() {
        RemotableMultiSelect.Builder o = RemotableMultiSelect.Builder.create(null)
    }

    @Test
	void testJAXB() {
		RemotableMultiSelect o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableMultiSelect.class);
	}

    private RemotableMultiSelect.Builder create() {
		RemotableMultiSelect.Builder o = RemotableMultiSelect.Builder.create(["foo":"bar"]);
        o.size = 2
        return o
	}
}
