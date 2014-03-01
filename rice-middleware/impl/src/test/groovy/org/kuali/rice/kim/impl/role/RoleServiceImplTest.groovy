/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.impl.role

import groovy.mock.interceptor.MockFor
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.core.api.criteria.*
import org.kuali.rice.kim.api.KimConstants
import org.kuali.rice.kim.api.role.*
import org.kuali.rice.krad.data.DataObjectService
import org.kuali.rice.krad.data.PersistenceOption

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal

/**
 * Some basic, largely read-only tests of interacting with RoleService.
 * This test is extended by the RoleServiceRemoteTest integration test in order
 * to verify remote usage.
 */
class RoleServiceImplTest {
    private final shouldFail = new GroovyTestCase().&shouldFail

    static Map<String, RoleBo> sampleRoles = new HashMap<String, RoleBo>()
    static Map<String, RoleBo> sampleRolesByName= new HashMap<String, RoleBo>()

    MockFor dataObjectServiceMockFor
    DataObjectService dos
    static GenericQueryResults queryResultsAll

    RoleServiceImpl roleServiceImpl
    RoleService roleService

    @BeforeClass
    static void createSampleRoleBOs() {
        Calendar calendarFrom = Calendar.getInstance();

        //Doing setup in a static context since bring up and down a server is an expensive operation
        RoleBoLite someRole = new RoleBoLite(active: true, id: "1", namespaceCode: "PUNK",
                name: "somerole", description: "this is some role", kimTypeId: "1")
        RoleBoLite otherRole = new RoleBoLite(active: true, id: "2", namespaceCode: "ROCK",
                name: "otherrole", description: "this is some other role", kimTypeId: "2")
        RoleBoLite  thirdRole = new RoleBoLite(active: true, id: "114", namespaceCode: "SOMETHING",
                name: "HMMM", description: "this is some weird role", kimTypeId: "1")
        for (bo in [someRole, otherRole, thirdRole]) {
            sampleRoles.put(bo.id, bo)
            sampleRolesByName.put(bo.namespaceCode + ";" + bo.name, bo)
        }

        //setup roleQueryResults
        GenericQueryResults.Builder builder = GenericQueryResults.Builder.create()

        builder.setResults(new ArrayList<RoleBo> (sampleRoles.values()))
        builder.setTotalRowCount(new Integer(3))
        queryResultsAll = builder.build()
    }

    protected RoleBoLite toRoleBoLite(RoleContract r) {
        def rbl = new RoleBoLite();
        rbl.id = r.id
        rbl.active =  r.active
        rbl.description = r.description
        rbl.kimTypeId = r.kimTypeId
        rbl.name = r.name
        rbl.namespaceCode = r.namespaceCode
        return rbl
    }

    @Before
    void setupMockContext() {
        dataObjectServiceMockFor = new MockFor(DataObjectService.class)
    }

    @Before
    void setupServiceUnderTest() {
        roleServiceImpl = new RoleServiceImpl()
        roleServiceImpl.@proxiedRoleService = roleServiceImpl
        roleService = roleServiceImpl //assign Interface type to implementation reference for unit test only
    }

    void injectDataObjectServiceIntoRoleService() {
        dos =  dataObjectServiceMockFor.proxyDelegateInstance()
        roleServiceImpl.setDataObjectService(dos)
    }

