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

package org.kuali.rice.krad.data.provider.util;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.data.util.ReferenceLinker;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.conference.ConferenceSession;
import org.kuali.rice.krad.test.conference.PresenterInfo;
import org.kuali.rice.krad.test.conference.Room;
import org.kuali.rice.krad.test.conference.SessionCoordinator;
import org.kuali.rice.krad.test.conference.SessionPresenter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.kuali.rice.krad.data.PersistenceOption.FLUSH;
import static org.kuali.rice.krad.data.PersistenceOption.LINK_KEYS;

public class ReferenceLinkerIntegrationTest extends KRADTestCase {

    private DataObjectService dataObjectService;
    private ReferenceLinker referenceLinker;

    @Override
    protected void setUpInternal() throws Exception {
        super.setUpInternal();
        dataObjectService = KradDataServiceLocator.getDataObjectService();
        referenceLinker = new ReferenceLinker();
        referenceLinker.setDataObjectService(dataObjectService);
    }

    @Test
    public void testIdentityLinking() {

        // first, create a SessionCoordinator and save it

        SessionCoordinator savedCoordinator = new SessionCoordinator();
        savedCoordinator.setName("Me");
        savedCoordinator = getDataObjectService().save(savedCoordinator);
        assertNotNull(savedCoordinator.getId());

        // now let's create an ConferenceSession and see if we can link to the SessionCoordinator using it's id

        ConferenceSession session = new ConferenceSession();
        SessionCoordinator coordinator = new SessionCoordinator();
        session.setAltCoordinator1(coordinator);
        coordinator.setId(savedCoordinator.getId());

        // try to perform the linking
        Set<String> changes = new HashSet<String>();
        changes.add("altCoordinator1.id");
        referenceLinker.linkChanges(session, changes);

        // assert that it loaded the relationship fresh and copied the FK back to the session
        SessionCoordinator fetchedCoordinator = session.getAltCoordinator1();
        assertNotNull(fetchedCoordinator);
        assertEquals(savedCoordinator.getId(), fetchedCoordinator.getId());
        assertEquals("Me", fetchedCoordinator.getName());
        assertEquals(savedCoordinator.getId(), session.getAltCoordinator1Id());

        // it should have also added our session to the list of alt sessions on the SessionCordinator to complete
        // the bi-directional relationship
        assertEquals(1, fetchedCoordinator.getAltCoordinatedSessions1().size());
        assertEquals(session, fetchedCoordinator.getAltCoordinatedSessions1().get(0));

    }

    @Test
    public void testIdentityLinking_InvalidKey() {
        ConferenceSession session = new ConferenceSession();
        // set to an account manager id that doesn't exist
        session.setAltCoordinator1Id(Long.valueOf(Long.MAX_VALUE));

        // now attempt to link, it shouldn't fail, but it also shouldn't do anything
        Set<String> changes = new HashSet<String>();
        changes.add("altCoordinator1Id");
        referenceLinker.linkChanges(session, changes);
        assertEquals(Long.valueOf(Long.MAX_VALUE), session.getAltCoordinator1Id());
        assertNull(session.getAltCoordinator1());

        // now set a dummy coordinator on the session, when we link, it should be replaced with null
        SessionCoordinator sc = new SessionCoordinator();
        sc.setName("admin");
        sc.setId(Long.valueOf(12345L));
        session.setAltCoordinator1(sc);
        referenceLinker.linkChanges(session, changes);
        assertEquals(Long.valueOf(Long.MAX_VALUE), session.getAltCoordinator1Id());
        assertNull(session.getAltCoordinator1());

    }

    @Test
    public void testAttributeLinking() {

        // first, create a SessionCoordinator and save it

        SessionCoordinator sc = new SessionCoordinator();
        sc.setName("admin");
        sc = getDataObjectService().save(sc);
        assertNotNull(sc.getId());

        // now let's create a ConferenceSession and see if we can link to the SessionCoordinator using it's FK

        ConferenceSession session = new ConferenceSession();
        session.setAltCoordinator1Id(sc.getId());

        // try to perform the linking
        Set<String> changes = new HashSet<String>();
        changes.add("altCoordinator1Id");
        referenceLinker.linkChanges(session, changes);

        // assert that it loaded the relationship fresh using the FK on the session

        // first, verify the FK on the session was not modified
        assertEquals(session.getAltCoordinator1Id(), sc.getId());
        // next, make sure that SessionCoordinator is no longer null
        SessionCoordinator fetchedSc = session.getAltCoordinator1();
        assertNotNull(fetchedSc);

        // the fetched SessionCoordinator should have all of it's values set appropriately
        assertEquals(session.getAltCoordinator1Id(), fetchedSc.getId());
        assertEquals("admin", fetchedSc.getName());

    }

