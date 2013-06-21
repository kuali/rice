/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository.mock;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.term.TermRepositoryService;
import org.kuali.rice.krms.api.repository.term.TermSpecificationDefinition;

/**
 *
 * @author nwright
 */
public class KrmsTermSpecificationLoader {
    
    private TermRepositoryService termRepositoryService = null;
    
    public TermRepositoryService getTermRepositoryService() {
        return termRepositoryService;
    }
    
    public void setTermRepositoryService(TermRepositoryService termRepositoryService) {
        this.termRepositoryService = termRepositoryService;
    }
    
    public void loadTermSpec(String id, String name, String type, String description, String namespace) {
        TermSpecificationDefinition.Builder bldr = TermSpecificationDefinition.Builder.create(id, name, namespace, type);
        bldr.setDescription(description);
        bldr.setId(id);
        bldr.setActive(true);
        TermSpecificationDefinition existing = this.findExisting(bldr);
        if (existing == null) {
            this.getTermRepositoryService().createTermSpecification(bldr.build());
        } else {
            bldr.setVersionNumber(existing.getVersionNumber());
            this.getTermRepositoryService().updateTermSpecification(bldr.build());
        }
    }
    
    private TermSpecificationDefinition findExisting(TermSpecificationDefinition.Builder bldr) {
        if (bldr.getId() != null) {
            try {
                return this.getTermRepositoryService().getTermSpecificationById(bldr.getId());
            } catch (RiceIllegalArgumentException ex) {
                return null;
            }
        }
        return this.getTermRepositoryService().getTermSpecificationByNameAndNamespace(bldr.getName(), bldr.getNamespace());
    }
    
    public void load() {
        loadTermSpec("10000", "CompletedCourse", "java.lang.Boolean", "Completed course", "KS-SYS");
        loadTermSpec("10001", "CompletedCourses", "java.lang.Boolean", "Completed courses", "KS-SYS");
        loadTermSpec("10002", "NumberOfCompletedCourses", "java.lang.Integer", " Number of completed courses", "KS-SYS");
        loadTermSpec("10003", "NumberOfCreditsFromCompletedCourses", "java.lang.Integer", "Number of credits from completed courses", "KS-SYS");
        loadTermSpec("10004", "EnrolledCourses", "java.lang.Integer", "Enrolled courses", "KS-SYS");
        loadTermSpec("10005", "GPAForCourses", "java.lang.Integer", "GPA for courses", "KS-SYS");
        loadTermSpec("10006", "GradeTypeForCourses", "java.lang.Integer", "Grade type for courses", "KS-SYS");
        loadTermSpec("10007", "NumberOfCredits", "java.lang.Integer", "Number of credits", "KS-SYS");
        loadTermSpec("10008", "NumberOfCreditsFromOrganization", "java.lang.Integer", "Number of credits from organization", "KS-SYS");
        loadTermSpec("10009", "AdminOrganizationPermissionRequired", "java.lang.Boolean", "Admin organization permission required", "KS-SYS");
        loadTermSpec("10010", "ScoreOnTest", "java.lang.Integer", "Score on test", "KS-SYS");
        loadTermSpec("10011", "AdmittedToProgram", "java.lang.Boolean", "Admitted to program", "KS-SYS");
        loadTermSpec("10012", "AdmittedToProgramLimitCoursesInOrgForDuration", "java.lang.Integer", "Admitted to program limit courses in organization for duration", "KS-SYS");
        loadTermSpec("10013", "FreeFormText", "java.lang.Boolean", "Free Form Text", "KS-SYS");
    }
}
