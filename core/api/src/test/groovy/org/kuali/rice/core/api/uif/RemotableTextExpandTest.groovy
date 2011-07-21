package org.kuali.rice.core.api.uif

import org.kuali.rice.core.test.JAXBAssert
import org.junit.Test
import org.junit.Assert

class RemotableTextExpandTest {

    	private static final String XML =
        """<textExpand xmlns="http://rice.kuali.org/core/v2_0">
          </textExpand>""";

    @Test
    void testHappyPath() {
        RemotableTextExpand o = create();
        Assert.assertNotNull(o);
    }

    @Test
	void testJAXB() {
		RemotableTextExpand o = create();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableTextExpand.class);
	}

    private RemotableTextExpand create() {
		return RemotableTextExpand.Builder.create().build();
	}
}
