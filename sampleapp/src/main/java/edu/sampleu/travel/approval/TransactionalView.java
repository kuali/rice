/**
 * Copyright 2005-2012 The Kuali Foundation
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
package edu.sampleu.travel.approval;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.view.DocumentView;
import org.kuali.rice.krad.uif.view.View;

@BeanTag(name="transactionView")
public class TransactionalView extends DocumentView {
    private static final long serialVersionUID = -3382812867703882341L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TransactionalView.class);

    private Class<?> dataObjectClassName;

    private String oldObjectBindingPath;

    public TransactionalView() {
        super();
        setViewTypeName(UifConstants.ViewType.TRANSACTION);
    }

    /**
     * Class name for the object the transactional document applies to
     *
     * <p>
     * The object class name is used to pick up a dictionary entry which will
     * feed the attribute field definitions and other configuration. In addition
     * it is used to configure the <code>Maintainable</code> which will carry
     * out the maintenance action
     * </p>
     *
     * @return Class<?> maintenance object class
     */
    @BeanTagAttribute(name="dataObjectClassName")
    public Class<?> getDataObjectClassName() {
        return this.dataObjectClassName;
    }

    /**
     * Setter for the object class name
     *
     * @param dataObjectClassName
     */
    public void setDataObjectClassName(Class<?> dataObjectClassName) {
        this.dataObjectClassName = dataObjectClassName;
    }

    /**
     * Setter for the old object binding path
     *
     * @param oldObjectBindingPath
     */
    public void setOldObjectBindingPath(String oldObjectBindingPath) {
        this.oldObjectBindingPath = oldObjectBindingPath;
    }

    /**
     * Gives the binding path to the old object (record being edited) to display
     * for comparison
     *
     * @return String old object binding path
     */
    @BeanTagAttribute(name="oldObjectBindingPath")
    public String getOldObjectBindingPath() {
        return this.oldObjectBindingPath;
    }
}
