/*
 * Copyright 2006-2014 The Kuali Foundation
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

package org.kuali.rice.krad.web.bind;

import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.conference.ConferenceSession;
import org.kuali.rice.krad.test.conference.SessionCoordinator;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.web.form.MaintenanceDocumentForm;
import org.kuali.rice.krad.web.form.UifFormManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Integration test for the {@link org.kuali.rice.krad.web.bind.UifServletRequestDataBinder}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifServletRequestDataBinderIntegrationTest extends KRADTestCase {

    /**
     * Tests auto linking of the ConferenceSession on a SessionCoordinator
     * "document.newMaintainableObject.dataObject.altCoordinator1Id" is passed on the request. This should use
     * "foreign-key-based" linking to fetch the SessionCoordinator
     */
    @Test
    public void testAutoLinking_UsingForeignKey() {
        MaintenanceDocumentForm form = buildMaintenanceDocumentForm();
        ViewPostMetadata viewPostMetadata = new ViewPostMetadata();
        form.setViewPostMetadata(viewPostMetadata);

        MaintenanceDocumentBase maintDoc = (MaintenanceDocumentBase)form.getDocument();

        UifServletRequestDataBinder binder = new UifServletRequestDataBinder(form, "form");
        // auto linking should be on by default
        assertTrue(binder.isChangeTracking());
        assertTrue(binder.isAutoLinking());

        // go ahead and create a SessionCoordinator
        SessionCoordinator sc = new SessionCoordinator();
        sc.setName("admin");
        sc = getDataObjectService().save(sc);
        assertNotNull(sc.getId());

        // before we bind, let's make sure that our account manager is null
        ConferenceSession session = (ConferenceSession)maintDoc.getNewMaintainableObject().getDataObject();
        assertNull(session.getAltCoordinator1());

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));

        UifFormManager formManager = new UifFormManager();
        request.getSession().setAttribute(UifParameters.FORM_MANAGER, formManager);
        String basePath = "document.newMaintainableObject.dataObject.";
        String altCoordIdPath = basePath + "altCoordinator1Id";
        request.addParameter(altCoordIdPath, sc.getId().toString());

        viewPostMetadata.addAccessibleBindingPath(altCoordIdPath);

        // let's bind this sucker
        binder.bind(request);

        // now our session should have an alt coordinator and an alt coordinator id
        assertEquals(sc.getId(), session.getAltCoordinator1Id());
        assertNotNull(session.getAltCoordinator1());
        assertEquals(sc.getId(), session.getAltCoordinator1().getId());
        assertEquals("admin", session.getAltCoordinator1().getName());

    }

    /**
     * Tests auto linking of the SessionCoordinator on an account whenever
     * "document.newMaintainableObject.dataObject.accoumentManager.amId" is passed on the request. This should use
     * "identity-based" linking to fetch the SessionCoordinator
     */
    @Test
    public void testAutoLinking_UsingIdentity() {
        MaintenanceDocumentForm form = buildMaintenanceDocumentForm();
        ViewPostMetadata viewPostMetadata = new ViewPostMetadata();
        form.setViewPostMetadata(viewPostMetadata);

        MaintenanceDocumentBase maintDoc = (MaintenanceDocumentBase)form.getDocument();

        UifServletRequestDataBinder binder = new UifServletRequestDataBinder(form, "form");
        // auto linking should be on by default
        assertTrue(binder.isChangeTracking());
        assertTrue(binder.isAutoLinking());

        // go ahead and create a SessionCoordinator
        SessionCoordinator sc = new SessionCoordinator();
        sc.setName("admin");
        sc = getDataObjectService().save(sc);
        assertNotNull(sc.getId());

        // before we bind, let's make sure that our account manager is null
        ConferenceSession session = (ConferenceSession)maintDoc.getNewMaintainableObject().getDataObject();
        assertNull(session.getAltCoordinator1());

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));

        UifFormManager formManager = new UifFormManager();
        request.getSession().setAttribute(UifParameters.FORM_MANAGER, formManager);
        String basePath = "document.newMaintainableObject.dataObject.";
        String altCoordIdPath = basePath + "altCoordinator1.id";
        request.addParameter(altCoordIdPath, sc.getId().toString());

        viewPostMetadata.addAccessibleBindingPath(altCoordIdPath);

        // let's bind this sucker
        binder.bind(request);

        // now our session should have an "alt 1" coordinator and coordinator id
        assertEquals(sc.getId(), session.getAltCoordinator1Id());
        assertNotNull(session.getAltCoordinator1());
        assertEquals(sc.getId(), session.getAltCoordinator1().getId());
        assertEquals("admin", session.getAltCoordinator1().getName());

    }

    @Test
    public void testModifiedPropertyTracking() {

        MaintenanceDocumentForm form = buildMaintenanceDocumentForm();
        ViewPostMetadata viewPostMetadata = new ViewPostMetadata();
        form.setViewPostMetadata(viewPostMetadata);

        MaintenanceDocumentBase maintDoc = (MaintenanceDocumentBase)form.getDocument();

        UifServletRequestDataBinder binder = new UifServletRequestDataBinder(form, "form");
        // turn off auto linking for this test
        binder.setAutoLinking(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request));

        UifFormManager formManager = new UifFormManager();
        request.getSession().setAttribute(UifParameters.FORM_MANAGER, formManager);
        String basePath = "document.newMaintainableObject.dataObject.";
        String idPath = basePath + "id";
        String titlePath = basePath + "sessionTitle";
        String altCoordinator1IdPath = basePath + "altCoordinator1Id";
        String altCoordinator1_idPath = basePath + "altCoordinator1.id";
        request.addParameter(idPath, "12345");
        request.addParameter(titlePath, "My New ConferenceSession");
        request.addParameter(altCoordinator1IdPath, (String)null);
        request.addParameter(altCoordinator1_idPath, "1");

        viewPostMetadata.addAccessibleBindingPath(idPath);
        viewPostMetadata.addAccessibleBindingPath(titlePath);
        viewPostMetadata.addAccessibleBindingPath(altCoordinator1IdPath);
        viewPostMetadata.addAccessibleBindingPath(altCoordinator1_idPath);

        // now let's run the binding and make sure it bound correctly
        binder.bind(request);

        // verify that it performed proper binding
        ConferenceSession session = (ConferenceSession)maintDoc.getNewMaintainableObject().getDataObject();
        assertEquals("12345", session.getId());
        assertEquals("My New ConferenceSession", session.getSessionTitle());
        assertNull(session.getAltCoordinator1Id());
        assertNotNull(session.getAltCoordinator1());
        assertEquals(Long.valueOf(1), session.getAltCoordinator1().getId());
        assertTrue(session.getAltCoordinator1().getAltCoordinatedSessions1().isEmpty());

        // finally, let's ensure it tracked the appropriate modified properties
        UifBeanPropertyBindingResult result = (UifBeanPropertyBindingResult)binder.getInternalBindingResult();
        Set<String> modifiedPaths = result.getModifiedPaths();

        // should contain these paths
        assertTrue(modifiedPaths.contains(idPath));
        assertTrue(modifiedPaths.contains(titlePath));
        assertTrue(modifiedPaths.contains(altCoordinator1_idPath));

        // should not contain the altCoordinator1Id path since we didn't actually change it's value
        assertFalse(modifiedPaths.contains(altCoordinator1IdPath));

        // total number of modified paths should be 3
        assertEquals(3, modifiedPaths.size());

        // Now let's rebind the same requests, there should be no modified paths this time

        // create a new binder first
        binder = new UifServletRequestDataBinder(form, "form");
        binder.bind(request);
        result = (UifBeanPropertyBindingResult)binder.getInternalBindingResult();
        modifiedPaths = result.getModifiedPaths();
        assertEquals(0, modifiedPaths.size());

        // now let's make a minor change to the amId
        binder = new UifServletRequestDataBinder(form, "form");
        request.setParameter(altCoordinator1IdPath, "2");
        binder.bind(request);

        // check the account, then the modified paths
        assertEquals(Long.valueOf(2), session.getAltCoordinator1Id());
        result = (UifBeanPropertyBindingResult)binder.getInternalBindingResult();
        modifiedPaths = result.getModifiedPaths();
        assertEquals(1, modifiedPaths.size());
        assertTrue(modifiedPaths.contains(altCoordinator1IdPath));

    }

    private MaintenanceDocumentForm buildMaintenanceDocumentForm() {
        MaintenanceDocumentForm form = new MaintenanceDocumentForm();
        MaintenanceDocumentBase document = new MaintenanceDocumentBase();
        ConferenceSession session = new ConferenceSession();
        MaintainableImpl oldMaintainable = new MaintainableImpl();
        oldMaintainable.setDataObject(session);
        MaintainableImpl newMaintainable = new MaintainableImpl();
        newMaintainable.setDataObject(session);
        document.setOldMaintainableObject(oldMaintainable);
        document.setNewMaintainableObject(newMaintainable);

        // let's create a doc id here that is a random value so it shouldn't conflict with anything
        document.setDocumentNumber(UUID.randomUUID().toString());
        form.setDocument(document);
        return form;

    }

    private DataObjectService getDataObjectService() {
        return KradDataServiceLocator.getDataObjectService();
    }

}
