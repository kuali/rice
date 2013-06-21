/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may	obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krms.impl.repository.language;

import java.util.Map;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplaterContract;

/**
 * A dead simple implementation of the templater contract for testing
 * @author nwright
 */
public class SimpleNaturalLanguageTemplater implements NaturalLanguageTemplaterContract {

    @Override
    public String translate(NaturalLanguageTemplate naturalLanguageTemplate, Map<String, Object> contextMap) {
        StringBuilder sb = new StringBuilder (naturalLanguageTemplate.getTemplate());
        sb.append(" applied with the following ");
        sb.append (contextMap.size());        
        sb.append(" variables: ");
        String comma = "";
        for (String key : contextMap.keySet()) {
            sb.append (comma);
            comma = ",";
            sb.append ("[");
            sb.append (key);
            sb.append ("=");
            sb.append (contextMap.get(key));
            sb.append ("]");
        }
        return sb.toString();
    }
     
}
