package org.kuali.rice.kim.impl.data;

import java.util.List;

public interface DataIntegrityService {

    List<String> checkIntegrity();

    List<String> repair();

}
