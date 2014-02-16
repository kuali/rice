package org.kuali.rice.krad.labs;

import org.kuali.rice.krad.web.form.UifFormBase;
//import org.kuali.student.enrollment.registration.client.service.dto.CourseSearchResult;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: swedev
 * Date: 11/21/13
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class KitchenSinkPerformanceForm extends UifFormBase {
    private String inputOne;
    private List<CourseSearchResult> perfCollection;

    public String getInputOne() {
        return inputOne;
    }

    public void setInputOne(String inputOne) {
        this.inputOne = inputOne;
    }

    public List<CourseSearchResult> getPerfCollection() {
        return perfCollection;
    }

    public void setPerfCollection(List<CourseSearchResult> perfCollection) {
        this.perfCollection = perfCollection;
    }
}
