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

import org.junit.Test;
import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.dao.RiceKNSDefaultUserDAOImpl;
import org.kuali.rice.kns.dao.UniversalUserDao;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.test.TestBase;

/**
 * This class is used to test the {@link NoteService} implementation 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class NoteServiceTest extends TestBase {

    /**
     * This method tests saving notes when using the {@link RiceKNSDefaultUserDAOImpl} as the implementation of {@link UniversalUserDao}
     * 
     * @throws Exception
     */
    @Test public void testNoteSave_LargeUniversalUserId() throws Exception {
        Note note = new Note();
        note.setAuthorUniversalIdentifier("superLongNameUsersFromWorkflow");
        note.setNotePostedTimestamp(KNSServiceLocator.getDateTimeService().getCurrentTimestamp());
        note.setNoteText("i like notes");
        note.setRemoteObjectIdentifier("1209348109834u");
        note.setNoteTypeCode(KNSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
        try {
            KNSServiceLocator.getNoteService().save(note);
        } catch (Exception e) {
            fail("Saving a note should not fail");
        }
    }
    
}
