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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="KRTST_CONF_SESS_T")
public class ConferenceSession implements Serializable {

    private static final long serialVersionUID = -7022847412734257626L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "KRTST_CONF_SESS_S")
    @PortableSequenceGenerator(name = "KRTST_CONF_SESS_S")
    private String id;

    @Column(name = "TITLE")
    private String sessionTitle;

    @Column(name = "SESS_DT")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "START_TIME")
    private String startTime;

    @Column(name = "END_TIME")
    private String endTime;

    @Column(name = "SESS_TYPE_CODE")
    private String sessionTypeCode;

    @Column(name = "DESCRIPTION")
    private String description;

    /**
     * A bi-directional ManyToOne (mapped with a OneToMany on the other side)
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "COORD_ID")
    private SessionCoordinator coordinator;

    /**
     * A bi-directional ManyToOne with a foreign key field and non-updatable reference.
     */
    @Column(name = "ALT_COORD1_ID")
    private Long altCoordinator1Id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ALT_COORD1_ID", insertable = false, updatable = false)
    private SessionCoordinator altCoordinator1;

    /**
     * A bi-directional ManyToOne with a foreign key field and non-updatable field.
     */
    @Column(name = "ALT_COORD2_ID", insertable = false, updatable = false)
    private Long altCoordinator2Id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ALT_COORD2_ID")
    private SessionCoordinator altCoordinator2;


    /**
     * A ManyToOne with no foreign key field.
     */
    @ManyToOne
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    /**
     * A ManyToOne with a foreign key field and non updatable reference
     */
    @Column(name = "ALT_ROOM1_ID")
    private String altRoom1Id;
    @ManyToOne
    @JoinColumn(name = "ALT_ROOM1_ID", insertable = false, updatable = false)
    private Room altRoom1;

    /**
     * A ManyToOne with a foreign key field and non updatable field
     */
    @Column(name = "ALT_ROOM2_ID", insertable = false, updatable = false)
    private String altRoom2Id;
    @ManyToOne
    @JoinColumn(name = "ALT_ROOM2_ID")
    private Room altRoom2;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "session")
    private List<SessionPresenter> presenters = new ArrayList<SessionPresenter>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSessionTypeCode() {
        return sessionTypeCode;
    }

    public void setSessionTypeCode(String sessionTypeCode) {
        this.sessionTypeCode = sessionTypeCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SessionCoordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(SessionCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Long getAltCoordinator1Id() {
        return altCoordinator1Id;
    }

    public void setAltCoordinator1Id(Long altCoordinator1Id) {
        this.altCoordinator1Id = altCoordinator1Id;
    }

    public SessionCoordinator getAltCoordinator1() {
        return altCoordinator1;
    }

    public void setAltCoordinator1(SessionCoordinator altCoordinator1) {
        this.altCoordinator1 = altCoordinator1;
    }

    public Long getAltCoordinator2Id() {
        return altCoordinator2Id;
    }

    public void setAltCoordinator2Id(Long altCoordinator2Id) {
        this.altCoordinator2Id = altCoordinator2Id;
    }

    public SessionCoordinator getAltCoordinator2() {
        return altCoordinator2;
    }

    public void setAltCoordinator2(SessionCoordinator altCoordinator2) {
        this.altCoordinator2 = altCoordinator2;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getAltRoom1Id() {
        return altRoom1Id;
    }

    public void setAltRoom1Id(String altRoom1Id) {
        this.altRoom1Id = altRoom1Id;
    }

    public Room getAltRoom1() {
        return altRoom1;
    }

    public void setAltRoom1(Room altRoom1) {
        this.altRoom1 = altRoom1;
    }

    public String getAltRoom2Id() {
        return altRoom2Id;
    }

    public void setAltRoom2Id(String altRoom2Id) {
        this.altRoom2Id = altRoom2Id;
    }

    public Room getAltRoom2() {
        return altRoom2;
    }

    public void setAltRoom2(Room altRoom2) {
        this.altRoom2 = altRoom2;
    }

    public List<SessionPresenter> getPresenters() {
        return presenters;
    }

    public void setPresenters(List<SessionPresenter> presenters) {
        this.presenters = presenters;
    }

}