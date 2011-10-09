package edu.sampleu.travel.bo;

import java.io.Serializable;

/**
 * Wrapper class for exposing remote attribute definitions
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SampleAppAttributes implements Serializable {
    private static final long serialVersionUID = -5536424163658038143L;

    private String number;
    private String foId;

    public SampleAppAttributes() {

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFoId() {
        return foId;
    }

    public void setFoId(String foId) {
        this.foId = foId;
    }
}
