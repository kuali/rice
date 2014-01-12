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
package org.kuali.rice.edl.impl.extract;

import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.edl.framework.extract.DumpDTO;
import org.kuali.rice.edl.framework.extract.FieldDTO;
import org.kuali.rice.kew.api.KewApiConstants;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KREW_EDL_DMP_T")
public class Dump implements Serializable {

    //	private static final long serialVersionUID = -6136544551121011531L;

    @Id
    @Column(name="DOC_HDR_ID", nullable = false)
    private String docId;

    @Column(name="DOC_TYP_NM", nullable = false)
    private String docTypeName;

    @Column(name="DOC_HDR_STAT_CD", nullable = false)
    private String docRouteStatusCode;

    @Column(name="DOC_HDR_MDFN_DT", nullable = false)
    private Timestamp docModificationDate;

    @Column(name="DOC_HDR_CRTE_DT", nullable = false)
    private Timestamp docCreationDate;

    @Column(name="DOC_HDR_TTL")
    private String docDescription;

    @Column(name="DOC_HDR_INITR_PRNCPL_ID", nullable = false)
    private String docInitiatorId;

    @Column(name="CRNT_NODE_NM", nullable = false)
    private String docCurrentNodeName;

    @Version
    @Column(name="VER_NBR", nullable = false)
    private Integer lockVerNbr;

    @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.ALL},mappedBy="dump")
    private List<Fields> fields = new ArrayList<Fields>();

    /**
     * Returns the document creation timestamp.
     * @return the doucment creation timestamp
     */
    public Timestamp getDocCreationDate() {
        return docCreationDate;
    }

    /**
     *
     * @see #getDocCreationDate()
     */
    public void setDocCreationDate(final Timestamp docCreationDate) {
        this.docCreationDate = docCreationDate;
    }

    /**
     * Rreturns document current node nam.e
     * @return document current node name
     */
    public String getDocCurrentNodeName() {
        return docCurrentNodeName;
    }

    /**
     *
     * @see #getDocCurrentNodeName()
     */
    public void setDocCurrentNodeName(final String docCurrentNodeName) {
        this.docCurrentNodeName = docCurrentNodeName;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDocDescription() {
        return docDescription;
    }

    /**
     *
     * @see #getDocDescription()
     */
    public void setDocDescription(final String docDescription) {
        this.docDescription = docDescription;
    }

    /**
     * Returns the document id.
     * @return the document id
     */
    public String getDocId() {
        return docId;
    }

    /**
     * Returns document initiator id.
     * @return document initiator id
     */
    public String getDocInitiatorId() {
        return docInitiatorId;
    }

    /**
     *
     * @see #getDocInitiatorId()
     */
    public void setDocInitiatorId(final String docInitiatorId) {
        this.docInitiatorId = docInitiatorId;
    }

    /**
     * Returns document modification date
     * @return the document modification date
     */
    public Timestamp getDocModificationDate() {
        return docModificationDate;
    }

    /**
     *
     * @see #getDocModificationDate()
     */
    public void setDocModificationDate(final Timestamp docModificationDate) {
        this.docModificationDate = docModificationDate;
    }

    /**
     * Returns document route status code.
     * @return document route status code
     */
    public String getDocRouteStatusCode() {
        return docRouteStatusCode;
    }

    /**
     *
     * @see #getDocRouteStatusCode()
     */
    public void setDocRouteStatusCode(final String docRouteStatusCode) {
        this.docRouteStatusCode = docRouteStatusCode;
    }

    /**
     * Returns the document type name.
     * @return the document type name
     */
    public String getDocTypeName() {
        return docTypeName;
    }

    /**
     *
     * @see #getDocTypeName()
     */
    public void setDocTypeName(final String docTypeName) {
        this.docTypeName = docTypeName;
    }

    /**
     * Returns the lock version number.
     * @return the lock version number
     */
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    /**
     *
     * @see #getLockVerNbr()
     */
    public void setLockVerNbr(final Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    /**
     * Returns the creation timestamp specially formatted.
     * @return the creation timestamp, specially formatted.
     */
    public String getFormattedCreateDateTime() {
        long time = getDocCreationDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat(KewApiConstants.TIMESTAMP_DATE_FORMAT_PATTERN2);
        return dateFormat.format(date);
    }

    /**
     * Returns the date portion of the creation timestamp.
     * @return the date portion of the creation timestamp.
     */
    public String getFormattedCreateDate() {
        long time = getDocCreationDate().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat dateFormat = RiceConstants.getDefaultDateFormat();
        return dateFormat.format(date);
    }

    /**
     *
     * @see #getDocId()
     */
    public void setDocId(final String docId) {
        this.docId = docId;
    }

    /**
     * Returns the {@link Fields}
     * @return the {@link Fields}
     */
    public List<Fields> getFields() {
        return fields;
    }

    /**
     *
     * @see #getFields()
     */
    public void setFields(final List<Fields> fields) {
        this.fields = fields;
    }

    /**
     * Converts a {@link Dump} to a {@link DumpDTO}
     * @param dump the {@link Dump} to convert.
     * @return a {@link DumpDTO}
     */
    public static DumpDTO to(Dump dump) {
        if (dump == null) {
            return null;
        }
        DumpDTO dumpDTO = new DumpDTO();
        dumpDTO.setDocCreationDate(dump.getDocCreationDate());
        dumpDTO.setDocCurrentNodeName(dump.getDocCurrentNodeName());
        dumpDTO.setDocDescription(dump.getDocDescription());
        dumpDTO.setDocId(dump.getDocId());
        dumpDTO.setDocInitiatorId(dump.getDocInitiatorId());
        dumpDTO.setDocModificationDate(dump.getDocModificationDate());
        dumpDTO.setDocRouteStatusCode(dump.getDocRouteStatusCode());
        dumpDTO.setDocTypeName(dump.getDocTypeName());
        dumpDTO.setLockVerNbr(dump.getLockVerNbr());
        List<FieldDTO> fields = new ArrayList<FieldDTO>();
        for (Fields field : dump.getFields()) {
            fields.add(Fields.to(field));
        }
        dumpDTO.setFields(fields);
        return dumpDTO;
    }

    /**
     * Converts a {@link DumpDTO} to a {@link Dump}
     * @param dumpDTO the {@link DumpDTO} to convert
     * @return a {@link Dump}
     */
    public static Dump from(DumpDTO dumpDTO) {
        if (dumpDTO == null) {
            return null;
        }
        Dump dump = new Dump();
        dump.setDocCreationDate(dumpDTO.getDocCreationDate());
        dump.setDocCurrentNodeName(dumpDTO.getDocCurrentNodeName());
        dump.setDocDescription(dumpDTO.getDocDescription());
        dump.setDocId(dumpDTO.getDocId());
        dump.setDocInitiatorId(dumpDTO.getDocInitiatorId());
        dump.setDocModificationDate(dumpDTO.getDocModificationDate());
        dump.setDocRouteStatusCode(dumpDTO.getDocRouteStatusCode());
        dump.setDocTypeName(dumpDTO.getDocTypeName());
        dump.setLockVerNbr(dumpDTO.getLockVerNbr());
        List<Fields> fields = new ArrayList<Fields>();
        for (FieldDTO fieldDTO : dumpDTO.getFields()) {
            fields.add(Fields.from(fieldDTO, dump));
        }
        dump.setFields(fields);
        return dump;
    }
}

