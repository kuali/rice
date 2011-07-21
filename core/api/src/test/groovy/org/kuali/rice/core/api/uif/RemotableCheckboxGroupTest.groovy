package org.kuali.rice.core.api.uif

import org.junit.Test
import static org.junit.Assert.*
import org.kuali.rice.core.test.JAXBAssert;

class RemotableCheckboxGroupTest {
                private static final String XML =
        """<checkboxGroup xmlns="http://rice.kuali.org/core/v2_0">
            <keyLabels>
		        <entry key="foo">bar</entry>
	        </keyLabels>
          </checkboxGroup>""";

    @Test
    void testHappyPath() {
        RemotableCheckboxGroup o = RemotableCheckboxGroup.Builder.create(["foo":"bar"]).build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableCheckboxGroup.Builder o = create();

        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testEmptyKeyLabels() {
        RemotableCheckboxGroup.Builder o = RemotableCheckboxGroup.Builder.create([:])
    }

    @Test(expected=IllegalArgumentException.class)
    void testNullKeyLabels() {
        RemotableCheckboxGroup.Builder o = RemotableCheckboxGroup.Builder.create(null)
    }

    @Test
	void testJAXB() {
		RemotableCheckboxGroup o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML, RemotableCheckboxGroup.class);
	}

    private RemotableCheckboxGroup.Builder create() {
		RemotableCheckboxGroup.Builder o = RemotableCheckboxGroup.Builder.create(["foo":"bar"]);
        return o
	}
}
