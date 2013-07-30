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
package org.kuali.rice.krad.data.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.kuali.rice.krad.data.metadata.DataObjectRelationship;
import org.kuali.rice.krad.data.jpa.JpaPersistenceProvider;
import org.kuali.rice.krad.data.provider.util.ReferenceLinker;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.AccountExtension;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.test.BaselineTestCase;
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
            // Need to fix the PK on this table - should not have contained the account type
            sqlStatements = {
                    @UnitTestSql("ALTER TABLE trv_acct_ext DROP PRIMARY KEY")
                    ,@UnitTestSql("ALTER TABLE trv_acct_ext ADD PRIMARY KEY (ACCT_NUM)")
                    ,@UnitTestSql("ALTER TABLE trv_acct_ext MODIFY ACCT_TYPE VARCHAR(100) NULL") // want to allow null values
            })
})
@PerTestUnitTestData(
        value = @UnitTestData(
//                order = {UnitTestData.Type.SQL_STATEMENTS, UnitTestData.Type.SQL_FILES},
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct_ext")
                        ,@UnitTestSql("delete from trv_acct_type")
                        ,@UnitTestSql("INSERT INTO trv_acct_type ( ACCT_TYPE, ACCT_TYPE_NAME, OBJ_ID, VER_NBR ) VALUES ( 'EX', 'EXPENSE', 'EX', 1 )")
                        ,@UnitTestSql("INSERT INTO trv_acct_type ( ACCT_TYPE, ACCT_TYPE_NAME, OBJ_ID, VER_NBR ) VALUES ( 'IN', 'INCOME', 'IN', 1 )")
                        ,@UnitTestSql("INSERT INTO trv_acct_ext(ACCT_NUM, ACCT_TYPE, OBJ_ID, VER_NBR) VALUES('NULL_TYPE', NULL, 'NULL_TYPE', 1)")
                        ,@UnitTestSql("INSERT INTO trv_acct_ext(ACCT_NUM, ACCT_TYPE, OBJ_ID, VER_NBR) VALUES('EX_TYPE', 'EX', 'EX_TYPE', 1)")
                }
//               ,sqlFiles = {
//                        @UnitTestFile(filename = "classpath:testAccountManagers.sql", delimiter = ";")
//                        , @UnitTestFile(filename = "classpath:testAccounts.sql", delimiter = ";")
//                }
        )
//       ,tearDown = @UnitTestData(
//                sqlStatements = {
//                        @UnitTestSql("delete from trv_acct where acct_fo_id between 101 and 301")
//                        ,@UnitTestSql("delete from trv_acct_fo where acct_fo_id between 101 and 301")
//                }
//       )
)
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class ReferenceLinkerTest extends KRADTestCase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferenceLinkerTest.class);
    protected JpaPersistenceProvider getPersistenceProvider() {
        return getKRADTestHarnessContext().getBean("kradJpaPersistenceProvider", JpaPersistenceProvider.class);
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
        getKRADTestHarnessContext().getBean("kradEntityManagerFactory", EntityManagerFactory.class).getCache().evictAll();
    }

    @Test
    public void preCheck_AccountExtension_Metadata() {
        DataObjectMetadata metadata = KradDataServiceLocator.getMetadataRepository().getMetadata(AccountExtension.class);
        assertNotNull("AccountExtension metadata missing", metadata);
        DataObjectRelationship relationship = metadata.getRelationship("accountType");
        assertNotNull("AccountExtension.accountType relationship metadata missing", relationship);
    }

    @Test
    public void persistenceWhenObjectSet_newParentObject() {
        // Create a new object and add an existing account type by object
        AccountExtension acct = new AccountExtension();
        acct.setNumber("NEW_ACCT");

        AccountType acctType = getDOS().find(AccountType.class, "EX");
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
    public void persistenceWhenObjectSet_existingParentObject_changeChildValue() {
        AccountExtension acct = getExAccount();

        acct.setAccountTypeCode("IN");

        // test what object getter does
        LOG.debug( "Account Type After setting FK to 'IN': " + acct.getAccountType());
        enableJotmLogging();
        acct = getDOS().save(acct);
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }

    @Test
    public void persistenceWhenObjectSet_existingParentObject_changeChildObject() {
        AccountExtension acct = getExAccount();

        enableJotmLogging();
        AccountType acctType = getDOS().find(AccountType.class, "IN");
        assertNotNull( "Error retrieving IN account type from data object service", acctType );

        acct.setAccountType(acctType);
        assertEquals( "Before saving account type code should have had the old value", "EX", acct.getAccountTypeCode());
        acct = getDOS().save(acct);
        assertEquals( "After saving, the acct type code should have been set to the PK from the acct type", acctType.getAccountTypeCode(), acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        disableJotmLogging();
    }

    protected AccountExtension getExAccount() {
        AccountExtension acct = getDOS().find(AccountExtension.class, "EX_TYPE");
        assertNotNull("unable to retrieve EX_TYPE from database", acct);
        assertEquals( "Incorrect acct type on EX_TYPE database record", acct.getAccountTypeCode(), "EX" );
        
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
    
    @Test
    public void persistenceWhenObjectSet_existingParentObject_setChildValue() {
        AccountExtension acct = getNullAccount();
               
        acct.setAccountTypeCode("IN");

        // test what object getter does
        LOG.debug( "Account Type After setting FK to 'IN': " + acct.getAccountType());
        enableJotmLogging();
        acct = getDOS().save(acct);
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }

    @Test
    public void persistenceWhenObjectSet_existingParentObject_setChildObject() {
        AccountExtension acct = getNullAccount();
        
        enableJotmLogging();
        AccountType acctType = getDOS().find(AccountType.class, "IN");
        assertNotNull( "Error retrieving IN account type from data object service", acctType );

        acct.setAccountType(acctType);
        assertNull( "Before saving account type code should have had the old value", acct.getAccountTypeCode());
        acct = getDOS().save(acct);
        assertEquals( "After saving, the acct type code should have been set to the PK from the acct type", acctType.getAccountTypeCode(), acct.getAccountTypeCode());
        assertNotNull( "After saving, the acct type object should be available", acct.getAccountType());
        assertEquals( "After Saving, the acct type code on the reference should be the same as the object", acct.getAccountTypeCode(), acct.getAccountType().getAccountTypeCode());
        disableJotmLogging();
    }
    
    public void persistenceWhenObjectSet_existingParentObject_blankOutChildValue() {
        fail( "Not Implemented");
    }

    public void persistenceWhenObjectSet_existingParentObject_blankOutChildObject() {
        fail( "Not Implemented");
    }
    
    public void persistenceWithUnsetGeneratedKey() {
        // Todo : make sure that the child object is saved first and the new key stored
        // SimpleAccount has a generated key - create a parent object for that
        fail( "Not Implemented");
    }

    public void addingUpdatableChildObject() {
        fail( "Not Implemented");
    }

    void enableJotmLogging() {
        //Logger.getLogger("org.objectweb.jotm").setLevel(Level.DEBUG);
    }

    void disableJotmLogging() {
        //Logger.getLogger("org.objectweb.jotm").setLevel(Level.WARN);
    }
}
