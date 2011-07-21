package org.kuali.rice.core.api.uif

import org.junit.Test
import static org.junit.Assert.*
import org.kuali.rice.core.test.JAXBAssert

class RemotableTextInputTest {
           	private static final String XML =
        """<textInput xmlns="http://rice.kuali.org/core/v2_0">
            <size>2</size>
            <watermark>foo</watermark>
          </textInput>""";

    @Test
    void testHappyPath() {
        RemotableTextInput o = RemotableTextInput.Builder.create().build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableTextInput.Builder o = create();
        o.size = 2
        o.watermark = "foo"

        assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotableTextInput.Builder o = create();
        o.size = null
        o.watermark = null

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadSize() {
        RemotableTextInput.Builder o = create();
        o.size = 0

        assertNotNull(o.build());
    }

    @Test
	void testJAXB() {
		RemotableTextInput o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableTextInput.class);
	}

    private RemotableTextInput.Builder create() {
		RemotableTextInput.Builder o = RemotableTextInput.Builder.create();
        o.size = 2
        o.watermark = "foo"
        return o
	}
}
