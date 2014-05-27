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
package org.kuali.rice.krad.bo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.LegacyDataAppAdapter;


/**
 * Adapter class to provide some of the parent methods expected of persistable business objects
 * This is a temporary class to use in place of PBOB (moved to the KNS module) to facilitate
 * code-level compatibility with existing business objects and documents.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PersistableBusinessObjectBaseAdapter extends DataObjectBase {
    private static final long serialVersionUID = 1L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistableBusinessObjectBaseAdapter.class);

    @Transient protected boolean newCollectionRecord;
    @Transient protected Object extension;

    /**
     * getService Refreshes the reference objects from the primitive values.
     *
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    public void refresh() {
        // do nothing
    }

    public void refreshNonUpdateableReferences() {
        getLegacyDataAdapter().refreshAllNonUpdatingReferences(this);
    }

    public void refreshReferenceObject(String referenceObjectName) {
        getLegacyDataAdapter().refreshReferenceObject(this, referenceObjectName);
    }

    /**
     * Returns the legacy data adapter for handling legacy KNS and KRAD data and metadata.
     *
     * @return the legacy data adapter
     * @deprecated application code should never use this! Always use KRAD code directly.
     */
    @Deprecated
    protected LegacyDataAppAdapter getLegacyDataAdapter() {
        return KRADServiceLocator.getLegacyDataAdapter();
    }

    public List buildListOfDeletionAwareLists() {
        return new ArrayList();
    }

    public void linkEditableUserFields() {
        // do nothing
    }

    public Object getExtension() {
        if ( extension == null
                && getLegacyDataAdapter().isPersistable(this.getClass())) {
            try {
                extension = getLegacyDataAdapter().getExtension(this.getClass());
            } catch ( Exception ex ) {
                LOG.error( "unable to create extension object", ex );
            }
        }
        return extension;
    }

    public void setExtension(Object extension) {
        this.extension = extension;
    }

    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    public void setNewCollectionRecord(boolean isNewCollectionRecord) {
        this.newCollectionRecord = isNewCollectionRecord;
    }
}
