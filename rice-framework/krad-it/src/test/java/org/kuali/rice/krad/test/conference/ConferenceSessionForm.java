/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.test.conference;

import org.kuali.rice.krad.data.util.Link;
import org.kuali.rice.krad.web.bind.ChangeTracking;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test form class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@ChangeTracking
@Link(path = {"conferenceSessionList", "conferenceSessionMap"})
public class ConferenceSessionForm extends UifFormBase {
    private static final long serialVersionUID = 2178491838787456800L;

    List<ConferenceSession> conferenceSessionList;
    Map<String, ConferenceSession> conferenceSessionMap;

    public ConferenceSessionForm() {
        conferenceSessionList = new ArrayList<ConferenceSession>();
        conferenceSessionMap = new HashMap<String, ConferenceSession>();
    }

    public List<ConferenceSession> getConferenceSessionList() {
        return conferenceSessionList;
    }

    public void setConferenceSessionList(List<ConferenceSession> conferenceSessionList) {
        this.conferenceSessionList = conferenceSessionList;
    }

    public Map<String, ConferenceSession> getConferenceSessionMap() {
        return conferenceSessionMap;
    }

    public void setConferenceSessionMap(Map<String, ConferenceSession> conferenceSessionMap) {
        this.conferenceSessionMap = conferenceSessionMap;
    }
}
