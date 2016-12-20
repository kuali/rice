package org.kuali.rice.krad.test.document.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * DataObject only mapped to JPA (non-legacy)
 */
@Entity
@Table(name="KR_KIM_TEST_BO") // this is a convenient test table
public class JPADataObject {
    @Id
    @Column(name="PK")
    private String pk;
}
