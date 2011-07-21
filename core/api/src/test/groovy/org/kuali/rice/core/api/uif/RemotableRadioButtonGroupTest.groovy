package org.kuali.rice.core.api.uif

import org.junit.Test
import static org.junit.Assert.*
import org.kuali.rice.core.test.JAXBAssert;

class RemotableRadioButtonGroupTest {
            private static final String XML =
        """<radioButtonGroup xmlns="http://rice.kuali.org/core/v2_0">
            <keyLabels>
		        <entry key="foo">bar</entry>
	        </keyLabels>
          </radioButtonGroup>""";

    @Test
    void testHappyPath() {
        RemotableRadioButtonGroup o = RemotableRadioButtonGroup.Builder.create(["foo":"bar"]).build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableRadioButtonGroup.Builder o = create();

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testEmptyKeyLabels() {
        RemotableRadioButtonGroup.Builder o = RemotableRadioButtonGroup.Builder.create([:])
    }

    @Test(expected=IllegalArgumentException.class)
    void testNullKeyLabels() {
        RemotableRadioButtonGroup.Builder o = RemotableRadioButtonGroup.Builder.create(null)
    }

    @Test
	void testJAXB() {
		RemotableRadioButtonGroup o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableRadioButtonGroup.class);
	}

    private RemotableRadioButtonGroup.Builder create() {
		RemotableRadioButtonGroup.Builder o = RemotableRadioButtonGroup.Builder.create(["foo":"bar"]);
        return o
	}
}
