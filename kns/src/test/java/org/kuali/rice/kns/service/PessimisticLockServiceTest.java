/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
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
package org.kuali.rice.kns.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants.PermissionNames;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.authorization.PessimisticLock;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.service.impl.PessimisticLockServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.test.TestBase;


/**
 * This class is used to test the {@link PessimisticLockServiceImpl} class
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class PessimisticLockServiceTest extends TestBase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    /**
     * This method tests deleting {@link PessimisticLock} objects. Tests that invalid deletes throw exceptions and valid
     * deletes by owner users as well as lock admin users do work as expected
     *
     * @throws Exception
     */
    @UnitTestData(
            sqlStatements = {
                    @UnitTestSql("DELETE FROM KRNS_PESSIMISTIC_LOCK_T"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1111, SYS_GUID(), 0, NULL, '1234', to_date('07/01/2007','mm/dd/yyyy'), 'employee')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1112, SYS_GUID(), 0, NULL, '1235', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1113, SYS_GUID(), 0, NULL, '1236', to_date('08/01/2007','mm/dd/yyyy'), 'fred')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1114, SYS_GUID(), 0, NULL, '1237', to_date('08/01/2007','mm/dd/yyyy'), 'fred')")
                    }
            )
    @Test
    public void testDeleteLocks() throws Exception {
        List<PessimisticLock> locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 4 locks in DB", 4, locks.size());

        String userId = "employee";
        String[] lockIdsToVerify = new String[]{"1112", "1113"};
        assertFalse("User " + userId + " should not be member of pessimistic lock admin permission", KIMServiceLocator.getIdentityManagementService().isAuthorized(new UserSession(userId).getPerson().getPrincipalId(), KNSConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING, null, null ) );
        verifyDelete(userId, Arrays.asList(lockIdsToVerify), AuthorizationException.class, true);
        userId = "frank";
        lockIdsToVerify = new String[]{"1111", "1113"};
        assertFalse("User " + userId + " should not be member of pessimistic lock admin permission", KIMServiceLocator.getIdentityManagementService().isAuthorized(new UserSession(userId).getPerson().getPrincipalId(), KNSConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING, null, null ) );
        verifyDelete(userId, Arrays.asList(lockIdsToVerify), AuthorizationException.class, true);
        userId = "fred";
        lockIdsToVerify = new String[]{"1111", "1112"};
        assertFalse("User " + userId + " should not be member of pessimistic lock admin permission", KIMServiceLocator.getIdentityManagementService().isAuthorized(new UserSession(userId).getPerson().getPrincipalId(), KNSConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING, null, null ) );
        verifyDelete(userId, Arrays.asList(lockIdsToVerify), AuthorizationException.class, true);

        verifyDelete("employee", Arrays.asList(new String[]{"1111"}), null, false);
        verifyDelete("frank", Arrays.asList(new String[]{"1112"}), null, false);
        verifyDelete("fred", Arrays.asList(new String[]{"1113"}), null, false);
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 1 lock left in DB", 1, locks.size());

        // test admin user can delete any lock
        userId = "fran";
        assertTrue("User " + userId + " should be member of pessimistic lock admin permission", KIMServiceLocator.getIdentityManagementService().isAuthorized(new UserSession(userId).getPerson().getPrincipalId(), KNSConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING, null, null ) );
        userId = "admin";
        assertTrue("User " + userId + " should be member of pessimistic lock admin permission", KIMServiceLocator.getIdentityManagementService().isAuthorized(new UserSession(userId).getPerson().getPrincipalId(), KNSConstants.KNS_NAMESPACE, PermissionNames.ADMIN_PESSIMISTIC_LOCKING, null, null ) );
        verifyDelete(userId, Arrays.asList(new String[]{"1114"}), null, false);
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 0 locks left in DB", 0, locks.size());
    }

    private void verifyDelete(String userId, List<String> lockIds, Class expectedException, boolean expectException) throws WorkflowException {
        GlobalVariables.setUserSession(new UserSession(userId));
        for (String lockId : lockIds) {
            try {
                KNSServiceLocator.getPessimisticLockService().delete(lockId);
                if (expectException) {
                    fail("Expected exception when deleting lock with id '" + lockId + "' for user '" + userId + "'");
                }
            } catch (Exception e) {
                if (!expectException) {
                    fail("Did not expect exception when deleting lock with id '" + lockId + "' for user '" + userId + "' but got exception of type '" + e.getClass().getName() + "'");
                }
                if (expectedException != null) {
                    // if we have an expected exception
                    if (!expectedException.isAssignableFrom(e.getClass())) {
                        fail("Expected exception of type '" + expectedException.getName() + "' when deleting lock with id '" + lockId + "' for user '" + userId + "' but got exception of type '" + e.getClass().getName() + "'");
                    }
                }
            }
        }
    }

    /**
     * This method tests the generation of new {@link PessimisticLock} objects
     *
     * @throws Exception
     */
    @Test
    public void testGenerateNewLocks() throws Exception {
        PessimisticLockService lockService = KNSServiceLocator.getPessimisticLockService();

        // test generating lock with no given lock descriptor
        String documentNumber = "1243";
        PessimisticLock lock = lockService.generateNewLock(documentNumber);
        assertNotNull("Generated lock should have id", lock.getId());
        assertEquals("Document Number should match", documentNumber, lock.getDocumentNumber());
        assertNotNull("Generated lock should have a generated timestamp ", lock.getGeneratedTimestamp());
        assertEquals("Generated lock should have default lock descriptor", PessimisticLock.DEFAULT_LOCK_DESCRIPTOR, lock.getLockDescriptor());
        assertEquals("Generated lock should be owned by current user", GlobalVariables.getUserSession().getPerson().getPrincipalName(), lock.getOwnedByUser().getPrincipalName());
        Map primaryKeys = new HashMap();
        primaryKeys.put(KNSPropertyConstants.ID, lock.getId());
        lock = null;
        lock = (PessimisticLock) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(PessimisticLock.class, primaryKeys);
        assertNotNull("Generated lock should be available from BO Service", lock);
        assertNotNull("Generated lock should have id", lock.getId());
        assertEquals("Document Number should match", documentNumber, lock.getDocumentNumber());
        assertNotNull("Generated lock should have a generated timestamp ", lock.getGeneratedTimestamp());
        assertEquals("Generated lock should have default lock descriptor", PessimisticLock.DEFAULT_LOCK_DESCRIPTOR, lock.getLockDescriptor());
        assertEquals("Generated lock should be owned by current user", GlobalVariables.getUserSession().getPerson().getPrincipalName(), lock.getOwnedByUser().getPrincipalName());

        // test generating lock with given lock descriptor
        lock = null;
        documentNumber = "4321";
        String lockDescriptor = "this is a test lock descriptor";
        lock = lockService.generateNewLock(documentNumber, lockDescriptor);
        assertNotNull("Generated lock should have id", lock.getId());
        assertEquals("Document Number should match", documentNumber, lock.getDocumentNumber());
        assertNotNull("Generated lock should have a generated timestamp ", lock.getGeneratedTimestamp());
        assertEquals("Generated lock should have lock descriptor set", lockDescriptor, lock.getLockDescriptor());
        assertEquals("Generated lock should be owned by current user", GlobalVariables.getUserSession().getPerson().getPrincipalName(), lock.getOwnedByUser().getPrincipalName());
        primaryKeys = new HashMap();
        primaryKeys.put(KNSPropertyConstants.ID, lock.getId());
        lock = null;
        lock = (PessimisticLock) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(PessimisticLock.class, primaryKeys);
        assertNotNull("Generated lock should be available from BO Service", lock);
        assertNotNull("Generated lock should have id", lock.getId());
        assertEquals("Document Number should match", documentNumber, lock.getDocumentNumber());
        assertNotNull("Generated lock should have a generated timestamp ", lock.getGeneratedTimestamp());
        assertEquals("Generated lock should have lock descriptor set", lockDescriptor, lock.getLockDescriptor());
        assertEquals("Generated lock should be owned by current user", GlobalVariables.getUserSession().getPerson().getPrincipalName(), lock.getOwnedByUser().getPrincipalName());
    }

    /**
     * This method tests retrieving {@link PessimisticLock} objects by document number
     *
     * @throws Exception
     */
    @UnitTestData(
            sqlStatements = {
                    @UnitTestSql("DELETE FROM KRNS_PESSIMISTIC_LOCK_T"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1111, SYS_GUID(), 0, NULL, '1234', to_date('07/01/2007','mm/dd/yyyy'), 'fran')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1112, SYS_GUID(), 0, NULL, '1237', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1113, SYS_GUID(), 0, NULL, '1236', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1114, SYS_GUID(), 0, NULL, '1237', to_date('08/01/2007','mm/dd/yyyy'), 'fred')")
                    }
            )
    @Test
    public void testGetPessimisticLocksForDocument() throws Exception {
        PessimisticLockService lockService = KNSServiceLocator.getPessimisticLockService();
        String docId = "1234";
        assertEquals("Document " + docId + " expected lock count incorrect", 1, lockService.getPessimisticLocksForDocument(docId).size());
        docId = "1237";
        assertEquals("Document " + docId + " expected lock count incorrect", 2, lockService.getPessimisticLocksForDocument(docId).size());
        docId = "1236";
        assertEquals("Document " + docId + " expected lock count incorrect", 1, lockService.getPessimisticLocksForDocument(docId).size());
        docId = "3948";
        assertEquals("Document " + docId + " expected lock count incorrect", 0, lockService.getPessimisticLocksForDocument(docId).size());
    }

    /**
     * This method tests releasing {@link PessimisticLock} objects for a specific user
     *
     * @throws Exception
     */
    @UnitTestData(
            sqlStatements = {
                    @UnitTestSql("DELETE FROM KRNS_PESSIMISTIC_LOCK_T"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1111, SYS_GUID(), 0, NULL, '1234', to_date('07/01/2007','mm/dd/yyyy'), 'fran')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1112, SYS_GUID(), 0, NULL, '1235', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1113, SYS_GUID(), 0, NULL, '1236', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1114, SYS_GUID(), 0, NULL, '1237', to_date('08/01/2007','mm/dd/yyyy'), 'fred')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1115, SYS_GUID(), 0, 'Temporary Lock', '1234', to_date('07/01/2007','mm/dd/yyyy'), 'fran')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1116, SYS_GUID(), 0, 'Temporary Lock', '1235', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1117, SYS_GUID(), 0, 'Temporary Lock', '1236', to_date('10/01/2007','mm/dd/yyyy'), 'frank')"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1118, SYS_GUID(), 0, 'Temporary Lock', '1237', to_date('08/01/2007','mm/dd/yyyy'), 'fred')")
                    }
            )
    @Test
    public void testReleaseAllLocksForUser() throws Exception {
        String lockDescriptor = "Temporary Lock";
        List<PessimisticLock> locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 8 manually inserted locks", 8, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("fran"), lockDescriptor);
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 7 locks left after releasing locks for fran using lock descriptor " + lockDescriptor, 7, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("frank"), lockDescriptor);
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 5 locks left after releasing locks for fran and frank using lock descriptor " + lockDescriptor, 5, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("fred"), lockDescriptor);
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 4 locks left after releasing locks for fran, frank, and fred using lock descriptor " + lockDescriptor, 4, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("fran"));
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 3 locks left after releasing locks for fran with no lock descriptor", 3, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("frank"));
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be 1 lock left after releasing locks for fran and frank with no lock descriptor", 1, locks.size());

        KNSServiceLocator.getPessimisticLockService().releaseAllLocksForUser(locks, org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPerson("fred"));
        locks = (List<PessimisticLock>) KNSServiceLocator.getBusinessObjectService().findAll(PessimisticLock.class);
        assertEquals("Should be no locks left after releasing locks for fran, frank, and fred with no lock descriptor", 0, locks.size());
    }

    /**
     * This method tests saving {@link PessimisticLock} objects
     *
     * @throws Exception
     */
    @UnitTestData(
            sqlStatements = {
                    @UnitTestSql("DELETE FROM KRNS_PESSIMISTIC_LOCK_T"),
                    @UnitTestSql("INSERT INTO KRNS_PESSIMISTIC_LOCK_T (\"PESSIMISTIC_LOCK_ID\",\"OBJ_ID\",\"VER_NBR\",\"LOCK_DESC_TXT\",\"DOC_HDR_ID\",\"GNRT_DT\",\"PRNCPL_ID\") VALUES (1111, SYS_GUID(), 0, NULL, '1234', to_date('07/01/2007','mm/dd/yyyy'), 'fran')")
                    }
            )
    @Test
    public void testSaveLock() throws Exception {
        String lockDescriptor = "new test lock descriptor";
        // get existing lock and update lock descriptor and save
        Map primaryKeys = new HashMap();
        primaryKeys.put(KNSPropertyConstants.ID, Long.valueOf("1111"));
        PessimisticLock lock = (PessimisticLock) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(PessimisticLock.class, primaryKeys);
        lock.setLockDescriptor(lockDescriptor);
        KNSServiceLocator.getPessimisticLockService().save(lock);

        // verify retrieved lock has lock descriptor set previously
        PessimisticLock savedLock = (PessimisticLock) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(PessimisticLock.class, primaryKeys);
        assertEquals("Lock descriptor is not correct from lock that was saved", lockDescriptor, savedLock.getLockDescriptor());
    }
}