    @Test
    public void testAttributeLinking_InvalidKey() {

        // now let's create an ConferenceSession with an SessionCoordinator with an invalid id

        ConferenceSession session = new ConferenceSession();
        SessionCoordinator sc = new SessionCoordinator();
        sc.setId(Long.valueOf(-1));
        session.setAltCoordinator1(sc);
        session.setAltCoordinator1Id(Long.valueOf(-2));

        // try to perform the linking
        Set<String> changes = new HashSet<String>();
        changes.add("altCoordinator1.id");
        referenceLinker.linkChanges(session, changes);

        // the result of the linking should be that the coordinator is unchanged since we don't nullify values
        assertEquals(Long.valueOf(-1), session.getAltCoordinator1().getId());

        // it should still copy the FK value back though,
        // so the altCoordinator1Id on the session itself should get changed from -2 to -1
        assertEquals(Long.valueOf(-1), session.getAltCoordinator1Id());

    }

    /**
     * Tests that an FK gets copied back when linking after a change.
     */
    @Test
    public void testAttributeLinking_Backward() {
        // create and save the Room first
        Room room = new Room();
        room.setNumber("1");
        room.setBuildingName("Building");
        room = getDataObjectService().save(room);

        ConferenceSession session = new ConferenceSession();
        session.setRoom(room);
        session.setAltRoom1(room);
        session.setAltRoom2(room);

        // now let's try linking changes
        referenceLinker.linkChanges(session, Sets.newHashSet("room", "altRoom1", "altRoom2"));

        // after linking, both the id's for alt room1 and room2 should have been copied back for us
        assertEquals(room.getId(), session.getAltRoom1Id());
        assertEquals(room.getId(), session.getAltRoom2Id());
    }

    @Test
    public void testIdentityAndAttributeLinking() {

        // first, create an SessionCoordinator and save it

        SessionCoordinator sc = new SessionCoordinator();
        sc.setName("admin");
        sc = getDataObjectService().save(sc);
        assertNotNull(sc.getId());

        // now let's create an ConferenceSession with a reference to an alt coordinator with a different id, but the FK to altCoordinator1Id being set properly

        ConferenceSession session = new ConferenceSession();
        SessionCoordinator coordinator = new SessionCoordinator();
        session.setAltCoordinator1(coordinator);
        coordinator.setId(Long.valueOf(-1));
        session.setAltCoordinator1Id(sc.getId());

        // now we have a situation where session.altCoordinator1Id and session.altCoordinator1.id are different but both were modified!

        // in these cases, as per the spec for this, the value in the foreign key should take precedence, so let's try some linking
        Set<String> changes = new HashSet<String>();
        changes.add("altCoordinator1.id");
        changes.add("altCoordinator1Id");
        referenceLinker.linkChanges(session, changes);

        assertNotNull(session.getAltCoordinator1());
        assertEquals(sc.getId(), session.getAltCoordinator1Id());
        assertEquals(sc.getId(), session.getAltCoordinator1().getId());
        assertEquals("admin", session.getAltCoordinator1().getName());

        // now if we change this slightly so that altCoordinator1Id on the session is invalid, it should replace the altCoordinator1
        // reference with a null value
        session.setAltCoordinator1Id(Long.valueOf(-1));
        referenceLinker.linkChanges(session, changes);
        assertEquals(Long.valueOf(-1), session.getAltCoordinator1Id());
        // account manager should be null
        assertNull(session.getAltCoordinator1());
    }

