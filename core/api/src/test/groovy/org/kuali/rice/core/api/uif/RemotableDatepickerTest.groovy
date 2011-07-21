package org.kuali.rice.core.api.uif

import org.kuali.rice.core.test.JAXBAssert
import org.junit.Test
import org.junit.Assert

class RemotableDatepickerTest {

    private static final String XML =
        """<datepicker xmlns="http://rice.kuali.org/core/v2_0">
          </datepicker>""";

    @Test
    void testHappyPath() {
        RemotableDatepicker o = create();
        Assert.assertNotNull(o);
    }

    @Test
	void testJAXB() {
		RemotableDatepicker o = create();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableDatepicker.class);
	}

    private RemotableDatepicker create() {
		return RemotableDatepicker.Builder.create().build();
	}
}
