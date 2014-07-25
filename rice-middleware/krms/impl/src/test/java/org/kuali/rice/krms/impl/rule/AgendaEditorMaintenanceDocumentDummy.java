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
package org.kuali.rice.krms.impl.rule;

import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteWorkgroup;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectExtension;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.document.authorization.PessimisticLock;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.util.NoteType;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;

import java.util.Collection;
import java.util.List;

public class AgendaEditorMaintenanceDocumentDummy implements MaintenanceDocument {
    protected Maintainable oldMaintainableObject;
    protected Maintainable newMaintainableObject;

    public String getXmlDocumentContents() {
        return null;
    }

    public Maintainable getNewMaintainableObject() {
        return this.newMaintainableObject;
    }

    public Maintainable getOldMaintainableObject() {
        return this.oldMaintainableObject;
    }

    public void setXmlDocumentContents(String documentContents) {
    }

    public void setNewMaintainableObject(Maintainable newMaintainableObject) {
        this.newMaintainableObject = newMaintainableObject;
    }

    public void setOldMaintainableObject(Maintainable oldMaintainableObject) {
        this.oldMaintainableObject = oldMaintainableObject;
    }

    public Object getDocumentDataObject() {
        return null;
    }

    public void populateXmlDocumentContentsFromMaintainables() {
    }

    public void populateMaintainablesFromXmlDocumentContents() {
    }

    public boolean isOldDataObjectInDocument() {
        return false;
    }

    public boolean isNew() {
        return false;
    }

    public boolean isEdit() {
        return false;
    }

    public boolean isNewWithExisting() {
        return false;
    }

    public boolean isFieldsClearedOnCopy() {
        return false;
    }

    public void setFieldsClearedOnCopy(boolean keysClearedOnCopy) {
    }

    public boolean isDisplayTopicFieldInNotes() {
        return false;
    }

    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes) {
    }

    public DocumentHeader getDocumentHeader() {
        return null;
    }

    public void setDocumentHeader(DocumentHeader documentHeader) {
    }

    public String getDocumentNumber() {
        return null;
    }

    public void setDocumentNumber(String documentHeaderId) {
    }

    public void populateDocumentForRouting() {
    }

    public String serializeDocumentToXml() {
        return null;
    }

    public String getXmlForRouteReport() {
        return null;
    }

    public void doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) {
    }

    public void doActionTaken(ActionTakenEvent event) {
    }

    public void afterActionTaken(ActionType performed, ActionTakenEvent event) {
    }

    public void afterWorkflowEngineProcess(boolean successfullyProcessed) {
    }

    public void beforeWorkflowEngineProcess() {
    }

    public List<String> getWorkflowEngineDocumentIdsToLock() {
        return null;
    }

    public String getDocumentTitle() {
        return null;
    }

    public List<AdHocRoutePerson> getAdHocRoutePersons() {
        return null;
    }

    public List<AdHocRouteWorkgroup> getAdHocRouteWorkgroups() {
        return null;
    }

    public void setAdHocRoutePersons(List<AdHocRoutePerson> adHocRoutePersons) {
    }

    public void setAdHocRouteWorkgroups(List<AdHocRouteWorkgroup> adHocRouteWorkgroups) {
    }

    public void prepareForSave() {
    }

    public void validateBusinessRules(DocumentEvent event) {
    }

    public void prepareForSave(DocumentEvent event) {
    }

    public void postProcessSave(DocumentEvent event) {
    }

    public void processAfterRetrieve() {
    }

    public boolean getAllowsCopy() {
        return false;
    }

    public List<DocumentEvent> generateSaveEvents() {
        return null;
    }

    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
    }

    public NoteType getNoteType() {
        return null;
    }

    public PersistableBusinessObject getNoteTarget() {
        return null;
    }

    public void addNote(Note note) {
    }

    public List<Note> getNotes() {
        return null;
    }

    public void setNotes(List<Note> notes) {
    }

    public Note getNote(int index) {
        return null;
    }

    public boolean removeNote(Note note) {
        return false;
    }

    public List<ActionRequest> getActionRequests() {
        return null;
    }

    public String getSuperUserAnnotation() {
        return null;
    }

    public void setSuperUserAnnotation(String superUserAnnotation) {
    }

    public List<PessimisticLock> getPessimisticLocks() {
        return null;
    }

    public void refreshPessimisticLocks() {
    }

    public void addPessimisticLock(PessimisticLock lock) {
    }

    public List<String> getLockClearingMethodNames() {
        return null;
    }

    public List<String> getLockClearningMethodNames() {
        return null;
    }

    public String getBasePathToDocumentDuringSerialization() {
        return null;
    }

    public PropertySerializabilityEvaluator getDocumentPropertySerizabilityEvaluator() {
        return null;
    }

    public Object wrapDocumentWithMetadataForXmlSerialization() {
        return null;
    }

    public boolean useCustomLockDescriptors() {
        return false;
    }

    public String getCustomLockDescriptor(Person user) {
        return null;
    }

    public void setVersionNumber(Long versionNumber) {
    }

    public void setObjectId(String objectId) {
    }

    public PersistableBusinessObjectExtension getExtension() {
        return null;
    }

    public void setExtension(PersistableBusinessObjectExtension extension) {
    }

    public void refreshNonUpdateableReferences() {
    }

    public void refreshReferenceObject(String referenceObjectName) {
    }

    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        return null;
    }

    public boolean isNewCollectionRecord() {
        return false;
    }

    public void setNewCollectionRecord(boolean isNewCollectionRecord) {
    }

    public void linkEditableUserFields() {
    }

    public void refresh() {
    }

    public String getObjectId() {
        return null;
    }

    public Long getVersionNumber() {
        return null;
    }

}