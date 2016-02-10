package org.kuali.rice.kim.impl.responsibility;

import java.util.List;

public interface ResponsibilityDao {

    List<ResponsibilityBo> findWorkflowResponsibilities(String documentTypeName);

    List<ResponsibilityBo> findWorkflowExceptionResponsibilities(String documentTypeName);

}
