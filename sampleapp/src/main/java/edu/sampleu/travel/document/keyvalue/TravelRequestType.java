/*
 * Copyright 2007 The Kuali Foundation
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
package edu.sampleu.travel.document.keyvalue;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.core.util.ConcreteKeyValue;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class TravelRequestType extends KeyValuesBase {

    public List getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        keyValues.add(new ConcreteKeyValue("", ""));
        keyValues.add(new ConcreteKeyValue("TRT1", "Travel Request Type 1"));
        keyValues.add(new ConcreteKeyValue("TRT2", "Travel Request Type 2"));

        // This should populate Type 3 only if we can get the form from GlobalVariables
        // and if we can get the document from the form and the document is not null;
        // this should be true when this ValuesFinder is used within the context of the webapp.
        KualiForm form = GlobalVariables.getKualiForm();
    	if ((form != null) && (form instanceof KualiDocumentFormBase)) {
    	    Document doc =((KualiDocumentFormBase)form).getDocument();
    	    if (doc != null) {
    	        keyValues.add(new ConcreteKeyValue("TRT3", "Travel Request Type 3"));
    	    }
    	}

        return keyValues;
    }

}
