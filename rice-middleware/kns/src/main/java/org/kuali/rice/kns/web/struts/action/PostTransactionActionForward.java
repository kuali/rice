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
package org.kuali.rice.kns.web.struts.action;

import org.apache.struts.action.ActionForward;


public class PostTransactionActionForward extends ActionForward {
    
    protected ActionForwardCallback callback;
    protected ActionForward realForward;

    public PostTransactionActionForward() {
    }

    public PostTransactionActionForward(final ActionForward copyMe, final ActionForwardCallback callback) {
        super(copyMe);
        setRealForward(copyMe);
        setCallback(callback);
    }

    public PostTransactionActionForward(final String path, final ActionForwardCallback callback) {
        super(path);
        setCallback(callback);
    }

    public PostTransactionActionForward(final String path, final ActionForwardCallback callback, boolean redirect) {
        super(path, redirect);
        setCallback(callback);
    }

    public PostTransactionActionForward(final String name, final ActionForwardCallback callback, final String path, boolean redirect) {
        super(name, path, redirect);
        setCallback(callback);
    }
    
    public void setCallback(final ActionForwardCallback callback) {
        this.callback = callback;
    }
    public ActionForwardCallback getCallback() {
        return callback;
    }

    public ActionForward getRealForward() {
        return this.realForward;
    }

    public void setRealForward(final ActionForward realForward) {
        this.realForward = realForward;
    }
}
