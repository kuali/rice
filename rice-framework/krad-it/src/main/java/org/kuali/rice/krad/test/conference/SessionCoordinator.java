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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KRTST_CONF_COORD_T")
public class SessionCoordinator {

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "KRTST_CONF_COORD_S")
    @PortableSequenceGenerator(name = "KRTST_CONF_COORD_S")
    private Long id;

    @Column(name = "NM")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "coordinator")
    private List<ConferenceSession> coordinatedSessions = new ArrayList<ConferenceSession>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "altCoordinator1")
    private List<ConferenceSession> altCoordinatedSessions1 = new ArrayList<ConferenceSession>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "altCoordinator2")
    private List<ConferenceSession> altCoordinatedSessions2 = new ArrayList<ConferenceSession>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ConferenceSession> getCoordinatedSessions() {
        return coordinatedSessions;
    }

    public void setCoordinatedSessions(List<ConferenceSession> coordinatedSessions) {
        this.coordinatedSessions = coordinatedSessions;
    }

    public List<ConferenceSession> getAltCoordinatedSessions1() {
        return altCoordinatedSessions1;
    }

    public void setAltCoordinatedSessions1(List<ConferenceSession> altCoordinatedSessions1) {
        this.altCoordinatedSessions1 = altCoordinatedSessions1;
    }

    public List<ConferenceSession> getAltCoordinatedSessions2() {
        return altCoordinatedSessions2;
    }

    public void setAltCoordinatedSessions2(List<ConferenceSession> altCoordinatedSessions2) {
        this.altCoordinatedSessions2 = altCoordinatedSessions2;
    }

}
