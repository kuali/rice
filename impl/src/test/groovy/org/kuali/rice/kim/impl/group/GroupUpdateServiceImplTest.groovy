package org.kuali.rice.kim.impl.group

import org.junit.Test
import org.junit.BeforeClass
import java.sql.Timestamp
import org.kuali.rice.kim.impl.type.KimTypeBo
import groovy.mock.interceptor.MockFor
import org.kuali.rice.kns.service.BusinessObjectService
import org.junit.Before
import org.kuali.rice.kim.api.group.GroupService
import org.kuali.rice.kim.service.impl.GroupUpdateServiceImpl
import org.kuali.rice.kim.api.group.GroupUpdateService
import org.kuali.rice.kns.bo.PersistableBusinessObject


class GroupUpdateServiceImplTest {

    //importing the should fail method since I don't want to extend
    //GroovyTestCase which is junit 3 style
    private final shouldFail = new GroovyTestCase().&shouldFail

    MockFor mock
    BusinessObjectService bos

    GroupServiceImpl groupServiceImpl
    GroupService groupService
    GroupUpdateService groupUpdateService
    GroupUpdateServiceImpl groupUpdateServiceImpl

    @Before
    void setupMockContext() {
        mock = new MockFor(BusinessObjectService.class)

    }

    @Before
    void setupServiceUnderTest() {
        groupServiceImpl = new GroupServiceImpl()
        groupService = groupServiceImpl    //assign Interface type to implementation reference for unit test only

        groupUpdateServiceImpl = new GroupUpdateServiceImpl()
        groupUpdateService = groupUpdateServiceImpl
    }

    void injectBusinessObjectServiceIntoGroupUpdateService() {
        bos = mock.proxyDelegateInstance()
        groupUpdateServiceImpl.setBusinessObjectService(bos)
    }

    @Test
    void test_createGroupNullGroup(){
        injectBusinessObjectServiceIntoGroupUpdateService()

        shouldFail(IllegalArgumentException.class) {
            groupUpdateService.createGroup(null)
        }
        mock.verify(bos)

    }

    @Test
    void test_updateGroupNullGroup(){
        injectBusinessObjectServiceIntoGroupUpdateService()

        shouldFail(IllegalArgumentException.class) {
            groupUpdateService.updateGroup(null, null)
        }
        mock.verify(bos)

    }


}