    @Test
    public void testParentObjectWithNonUpdatableFK() {
        ConferenceSession session = new ConferenceSession();
        session.setSessionTitle("blah");
        SessionCoordinator coordinator = new SessionCoordinator();
        coordinator.setName("someuser");
        session.setAltCoordinator2(coordinator);

        // first let's do a save with no linking
        ConferenceSession savedSession = dataObjectService.save(session, FLUSH);
        assertNotNull(savedSession.getId());
        assertNotNull(savedSession.getAltCoordinator2().getId());
        // the altCoordinator2Id *should* be null, JPA does not automatically link this up for us
        assertNull(savedSession.getAltCoordinator2Id());

        // now if we are feeling fiesty here we can link at this point and end up getting our altCoordinator2Id updated
        referenceLinker.linkChanges(savedSession, Sets.newHashSet("altCoordinator2.id"));
        assertEquals(savedSession.getAltCoordinator2().getId(), savedSession.getAltCoordinator2Id());

        // but what we really want is for it to link that up for us automagically after we save the session, is that
        // so much to ask? Well, we *can* ask for that with the LINK_KEYS parameter
        ConferenceSession savedSession2 = dataObjectService.save(session, LINK_KEYS);
        // first, let's just double check to make sure this is a different session than the last one we saved (note
        // that we saved the "session", not the "savedSession"
        assertNotEquals(savedSession.getId(), savedSession2.getId());
        assertNotEquals(savedSession.getAltCoordinator2().getId(), savedSession2.getAltCoordinator2().getId());

        // at this point our new session should have it's altCoordinator2Id set properly
        assertEquals(savedSession2.getAltCoordinator2().getId(), savedSession2.getAltCoordinator2Id());

        // now if we set the altCoordinator2 to null and save, we ought to get reset back to null on the FK
        savedSession2.setAltCoordinator2(null);
        assertNotNull(savedSession2.getAltCoordinator2Id());

        // save it, altCoordinator2 should still be null, but the altCoordinator2Id on the session should be null now
        savedSession2 = dataObjectService.save(savedSession2, LINK_KEYS);
        assertNull(savedSession2.getAltCoordinator2());
        assertNull(savedSession2.getAltCoordinator2Id());

        // now if we set the altCoordinator2 to null and link, we ought to get reset back to null on the FK
        savedSession.setAltCoordinator2(null);
        assertNotNull(savedSession.getAltCoordinator2Id());

        // link it
        referenceLinker.linkChanges(savedSession, Sets.newHashSet("altCoordinator2"));

        // altCoordinator2 should still be null, but the altCordinator2Id on the session should be null now as well
        assertNull(savedSession.getAltCoordinator2());
        assertNull(savedSession.getAltCoordinator2Id());

    }

