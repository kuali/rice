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

package org.kuali.rice.krad.test.conference;

import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="KRTST_CONF_SESS_PRES_T")
public class SessionPresenter {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "KRTST_CONF_SESS_PRES_S")
    @PortableSequenceGenerator(name = "KRTST_CONF_SESS_PRES_S")
    private String id;

    @Column(name = "SESS_ID", updatable = false, insertable = false)
    private String sessionId;

    @Column(name = "PRES_ID")
    private String presenterId;

    @Column(name = "PRIMARY_IND")
    private Boolean primary = Boolean.FALSE;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SESS_ID")
    private ConferenceSession session;

    @ManyToOne
    @JoinColumn(name = "PRES_ID", referencedColumnName = "ID", updatable = false, insertable = false)
    private PresenterInfo presenter;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPresenterId() {
        return presenterId;
    }

    public void setPresenterId(String presenterId) {
        this.presenterId = presenterId;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public ConferenceSession getSession() {
        return session;
    }

    public void setSession(ConferenceSession session) {
        this.session = session;
    }

    public PresenterInfo getPresenter() {
        return presenter;
    }

    public void setPresenter(PresenterInfo presenter) {
        this.presenter = presenter;
    }
}
