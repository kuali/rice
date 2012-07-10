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
package org.kuali.rice.krad.uif.view;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.component.RequestParameter;

/**
 * View type for Maintenance documents
 *
 * <p>
 * Supports primary display for a new maintenance record, in which case the
 * fields are display for populating the new record, and an edit maintenance
 * record, which is a comparison view with the old record read-only on the left
 * side and the new record (changed record) on the right side
 * </p>
 *
 * <p>
 * The <code>MaintenanceView</code> provides the interface for the maintenance
 * framework. It works with the <code>Maintainable</code> service and
 * maintenance controller.
 * </p>
 *
 * <p>
 * Maintenance views are primarily configured by the object class they are
 * associated with. This provides the default dictionary information for the
 * fields. If more than one maintenance view is needed for the same object
 * class, the view name can be used to further identify an unique view
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceView extends DocumentView {
    private static final long serialVersionUID = -3382802967703882341L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceView.class);

    private Class<?> dataObjectClassName;

    private String oldObjectBindingPath;

    @RequestParameter
    private String maintenanceAction;

    public MaintenanceView() {
        super();

        setViewTypeName(ViewType.MAINTENANCE);
    }

    /**
     * The following initialization is performed:
     *
     * <ul>
     * <li>Set the abstractTypeClasses map for the maintenance object path</li>
     * </ul>
     *
     * @see org.kuali.rice.krad.uif.container.ContainerBase#performInitialization(org.kuali.rice.krad.uif.view.View, java.lang.Object)
     */
    @Override
    public void performInitialization(View view, Object model) {
        super.performInitialization(view, model);

        getAbstractTypeClasses().put(getDefaultBindingObjectPath(), getDataObjectClassName());
        getAbstractTypeClasses().put(getOldObjectBindingPath(), getDataObjectClassName());
    }

    /**
     * Overrides to retrieve the a {@link MaintenanceDocumentEntry} based on the configured data object class
     *
     * @return MaintenanceDocumentEntry document entry (exception thrown if not found)
     */
    @Override
    protected MaintenanceDocumentEntry getDocumentEntryForView() {
        MaintenanceDocumentEntry documentEntry = null;
        String docTypeName = KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentTypeName(
                getDataObjectClassName());
        if (StringUtils.isNotBlank(docTypeName)) {
            documentEntry = KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentEntry(
                    docTypeName);
        }

        if (documentEntry == null) {
            throw new RuntimeException(
                    "Unable to find maintenance document entry for data object class: " + getDataObjectClassName()
                            .getName());
        }

        return documentEntry;
    }

    /**
     * Class name for the object the maintenance document applies to
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
     * Gives the binding path to the old object (record being edited) to display
     * for comparison
     *
     * @return String old object binding path
     */
    public String getOldObjectBindingPath() {
        return this.oldObjectBindingPath;
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
     * Indicates what maintenance action (new, edit, copy) was
     * requested
     *
     * @return String maintenance action
     */
    public String getMaintenanceAction() {
        return maintenanceAction;
    }

    /**
     * Setter for the maintenance action
     *
     * @param maintenanceAction
     */
    public void setMaintenanceAction(String maintenanceAction) {
        this.maintenanceAction = maintenanceAction;
    }

}
