package org.kuali.rice.krad.test.document;

/**
 * A class which is only mapped in OJB, and not JPA.
 */
public class OjbOnly {

    private String primaryKey;
    private String principalId;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }
}
