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
package org.kuali.rice.krad.labs;

import org.kuali.rice.krad.uif.util.SessionTransient;
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

    protected List<CourseSearchResult> perfCollection;

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
