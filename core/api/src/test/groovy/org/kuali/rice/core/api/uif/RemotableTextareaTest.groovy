package org.kuali.rice.core.api.uif

import org.junit.Test
import org.junit.Assert
import org.kuali.rice.core.test.JAXBAssert

class RemotableTextareaTest {

       	private static final String XML =
        """<textarea xmlns="http://rice.kuali.org/core/v2_0">
            <rows>2</rows>
            <cols>2</cols>
            <watermark>foo</watermark>
          </textarea>""";

    @Test
    void testHappyPath() {
        RemotableTextarea o = RemotableTextarea.Builder.create().build();
        Assert.assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableTextarea.Builder o = create();
        o.cols = 2
        o.rows = 2
        o.watermark = "foo"

        Assert.assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotableTextarea.Builder o = create();
        o.cols = null
        o.rows = null
        o.watermark = null

        Assert.assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadCols() {
        RemotableTextarea.Builder o = create();
        o.cols = 0

        Assert.assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadRows() {
        RemotableTextarea.Builder o = create();
        o.rows = 0

        Assert.assertNotNull(o.build());
    }

    @Test
	void testJAXB() {
		RemotableTextarea o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableTextarea.class);
	}

    private RemotableTextarea.Builder create() {
		RemotableTextarea.Builder o = RemotableTextarea.Builder.create();
        o.cols = 2
        o.rows = 2
        o.watermark = "foo"
        return o
	}
}
