package org.kuali.rice.krad.data.provider;

import org.apache.commons.lang.RandomStringUtils;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.test.document.bo.AccountExtension;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.krad.test.document.bo.SimpleAccount;

import static org.junit.Assert.assertEquals;

/**
 * PersistenceProviderTestBase using SimpleAccount as test object
 */
public abstract class PersistenceProviderAccountTestBase extends PersistenceProviderTestBase<SimpleAccount> {

    @Override
    protected SimpleAccount createTopLevelObject() {
        SimpleAccount a = new SimpleAccount();
        //Long number = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("trvl_id_seq");
        //Long amId = 1l; //RandomUtils.nextLong();
        String name = RandomStringUtils.randomAlphanumeric(10);
        //a.setNumber(number.toString());
        //a.setAmId(amId);
        a.setName(name);
        return a;
    }

    @Override
    protected void addLinkedReferences(SimpleAccount a) {
        addUnlinkedReferences(a);
        //a.getAccountManager().setAmId(Long.parseLong(a.getNumber()));
        AccountExtension e = (AccountExtension) a.getExtension();
        e.setAccountTypeCode(e.getAccountType().getAccountTypeCode());
        e.setNumber(a.getNumber());
    }

    @Override
    protected void addUnlinkedReferences(SimpleAccount a) {
        //AccountManager am = new AccountManager();
        //am.setUserName(RandomStringUtils.randomAlphanumeric(10));
        //a.setAccountManager(am);
        AccountExtension extension = new AccountExtension();
        //extension.setNumber(KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("trvl_id_seq").toString());
        AccountType at = new AccountType();
        at.setName(RandomStringUtils.randomAlphanumeric(10));
        at.setAccountTypeCode(RandomStringUtils.randomAlphanumeric(2));
        extension.setAccountType(at);
        a.setExtension(extension);
    }

    @Override
    protected String[] getPropertiesForQuery() {
        return new String[] { "number", "name" };
    }

    @Override
    protected Object getIdForLookup(SimpleAccount a) {
        return a.getNumber();
    }

    @Override
    protected Object getNextTestObjectId() {
        return KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber("trvl_id_seq").toString();
    }

    @Override
    protected void setTestObjectPK(SimpleAccount a, Object key) {
        a.setNumber((String) key);
    }

    @Override
    protected void assertTestObjectIdentityEquals(SimpleAccount expected, SimpleAccount actual) {
        assertTestObjectEquals(expected, actual);
        assertEquals(expected.getNumber(), actual.getNumber());
    }

    @Override
    protected void assertTestObjectEquals(SimpleAccount expected, SimpleAccount actual) {
        assertEquals(expected.getAmId(), actual.getAmId());
        assertEquals(expected.getName(), actual.getName());
        if (expected.getExtension() != null) {
            AccountExtension e1 = (AccountExtension) expected.getExtension();
            AccountExtension e2 = (AccountExtension) actual.getExtension();
            assertEquals(e1.getNumber(), e2.getNumber());
            assertEquals(e1.getAccountTypeCode(), e2.getAccountTypeCode());

            if (e1.getAccountType() != null) {
                AccountType at1 = e1.getAccountType();
                AccountType at2 = e2.getAccountType();
                assertEquals(at1.getName(), at2.getName());
                assertEquals(at1.getAccountTypeCode(), at2.getAccountTypeCode());
            }
        }

    }
}
