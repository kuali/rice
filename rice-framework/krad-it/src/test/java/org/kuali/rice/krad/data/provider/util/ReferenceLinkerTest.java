/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.provider.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.krad.test.document.bo.AccountExtension;
import org.kuali.rice.krad.test.document.bo.AccountManager;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.krad.test.document.bo.ChildOfParentObjectWithGeneratedKey;
import org.kuali.rice.krad.test.document.bo.ParentObjectWithGeneratedKey;
import org.kuali.rice.krad.test.document.bo.ParentObjectWithUpdatableChild;
import org.kuali.rice.krad.test.document.bo.UpdatableChildObject;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * TODO jonathan don't forget to fill this in. -- OK.  I won't.  Oops.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@PerSuiteUnitTestData( {
    @UnitTestData(
            sqlStatements = {
                    // Need to remove the not null constraint on the ACCT_TYPE column
                    // TODO: Remove these when master data source corrected
                     @UnitTestSql("DROP TABLE trv_acct_ext")
                    ,@UnitTestSql("CREATE TABLE trv_acct_ext ( ACCT_NUM VARCHAR(10), ACCT_TYPE VARCHAR(100), OBJ_ID VARCHAR(36), VER_NBR DECIMAL(8) )")
                    // For some reason, this table seems to be empty - populate with a couple needed values
                    ,@UnitTestSql("delete from trv_acct_type")
                    ,@UnitTestSql("INSERT INTO trv_acct_type ( ACCT_TYPE, ACCT_TYPE_NAME, OBJ_ID, VER_NBR ) VALUES ( 'EAT', 'EXPENSE', 'EX', 1 )")
                    ,@UnitTestSql("INSERT INTO trv_acct_type ( ACCT_TYPE, ACCT_TYPE_NAME, OBJ_ID, VER_NBR ) VALUES ( 'IAT', 'INCOME', 'IN', 1 )")
            })
})
@PerTestUnitTestData(
        value = @UnitTestData(
                sqlStatements = {
                         @UnitTestSql("delete from trv_acct_ext")
                        ,@UnitTestSql("delete from trv_acct_fo")
                        ,@UnitTestSql("delete from trv_acct")
                        ,@UnitTestSql("INSERT INTO trv_acct_ext(ACCT_NUM, ACCT_TYPE, OBJ_ID, VER_NBR) VALUES('NULL_TYPE', NULL, 'NULL_TYPE', 1)")
                        ,@UnitTestSql("INSERT INTO trv_acct_ext(ACCT_NUM, ACCT_TYPE, OBJ_ID, VER_NBR) VALUES('EX_TYPE', 'EAT', 'EX_TYPE', 1)")
                        ,@UnitTestSql("INSERT INTO TRV_ACCT_FO(ACCT_FO_ID, ACCT_FO_USER_NAME, OBJ_ID, VER_NBR) VALUES(1, 'One', '1', 1)")
                }
        )
)
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class ReferenceLinkerTest extends KRADTestCase {

    private static final String EXPENSE_ACCOUNT_TYPE_CODE = "EAT";
    private static final String INCOME_ACCOUNT_TYPE_CODE = "IAT";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferenceLinkerTest.class);

    protected PersistenceProvider getPersistenceProvider() {
        return getKRADTestHarnessContext().getBean("kradTestJpaPersistenceProvider", PersistenceProvider.class);
    }

    protected EntityManager getEntityManager() {
        return getKRADTestHarnessContext().getBean("kradTestSharedEntityManager", EntityManager.class);
    }

    protected DataObjectService getDOS() {
        return KradDataServiceLocator.getDataObjectService();
    }

    @BeforeClass
    public static void beforeTests() {
        Logger.getLogger(ReferenceLinker.class).setLevel(Level.DEBUG);
        Logger.getLogger(ReferenceLinkerTest.class).setLevel(Level.DEBUG);
    }

    @AfterClass
    public static void afterTests() {
        Logger.getLogger(ReferenceLinker.class).setLevel(null);
        Logger.getLogger(ReferenceLinkerTest.class).setLevel(null);
    }

    @Before
    public void evictAll() {
        getKRADTestHarnessContext().getBean("kradTestEntityManagerFactory", EntityManagerFactory.class).getCache().evictAll();
    }

    @Test
    public void preCheck_AccountExtension_Metadata() {
        DataObjectMetadata metadata = KradDataServiceLocator.getMetadataRepository().getMetadata(AccountExtension.class);
        assertNotNull("AccountExtension metadata missing", metadata);
        DataObjectRelationship relationship = metadata.getRelationship("accountType");
        assertNotNull("AccountExtension.accountType relationship metadata missing", relationship);
    }

    @Test
    public void newParentObject_setChildReferenceObject() {
        // Create a new object and add an existing account type by object
        AccountExtension acct = new AccountExtension();
        acct.setNumber("NEW_ACCT");

        AccountType acctType = getDOS().find(AccountType.class, EXPENSE_ACCOUNT_TYPE_CODE);
        assertNotNull( "Error retrieving EX account type from data object service", acctType );

        acct.setAccountType(acctType);
        assertNull( "Before saving account type code should have been null", acct.getAccountTypeCode());

        // Save the object and test the result
        enableJotmLogging();
        acct = getDOS().save(acct);
        assertEquals( "After saving, the acct type code should have been set to the PK from the acct type", acctType.getAccountTypeCode(), acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());

        // Reload from database
        acct = getDOS().find(AccountExtension.class, "NEW_ACCT");
        assertNotNull("unable to retrieve new object from database after save", acct);
        assertEquals( "After reloading, the acct type code should have been set to the PK from the acct type", acctType.getAccountTypeCode(), acct.getAccountTypeCode());
        assertNotNull( "After reloading, the acct type object should be available", acct.getAccountType());
        disableJotmLogging();
    }

    @Test
    public void newParentObject_setChildReferenceKeyValue() {
        // Create a new object and add an existing account type by object
        AccountExtension acct = new AccountExtension();
        acct.setNumber("NEW_ACCT");

        acct.setAccountTypeCode(EXPENSE_ACCOUNT_TYPE_CODE);
        assertNull( "Before saving account type object should have been null", acct.getAccountType());

        // Save the object and test the result
        enableJotmLogging();
        acct = getDOS().save(acct,PersistenceOption.FLUSH,PersistenceOption.REFRESH);
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After saving, the acct type code on the object should have been the one from the parent object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());

        disableJotmLogging();
    }

    @Test
    public void existingParent_changeReferenceChildKeyValue() {
        AccountExtension acct = getExAccount();

        acct.setAccountTypeCode(INCOME_ACCOUNT_TYPE_CODE);

        // test what object getter does
        LOG.debug( "Account Type After setting FK to 'IAT': " + acct.getAccountType());
        enableJotmLogging();
        acct = getDOS().save(acct,PersistenceOption.FLUSH,PersistenceOption.REFRESH);
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }

    @Test
    public void existingParent_changeNonUpdatableChildObject() {
        AccountExtension acct = getExAccount();

        enableJotmLogging();
        AccountType acctType = getDOS().find(AccountType.class, INCOME_ACCOUNT_TYPE_CODE);
        assertNotNull( "Error retrieving IAT account type from data object service", acctType );

        acct.setAccountType(acctType);
        assertEquals( "Before saving account type code should have had the old value", EXPENSE_ACCOUNT_TYPE_CODE, acct.getAccountTypeCode());
        acct = getDOS().save(acct);
        // reload to be sure
        acct = getExAccount();
        assertEquals( "After saving, the acct type code should not have changed - setting the object does not change the key values when the reference updatable==false", EXPENSE_ACCOUNT_TYPE_CODE, acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        disableJotmLogging();
    }

    @Test
    public void existingParent_noExistingChildValue_setChildValue() {
        AccountExtension acct = getNullAccount();

        acct.setAccountTypeCode(INCOME_ACCOUNT_TYPE_CODE);

        // test what object getter does
        LOG.debug( "Account Type After setting FK to 'IAT': " + acct.getAccountType());
        enableJotmLogging();
        acct = getDOS().save(acct,PersistenceOption.FLUSH,PersistenceOption.REFRESH);
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }

    @Test
    public void existingParent_noExistingChildValue_setChildObject() {
        AccountExtension acct = getNullAccount();

        enableJotmLogging();
        AccountType acctType = getDOS().find(AccountType.class, INCOME_ACCOUNT_TYPE_CODE);
        assertNotNull( "Error retrieving IN account type from data object service", acctType );

        acct.setAccountType(acctType);
        assertNull( "Before saving account type code should have had the old value", acct.getAccountTypeCode());
        acct = getDOS().save(acct);
        assertEquals( "After saving, the acct type code should have been set to the PK from the acct type", acctType.getAccountTypeCode(), acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }

    @Test
    public void existingParent_noExistingCollectionRecords_settingOfCollectionKeysOnNew() {
        // Get the AM record
        AccountManager am = getDOS().find(AccountManager.class, 1L);
        assertNotNull( "Error retrieving account manager", am );

        // create the new collection records
        ArrayList<Account> accounts = new ArrayList<Account>();
        accounts.add( new Account( "a", "Account a") );
        accounts.add( new Account( "b", "Account b") );
        am.setAccounts(accounts);

        for ( Account a : am.getAccounts() ) {
            assertNull( "Before save, the FO ID on the Accounts should have been null", a.getAmId() );
        }
        am = getDOS().save(am);
        for ( Account a : am.getAccounts() ) {
            assertEquals( "After the save, the FO ID on the Accounts should have been the same as the AccountManager", am.getAmId(), a.getAmId() );
        }
    }

    @Test
    public void newParentObject_noExistingCollectionRecords_settingOfCollectionKeysOnNew() {
        AccountManager am = new AccountManager( 2L, "Two" );

        // create the new collection records
        ArrayList<Account> accounts = new ArrayList<Account>();
        accounts.add( new Account( "a", "Account a") );
        accounts.add( new Account( "b", "Account b") );
        am.setAccounts(accounts);

        for ( Account a : am.getAccounts() ) {
            assertNull( "Before save, the FO ID on the Accounts should have been null", a.getAmId() );
        }
        am = getDOS().save(am);
        for ( Account a : am.getAccounts() ) {
            assertEquals( "After the save, the FO ID on the Accounts should have been the same as the AccountManager", am.getAmId(), a.getAmId() );
        }
    }

    @Test
    public void newParentObjectWithGeneratedKey_noExistingCollectionRecords_settingOfCollectionKeysOnNew() {
        ParentObjectWithGeneratedKey obj = new ParentObjectWithGeneratedKey();

        // create the new collection records
        ArrayList<ChildOfParentObjectWithGeneratedKey> children = new ArrayList<ChildOfParentObjectWithGeneratedKey>();
        children.add( new ChildOfParentObjectWithGeneratedKey( 1L ) );
        children.add( new ChildOfParentObjectWithGeneratedKey( 22L ) );
        children.add( new ChildOfParentObjectWithGeneratedKey( 333L ) );
        obj.setChildren(children);

        for ( ChildOfParentObjectWithGeneratedKey c : obj.getChildren() ) {
            assertNull( "Before save, the PK ID on the children should have been null", c.getGeneratedKey() );
        }
        System.err.println( "Before Saving: " + obj );
        obj = getDOS().save(obj);
        System.err.println( "After Saving: " + obj );
        assertNotNull( "After saving, the generated key should have had a value", obj.getGeneratedKey());
        for ( ChildOfParentObjectWithGeneratedKey c : obj.getChildren() ) {
            assertEquals( "After the save, the PK ID on the children should have been the same as the parent", obj.getGeneratedKey(), c.getGeneratedKey() );
        }
    }

    @Test
    public void existingParent_setChildKeyValueToNull() {
        AccountExtension acct = getExAccount();

        acct.setAccountTypeCode(null);
        //acct.setAccountType(null);

        // test what object getter does
        LOG.debug( "Account Type After setting FK to null: " + acct.getAccountType());
        enableJotmLogging();
        acct = getDOS().save(acct,PersistenceOption.FLUSH,PersistenceOption.REFRESH);
        assertNull( "After saving, the acct type code should be null", acct.getAccountTypeCode());
        assertNull( "After saving, the acct type object should not be available", acct.getAccountType());
        disableJotmLogging();
    }

    @Test
    public void existingParent_setChildObjectToNull() {
        AccountExtension acct = getExAccount();

        acct.setAccountType(null);

        // test what object getter does
        enableJotmLogging();
        acct = getDOS().save(acct,PersistenceOption.FLUSH,PersistenceOption.REFRESH);
        assertEquals( "After saving, the acct type code should be unchanged", EXPENSE_ACCOUNT_TYPE_CODE, acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        disableJotmLogging();
    }

    @Test
    public void existingParent_withUpdatableChild_changeChildForeignKey() throws Exception {
        SQLDataLoader sqlDataLoader = new SQLDataLoader("DELETE FROM KRTST_PARENT_OF_UPDATABLE_T");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("DELETE FROM KRTST_UPDATABLE_CHILD_T");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("INSERT INTO KRTST_PARENT_OF_UPDATABLE_T(PK_COL, UPDATABLE_CHILD_KEY_COL) VALUES(1, '123')");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("INSERT INTO KRTST_UPDATABLE_CHILD_T(PK_COL, SOME_DATA_COL) VALUES('123', 'Some Data 123')");
        sqlDataLoader.runSql();

        ParentObjectWithUpdatableChild obj = getDOS().find(ParentObjectWithUpdatableChild.class, 1L);
        assertNotNull( "Unable to find parent object in DB", obj);

        obj.setUpdatableChildsKey("ABC");
        assertEquals( "Child object's key should not be changed before save", "123", obj.getUpdatableChild().getChildKey());
        System.err.println( "Before Saving: " + obj );
        obj = getDOS().save(obj);//, PersistenceOption.FLUSH, PersistenceOption.REFRESH);
        // if the field is a PK field, it may not be updated
        // In fact, in the case where we have embedded an updatable reference object which is linked by the PK,
        // we should always copy that PK value back to the parent
        System.err.println( "After Saving: " + obj );
        assertEquals( "Parent object's foreign key should still be changed after save", "ABC", obj.getUpdatableChildsKey());
        obj = getDOS().find(ParentObjectWithUpdatableChild.class, 1L);
        assertNotNull( "Unable to find object after save", obj);
        System.err.println( "After Reloading: " + obj );
        assertNull( "Child object not missing after save - should have been, since an 'ABC' does not exist", obj.getUpdatableChild());
        //assertEquals( "Child object's key should have been changed after save", "ABC", obj.getUpdatableChild().getChildKey());
    }

    @Test
    public void existingParent_withUpdatableChild_changeChildObject() throws Exception {
        SQLDataLoader sqlDataLoader = new SQLDataLoader("DELETE FROM KRTST_PARENT_OF_UPDATABLE_T");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("DELETE FROM KRTST_UPDATABLE_CHILD_T");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("INSERT INTO KRTST_PARENT_OF_UPDATABLE_T(PK_COL, UPDATABLE_CHILD_KEY_COL) VALUES(1, '123')");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("INSERT INTO KRTST_UPDATABLE_CHILD_T(PK_COL, SOME_DATA_COL) VALUES('123', 'Some Data 123')");
        sqlDataLoader.runSql();
        sqlDataLoader = new SQLDataLoader("INSERT INTO KRTST_UPDATABLE_CHILD_T(PK_COL, SOME_DATA_COL) VALUES('456', 'Some Data 456')");
        sqlDataLoader.runSql();

        ParentObjectWithUpdatableChild obj = getDOS().find(ParentObjectWithUpdatableChild.class, 1L);
        assertNotNull( "Unable to find parent object in DB", obj);
        UpdatableChildObject child456 = getDOS().find(UpdatableChildObject.class, "456");
        assertNotNull( "Unable to find child 456 in DB", child456);

        obj.setUpdatableChild(child456);
        System.err.println( "Before Saving: " + obj );
        obj = getDOS().save(obj);//, PersistenceOption.FLUSH, PersistenceOption.REFRESH);
        System.err.println( "After Saving: " + obj );
        assertEquals( "Parent object's foreign key should have been changed after save", "456", obj.getUpdatableChildsKey());
        assertEquals( "Child object's key should not have been reverted after save", "456", obj.getUpdatableChild().getChildKey());
    }

    @Test
    public void newParent_withNewUpdatableChild_setKeyOnParent() throws Exception {
        ParentObjectWithUpdatableChild obj = new ParentObjectWithUpdatableChild();
        obj.setPrimaryKey(56L);

        UpdatableChildObject child = new UpdatableChildObject();
        child.setSomeData( "Some More Data");
        obj.setUpdatableChildsKey("789");
        obj.setUpdatableChild(child);

        System.err.println( "Before Saving: " + obj );
        obj = getDOS().save(obj);
        System.err.println( "After Saving: " + obj );
        assertEquals( "Child object's key should have been set after save", "789", obj.getUpdatableChild().getChildKey());

        obj = getDOS().find(ParentObjectWithUpdatableChild.class, 56L);
        assertNotNull("after reload from DB, unable to find object", obj);
        child = getDOS().find(UpdatableChildObject.class, "789");
        assertNotNull("after reload from DB, unable to find child object", child);
        assertNotNull("after reload from DB, child object was not loaded", obj.getUpdatableChild());
    }

    @Test
    public void newParent_withNewUpdatableChild_setKeyOnChild() throws Exception {
        ParentObjectWithUpdatableChild obj = new ParentObjectWithUpdatableChild();
        obj.setPrimaryKey(56L);

        UpdatableChildObject child = new UpdatableChildObject();
        child.setSomeData( "Some More Data");
        child.setChildKey("789");
        obj.setUpdatableChild(child);

        System.err.println( "Before Saving: " + obj );
        obj = getDOS().save(obj, PersistenceOption.FLUSH, PersistenceOption.REFRESH);
        System.err.println( "After Saving: " + obj );
        child = getDOS().find(UpdatableChildObject.class, "789");
        assertNotNull("after reload from DB, unable to find child object", child);

        // TODO: ReferenceLinker needs to set the keys on the parent (if null) when the child object has been changed.
        // BUT!!!  Only if the foreign keys are not part of the parent's primary key, since those can't be changed.
        assertEquals( "Parent object's key should have been set after save", "789", obj.getUpdatableChildsKey());

        obj = getDOS().find(ParentObjectWithUpdatableChild.class, 56L);
        assertNotNull("after reload from DB, unable to find object", obj);
        assertNotNull("after reload from DB, child object was not loaded", obj.getUpdatableChild());
    }
    /*
    INSERT INTO KRTST_PARENT_OF_UPDATABLE_T(PK_COL, UPDATABLE_CHILD_KEY_COL) VALUES(1, '123')
    /
    INSERT INTO KRTST_UPDATABLE_CHILD_T(PK_COL, SOME_DATA_COL) VALUES('123', 'Some Data 123')
    /
    INSERT INTO KRTST_PARENT_OF_UPDATABLE_T(PK_COL, UPDATABLE_CHILD_KEY_COL) VALUES(2, NULL)
    /
    INSERT INTO KRTST_UPDATABLE_CHILD_T(PK_COL, SOME_DATA_COL) VALUES('456', 'Some Data 456')
    /

     */
    // TODO: Test deletion of child records - references and collections

    protected AccountExtension getExAccount() {
        AccountExtension acct = getDOS().find(AccountExtension.class, "EX_TYPE");
        assertNotNull("unable to retrieve EX_TYPE from database", acct);
        assertEquals( "Incorrect acct type on EX_TYPE database record", acct.getAccountTypeCode(), EXPENSE_ACCOUNT_TYPE_CODE );

        assertNotNull( "the acct type object should be available", acct.getAccountType());
        assertEquals( "the acct type code on the object should be the same as the reference", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());

        return acct;
    }

    protected AccountExtension getNullAccount() {
        AccountExtension acct = getDOS().find(AccountExtension.class, "NULL_TYPE");
        assertNotNull("unable to retrieve NULL_TYPE from database", acct);
        assertNull( "Incorrect acct type on NULL_TYPE database record.", acct.getAccountTypeCode() );

        assertNull( "the acct type object should not be available", acct.getAccountType());

        return acct;
    }

    void enableJotmLogging() {
        //Logger.getLogger("org.objectweb.jotm").setLevel(Level.DEBUG);
    }

    void disableJotmLogging() {
        //Logger.getLogger("org.objectweb.jotm").setLevel(Level.WARN);
    }
}
