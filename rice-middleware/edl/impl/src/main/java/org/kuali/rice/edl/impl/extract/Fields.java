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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.kuali.rice.edl.framework.extract.FieldDTO;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import java.io.Serializable;

/**
 *
 *
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name="KREW_EDL_FLD_DMP_T")
public class Fields implements Serializable {

    //	private static final long serialVersionUID = -6136544551121011531L;

    @Id
    @GeneratedValue(generator="KREW_EDL_FLD_DMP_S")
    @PortableSequenceGenerator(name = "KREW_EDL_FLD_DMP_S")
    @Column(name="EDL_FIELD_DMP_ID")
    private Long fieldId;

    @Column(name="DOC_HDR_ID")
    private String docId;

    @Column(name="FLD_NM")
    private String fieldName;

    @Column(name="FLD_VAL")
    private String fieldValue;

    @Version
    @Column(name="VER_NBR")
    private Integer lockVerNbr;

    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
    @JoinColumn(name="DOC_HDR_ID", insertable=false, updatable=false)
    private Dump dump;

    /**
     * Returns the field id.
     * @return the field id
     */
    public Long getFieldId() {
        return fieldId;
    }

    /**
     *
     * @see #getFieldId()
     */
    public void setFieldId(Long fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Returns the doc id.
     * @return the doc id
     */
    public String getDocId() {
        return docId;
    }

    /**
     *
     * @see #getDocId()
     */
    public void setDocId(final String docId) {
        this.docId = docId;
    }

    /**
     * Returns the field value.
     * @return the field value
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     *
     * @see #getFieldValue()
     */
    public void setFieldValue(final String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * Returns the field name.
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     *
     * @see #getFieldName()
     */
    public void setFieldName(final String filedName) {
        this.fieldName = filedName;
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
     * Returns a {@link Dump}
     * @return a {@link Dump}
     */
    public Dump getDump() {
        return dump;
    }

    /**
     *
     * @see #getDump()
     */
    public void setDump(final Dump dump) {
        this.dump = dump;
    }

    /**
     * Converts a {@link Fields} to a {@link FieldDTO}.
     * @param field the {@link Fields} to convert.
     * @return a {@link Fields}
     */
    public static FieldDTO to(Fields field) {
        if (field == null) {
            return null;
        }
        FieldDTO fieldDTO = new FieldDTO();
        fieldDTO.setDocId(field.getDocId());
        fieldDTO.setFieldName(field.getFieldName());
        fieldDTO.setFieldValue(field.getFieldValue());
        fieldDTO.setLockVerNbr(field.getLockVerNbr());
        return fieldDTO;
    }

    /**
     * Converts a {@link FieldDTO} to a {@link Fields}
     * @param fieldDTO the {@link FieldDTO} to convert.
     * @param dump a {@link Dump}
     * @return a {@link Fields}
     */
    public static Fields from(FieldDTO fieldDTO, Dump dump) {
        if (fieldDTO == null) {
            return null;
        }
        Fields fields = new Fields();
        fields.setDump(dump);
        fields.setDocId(fieldDTO.getDocId());
        fields.setFieldName(fieldDTO.getFiledName());
        fields.setFieldValue(fieldDTO.getFieldValue());
        fields.setLockVerNbr(fieldDTO.getLockVerNbr());
        return fields;
    }
}

