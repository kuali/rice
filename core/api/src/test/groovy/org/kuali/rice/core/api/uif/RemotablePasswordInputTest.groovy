package org.kuali.rice.core.api.uif

import static org.junit.Assert.*
import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert;

class RemotablePasswordInputTest {
               	private static final String XML =
        """<passwordInput xmlns="http://rice.kuali.org/core/v2_0">
            <size>2</size>
          </passwordInput>""";

    @Test
    void testHappyPath() {
        RemotablePasswordInput o = RemotablePasswordInput.Builder.create().build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotablePasswordInput.Builder o = create();
        o.size = 2

        assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotablePasswordInput.Builder o = create();
        o.size = null

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testbadSize() {
        RemotablePasswordInput.Builder o = create();
        o.size = 0

        assertNotNull(o.build());
    }

    @Test
	void testJAXB() {
		RemotablePasswordInput o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotablePasswordInput.class);
	}

    private RemotablePasswordInput.Builder create() {
		RemotablePasswordInput.Builder o = RemotablePasswordInput.Builder.create();
        o.size = 2
        return o
	}
}
