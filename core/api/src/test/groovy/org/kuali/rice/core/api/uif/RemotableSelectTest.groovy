package org.kuali.rice.core.api.uif

import org.junit.Test
import static org.junit.Assert.*
import org.kuali.rice.core.test.JAXBAssert;

class RemotableSelectTest {
        private static final String XML =
        """<select xmlns="http://rice.kuali.org/core/v2_0">
            <keyLabels>
		        <entry key="foo">bar</entry>
	        </keyLabels>
            <size>2</size>
          </select>""";

    @Test
    void testHappyPath() {
        RemotableSelect o = RemotableSelect.Builder.create(["foo":"bar"]).build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableSelect.Builder o = create();
        o.size = 2

        assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotableSelect.Builder o = create();
        o.size = null

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadSize() {
        RemotableSelect.Builder o = create();
        o.size = 0

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testEmptyKeyLabels() {
        RemotableSelect.Builder o = RemotableSelect.Builder.create([:])
    }

    @Test(expected=IllegalArgumentException.class)
    void testNullKeyLabels() {
        RemotableSelect.Builder o = RemotableSelect.Builder.create(null)
    }

    @Test
	void testJAXB() {
		RemotableSelect o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableSelect.class);
	}

    private RemotableSelect.Builder create() {
		RemotableSelect.Builder o = RemotableSelect.Builder.create(["foo":"bar"]);
        o.size = 2
        return o
	}
}
