package org.kuali.rice.core.api.uif

import static org.junit.Assert.*
import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert;

class RemotableHiddenInputTest {
               	private static final String XML =
        """<hiddenInput xmlns="http://rice.kuali.org/core/v2_0">
          </hiddenInput>""";

    @Test
    void testHappyPath() {
        RemotableHiddenInput o = RemotableHiddenInput.Builder.create().build();
        assertNotNull(o);
    }

    @Test
	void testJAXB() {
		RemotableHiddenInput o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableHiddenInput.class);
	}

    private RemotableHiddenInput.Builder create() {
		RemotableHiddenInput.Builder o = RemotableHiddenInput.Builder.create();
        return o
	}
}
