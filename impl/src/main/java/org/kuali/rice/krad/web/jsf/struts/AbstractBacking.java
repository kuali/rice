/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.web.jsf.struts;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.kns.web.struts.form.KualiForm;


/**
 * <p>Abstract base class for backing beans.</p>
 */
public abstract class AbstractBacking {

    /**
     * <p>Return the context relative path for the specified action.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param action Name of the requested action
     */
    protected StringBuffer action(FacesContext context, String action) {
        // FIXME - assumes extension mapping for Struts
        StringBuffer sb = new StringBuffer(action);
        sb.append(".do");
        
        return (sb);
    }


    /**
     * <p>Forward to the specified URL and mark this response as having
     * been completed.</p>
     *
     * @param context <code>FacesContext</code> for the current request
     * @param url Context-relative URL to forward to
     *
     * @exception FacesException if any error occurs
     */
    protected void forward(FacesContext context, String url) {
        try {
        	((HttpServletRequest) context.getExternalContext().getRequest()).setAttribute("FacesRequest", "True");
            context.getExternalContext().dispatch(url);
        } catch (IOException e) {
            throw new FacesException(e);
        } finally {
            context.responseComplete();
        }
    }
    
    protected KualiForm getActionForm(FacesContext context) {
    	return (KualiForm) context.getExternalContext().getRequestMap().get("KualiForm");
    }

}
