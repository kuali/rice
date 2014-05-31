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
package org.kuali.rice.krad.uif.mock;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Form class for {@link org.kuali.rice.krad.uif.mock.MockView} instances that holds data in generic maps.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DynaForm extends UifFormBase {
    private static final long serialVersionUID = -2112462466031059707L;

    private Map<String, Object> data;
    private Map<String, Boolean> booleanData;

    @SessionTransient
    private boolean initialGetRequest;

    /**
     * Default constructor.
     */
    public DynaForm() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postBind(HttpServletRequest request) {
        // form key is assigned in super so need to do this check before it executes
        if (StringUtils.isBlank(getFormKey()) && request.getMethod().equals(RequestMethod.GET.name())) {
            initialGetRequest = true;
        }

        super.postBind(request);
    }

    /**
     * Map containing non-boolean data for the view.
     *
     * @return map where key is property name and value is the property value
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @see DynaForm#getData()
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    /**
     * Map containing boolean data for the view.
     *
     * @return map where key is property name and value is the property value
     */
    public Map<String, Boolean> getBooleanData() {
        return booleanData;
    }

    /**
     * @see DynaForm#getBooleanData()
     */
    public void setBooleanData(Map<String, Boolean> booleanData) {
        this.booleanData = booleanData;
    }

    /**
     * Indicates whether the request is the initial get request for the view.
     *
     * @return boolean true if this is the initial request, false if not
     */
    public boolean isInitialGetRequest() {
        return initialGetRequest;
    }
}
