package org.kuali.rice.core.api.uif

import org.junit.Test
import org.kuali.rice.core.test.JAXBAssert
import static org.junit.Assert.assertNotNull

class RemotableQuickFinderTest {
    
            private static final String XML1 =
        """<quickFinder xmlns="http://rice.kuali.org/core/v2_0">
            <baseLookupUrl>http://lookup.url/</baseLookupUrl>
            <dataObjectClass>FooBo</dataObjectClass>
          </quickFinder>""";

        private static final String XML2 =
        """<quickFinder xmlns="http://rice.kuali.org/core/v2_0">
            <baseLookupUrl>http://lookup.url/</baseLookupUrl>
            <dataObjectClass>FooBo</dataObjectClass>
            <lookupParameters>
                <entry key="foo">bar</entry>
            </lookupParameters>
            <fieldConversions>
                <entry key="baz">bing</entry>
            </fieldConversions>
          </quickFinder>""";

    @Test
    void testHappyPath() {
        RemotableQuickFinder o = RemotableQuickFinder.Builder.create("http://lookup.url/", "FooBo").build();
        assertNotNull(o);
    }

    @Test
    void testHappyPath2() {
        RemotableQuickFinder.Builder o = create2();
        assertNotNull(o.build());
    }

    @Test
    void testOptional() {
        RemotableQuickFinder.Builder o = create();
        o.fieldConversions = null
        o.lookupParameters = null
        assertNotNull(o.build());
    }

    @Test
    void testOptional2() {
        RemotableQuickFinder.Builder o = create();
        o.fieldConversions = [:]
        o.lookupParameters = [:]
        assertNotNull(o.build());
    }

    @Test(expected=IllegalArgumentException.class)
    void testNullDO() {
        RemotableQuickFinder o = RemotableQuickFinder.Builder.create("http://lookup.url/", null).build()
    }

    @Test(expected=IllegalArgumentException.class)
    void testBlankDO() {
        RemotableQuickFinder o = RemotableQuickFinder.Builder.create("http://lookup.url/", " ").build()
    }

    @Test
	void testJAXB1() {
		RemotableQuickFinder o = create().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML1, RemotableQuickFinder.class);
	}

    @Test
	void testJAXB2() {
		RemotableQuickFinder o = create2().build();
		JAXBAssert.assertEqualXmlMarshalUnmarshal(o, XML2, RemotableQuickFinder.class);
	}

    private RemotableQuickFinder.Builder create() {
		RemotableQuickFinder.Builder o = RemotableQuickFinder.Builder.create("http://lookup.url/", "FooBo");
        return o
	}

    private RemotableQuickFinder.Builder create2() {
		RemotableQuickFinder.Builder o = RemotableQuickFinder.Builder.create("http://lookup.url/", "FooBo");
        o.fieldConversions = ["baz" : "bing"]
        o.lookupParameters = ["foo" : "bar"]
        return o
	}
}