    /**
     * Tests that linking recurses down through collections properly.
     */
    @Test
    public void testLinkingThroughCollection() {
        // create 3 PresenterInfo
        PresenterInfo presenter1 = new PresenterInfo();
        presenter1.setName("presenter1");
        presenter1.setInstitution("institution1");
        PresenterInfo presenter2 = new PresenterInfo();
        presenter2.setName("presenter2");
        presenter2.setInstitution("institution2");
        PresenterInfo presenter3 = new PresenterInfo();
        presenter3.setName("presenter3");
        presenter3.setInstitution("institution1");
        presenter1 = getDataObjectService().save(presenter1, FLUSH);
        presenter2 = getDataObjectService().save(presenter2, FLUSH);
        presenter3 = getDataObjectService().save(presenter3, FLUSH);

        // now create the conference session and add 3 session presenters referencing those three presenter info
        ConferenceSession session = new ConferenceSession();

        // set presenter 1 using it's id
        SessionPresenter sessionPresenter1 = new SessionPresenter();
        sessionPresenter1.setPrimary(true);
        sessionPresenter1.setPresenterId(presenter1.getId());

        // set presenter 2 using the id on the reference object
        SessionPresenter sessionPresenter2 = new SessionPresenter();
        PresenterInfo presenter2Id = new PresenterInfo();
        presenter2Id.setId(presenter2.getId());
        sessionPresenter2.setPresenter(presenter2Id);

        // set presenter 3 directly
        SessionPresenter sessionPresenter3 = new SessionPresenter();
        sessionPresenter3.setPresenter(presenter3);

        // add all three to the session
        session.getPresenters().add(sessionPresenter1);
        session.getPresenters().add(sessionPresenter2);
        session.getPresenters().add(sessionPresenter3);

        // now link us some changes
        Set<String> changes = Sets.newHashSet(
                "presenters[0].presenterId",
                "presenters[1].presenter.id",
                "presenters[2].presenter");
        referenceLinker.linkChanges(session, changes);

        // Assert that everything linked properly

        // check the first SessionPresenter
        SessionPresenter linkedPresenter1 = session.getPresenters().get(0);
        assertEquals(session, linkedPresenter1.getSession());
        assertNotNull(linkedPresenter1.getPresenter());
        assertEquals(presenter1.getId(), linkedPresenter1.getPresenterId());
        assertEquals(presenter1.getId(), linkedPresenter1.getPresenter().getId());
        assertEquals(presenter1.getName(), linkedPresenter1.getPresenter().getName());

        // check the second SessionPresenter
        SessionPresenter linkedPresenter2 = session.getPresenters().get(1);
        assertEquals(session, linkedPresenter2.getSession());
        assertNotNull(linkedPresenter2.getPresenter());
        assertEquals(presenter2.getId(), linkedPresenter2.getPresenterId());
        assertEquals(presenter2.getId(), linkedPresenter2.getPresenter().getId());
        assertEquals(presenter2.getName(), linkedPresenter2.getPresenter().getName());

        // check the third SessionPresenter
        SessionPresenter linkedPresenter3 = session.getPresenters().get(2);
        assertEquals(session, linkedPresenter3.getSession());
        assertNotNull(linkedPresenter3.getPresenter());
        assertEquals(presenter3.getId(), linkedPresenter3.getPresenterId());
        assertEquals(presenter3.getId(), linkedPresenter3.getPresenter().getId());
        assertEquals(presenter3.getName(), linkedPresenter3.getPresenter().getName());

        // save and sync keys
        ConferenceSession savedSession = getDataObjectService().save(session, LINK_KEYS);
        assertNotNull(savedSession.getId());
        // check that the presenters got their ids and got updated with the session id
        for (SessionPresenter sessionPresenter : savedSession.getPresenters()) {
            String presenterName = sessionPresenter.getPresenter().getName();
            assertNotNull("Session presenter should have generated an id for presenter " + presenterName, sessionPresenter.getId());
            assertEquals("Session id was not linked for presenter " + presenterName, savedSession.getId(), sessionPresenter.getSessionId());
        }

        // now let's try adding another SessionPresenter, when we link this one, it should get linked in a similar
        // fashion as the others, but also should get it's session id set properly since our parent session has an id
        SessionPresenter sessionPresenter4 = new SessionPresenter();
        sessionPresenter4.setPresenterId(presenter1.getId());
        savedSession.getPresenters().add(sessionPresenter4);

        referenceLinker.linkChanges(savedSession, Sets.newHashSet("presenters[3].presenterId"));

        // verify everything was linked properly
        assertEquals(presenter1.getId(), sessionPresenter4.getPresenterId());
        assertNotNull(sessionPresenter4.getPresenter());
        assertEquals(presenter1, sessionPresenter4.getPresenter());
        assertEquals(savedSession, sessionPresenter4.getSession());
        assertEquals(savedSession.getId(), sessionPresenter4.getSessionId());

        ConferenceSession savedSession2 = getDataObjectService().save(savedSession);
        assertEquals(4, savedSession2.getPresenters().size());
    }

    /**
     * Tests that linking is working for bi-directional collections
     */
    @Test
    public void testBiDirectionalCollectionLinking() {
        // we have a bidirectional collection relationship between ConferenceSession and SessionCoordinator so let's try that out
        SessionCoordinator sc = new SessionCoordinator();
        List<ConferenceSession> sessions = new ArrayList<ConferenceSession>();
        ConferenceSession session = new ConferenceSession();
        sessions.add(session);
        sc.setAltCoordinatedSessions1(sessions);

        // before we link, we should be missing the relationship from ConferenceSession back to it's SessionCoordinator
        assertNull(session.getAltCoordinator1());

        // first let's link without anything in our change list, it should not perform the linking
        referenceLinker.linkChanges(sc, Sets.<String>newHashSet());

        // now let's link with the changes, should work if we just specify the "accounts" path
        referenceLinker.linkChanges(sc, Sets.newHashSet("altCoordinatedSessions1"));

        // assert that our account now points back to it's account manager
        assertEquals(sc, session.getAltCoordinator1());

        // now let's try linking using an indexed-based path of "altCoordinatedSessions1[0]", we will (of course) need to reset our
        // session object first
        session.setAltCoordinator1(null);

        // first, if we link with an invalid index, it should do nothing
        referenceLinker.linkChanges(sc, Sets.newHashSet("altCoordinatedSessions1[1]"));
        assertNull(session.getAltCoordinator1());

        referenceLinker.linkChanges(sc, Sets.newHashSet("altCoordinatedSessions1[0]"));
        assertEquals(sc, session.getAltCoordinator1());

    }

    private DataObjectService getDataObjectService() {
        return KradDataServiceLocator.getDataObjectService();
    }

}
