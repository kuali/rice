package org.kuali.rice.kim.lookup

import org.junit.Test
import static org.junit.Assert.*;

class KimDocumentRoleMemberLookupableHelperServiceImplTest {

    @Test
    public void testStripEnd() {
    	assertNull(KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd(null, null));
    	assertEquals("", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("", null));
    	assertEquals("", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("", ""));
    	assertEquals("b", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("b", ""));
    	assertEquals("", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("b", "b"));
    	assertEquals("b", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("b", "bb"));
    	assertEquals("wx", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("wxyz", "yz"));
    	assertEquals("wx", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("wxyz     ", "yz     "));
    	assertEquals("wxyz", KimDocumentRoleMemberLookupableHelperServiceImpl.stripEnd("wxyz", "abc"));
    }
}