    @Test
    public void test_getRole() {
        dataObjectServiceMockFor.demand.find(1..sampleRoles.size()) {
            Class clazz, Object primaryKey -> return toRoleBoLite(sampleRoles.get(primaryKey))
        }
        injectDataObjectServiceIntoRoleService()
        for (String id : sampleRoles.keySet()) {
            Role role = roleService.getRole(id)
            Assert.assertEquals(RoleBoLite.to(sampleRoles.get(id)), role)
        }
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    public void test_getRoleNonExistent() {
        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object primaryKey -> return null
        }
        injectDataObjectServiceIntoRoleService()
        Role role = roleService.getRole("badId")
        Assert.assertNull(role)
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    public void test_getRoleByName() {
        dataObjectServiceMockFor.demand.findMatching(1..sampleRolesByName.size()) {
            Class clazz, QueryByCriteria criteria ->
                Map<String, Object> map = new HashMap<String, Object>()
                AndPredicate and = criteria.getPredicate()
                Set<Predicate> predicates = and.getPredicates()
                for (Predicate predicate : predicates) {
                    map.put(predicate.getPropertyPath(), predicate.getValue().getValue())
                }

                RoleBoLite roleBoLite = toRoleBoLite(sampleRolesByName.get(map.get(KimConstants.UniqueKeyConstants.NAMESPACE_CODE) + ";" + map.get(KimConstants.UniqueKeyConstants.NAME)))

                GenericQueryResults.Builder results = GenericQueryResults.Builder.create()
                results.getResults().add(roleBoLite)

                return results.build()
        }
        injectDataObjectServiceIntoRoleService()

        for (String name : sampleRolesByName.keySet()) {
            RoleBoLite tempGroup = sampleRolesByName.get(name)
            Role role = roleService.getRoleByNamespaceCodeAndName(tempGroup.namespaceCode, tempGroup.name)
            Assert.assertEquals(RoleBoLite.to(sampleRolesByName.get(name)), role)
        }
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    public void test_getRoleByNameNonExistent() {
        dataObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, QueryByCriteria criteria -> GenericQueryResults.Builder.create().build();
        }
        injectDataObjectServiceIntoRoleService()

        Role role = roleService.getRoleByNamespaceCodeAndName("badNamespace", "noname")
        Assert.assertNull(role)
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    public void test_lookupRoles() {
        dataObjectServiceMockFor.demand.findMatching(1) {
            Class clazz, QueryByCriteria query -> return queryResultsAll
        }

        injectDataObjectServiceIntoRoleService()

        List<Role> expectedRoles = new ArrayList<Role>()
        for (RoleBoLite roleBo : sampleRoles.values()) {
            expectedRoles.add(RoleBoLite.to(roleBo))
        }

        QueryByCriteria.Builder query = QueryByCriteria.Builder.create()
        query.setPredicates(equal("active", "Y"))
        RoleQueryResults qr = roleService.findRoles(query.build())

        Assert.assertEquals(qr.getTotalRowCount(), sampleRoles.size())
        Assert.assertEquals(expectedRoles, qr.getResults())
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_getRoleMembers_null() {
        shouldFail(IllegalArgumentException.class) {
            roleService.getRoleMembers(null, null)
        }
    }

    @Test
    void test_createRoleNullRole(){
        injectDataObjectServiceIntoRoleService()

        shouldFail(IllegalArgumentException.class) {
            roleService.createRole(null)
        }
        dataObjectServiceMockFor.verify(dos)

    }

    @Test
    void test_updateRoleNullRole(){
        injectDataObjectServiceIntoRoleService()

        shouldFail(IllegalArgumentException.class) {
            roleService.updateRole(null)
        }
        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_createRoleResponsibilityAction_inputs() {
        shouldFail(IllegalArgumentException.class) {
            roleService.createRoleResponsibilityAction(null)
        }

        def bo = new RoleResponsibilityActionBo()
        bo.setId("1234")

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return bo
        }

        injectDataObjectServiceIntoRoleService()

        // throws RiceIllegalStateException in unit tests
        // Exception/SOAPFaultException in remote tests
        shouldFail(Exception.class) {
            roleService.createRoleResponsibilityAction(RoleResponsibilityActionBo.to(bo))
        }

        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_updateRoleResponsibilityAction_inputs() {
        shouldFail(IllegalArgumentException.class) {
            roleService.updateRoleResponsibilityAction(null)
        }

        def bo = new RoleResponsibilityActionBo()
        bo.setId("1234")

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return null
        }

        injectDataObjectServiceIntoRoleService()

        // throws RiceIllegalStateException in unit tests
        // Exception/SOAPFaultException in remote tests
        shouldFail(Exception.class) {
            roleService.updateRoleResponsibilityAction(RoleResponsibilityActionBo.to(bo))
        }

        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_deleteRoleResponsibilityAction_null() {
        shouldFail(IllegalArgumentException.class) {
            roleService.deleteRoleResponsibilityAction(null)
        }

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return null
        }

        injectDataObjectServiceIntoRoleService()

        // throws RiceIllegalStateException in unit tests
        // Exception/SOAPFaultException in remote tests
        shouldFail(Exception.class) {
            roleService.deleteRoleResponsibilityAction("1234")
        }

        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_createRoleResponsibilityAction() {
        def builder = RoleResponsibilityAction.Builder.create()
        builder.setId("1234")

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return null
        }

        dataObjectServiceMockFor.demand.save(1) {
            Object s, PersistenceOption... options -> return s
        }

        injectDataObjectServiceIntoRoleService()

        def saved = roleService.createRoleResponsibilityAction(builder.build())

        Assert.assertEquals(builder.build(), saved)

        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_updateRoleResponsibilityAction() {
        def bo = new RoleResponsibilityActionBo()
        bo.setId("1234")

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return bo
        }

        dataObjectServiceMockFor.demand.save(1) {
            Object s, PersistenceOption... options -> return s
        }

        injectDataObjectServiceIntoRoleService()

        def saved = roleService.updateRoleResponsibilityAction(RoleResponsibilityActionBo.to(bo))

        Assert.assertEquals(RoleResponsibilityActionBo.to(bo), saved)

        dataObjectServiceMockFor.verify(dos)
    }

    @Test
    void test_deleteRoleResponsibilityAction() {
        def bo = new RoleResponsibilityActionBo()
        bo.setId("1234")

        dataObjectServiceMockFor.demand.find(1) {
            Class clazz, Object id -> return bo
        }

        dataObjectServiceMockFor.demand.delete(1) {
            Object s -> Assert.assertEquals(bo, s)
        }

        injectDataObjectServiceIntoRoleService()

        roleService.deleteRoleResponsibilityAction("1234")

        dataObjectServiceMockFor.verify(dos)
    }

}
