/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.options;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.demo.uif.form.KradSampleAppForm;
import org.kuali.rice.krad.labs.kitchensink.UifComponentsTestForm;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.view.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FoodKeyValuesFinder extends UifKeyValuesFinderBase {

    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        String foodType = null;

        if (model instanceof KradSampleAppForm) {
            KradSampleAppForm testForm = (KradSampleAppForm) model;
            foodType = testForm.getInputField4();
        } else {
            UifComponentsTestForm testForm = (UifComponentsTestForm) model;
            foodType = testForm.getField88();
        }

        List<KeyValue> options = new ArrayList<KeyValue>();

        //options would come from a db in a real scenario
        if (foodType == null || foodType.equals("Fruits")) {
            options.add(new ConcreteKeyValue("Apples", "Apples"));
            options.add(new ConcreteKeyValue("Bananas", "Bananas"));
            options.add(new ConcreteKeyValue("Cherries", "Cherries"));
            options.add(new ConcreteKeyValue("Oranges", "Oranges"));
            options.add(new ConcreteKeyValue("Pears", "Pears"));
        } else if (foodType.equals("Vegetables")) {
            options.add(new ConcreteKeyValue("Beans", "Beans"));
            options.add(new ConcreteKeyValue("Broccoli", "Broccoli"));
            options.add(new ConcreteKeyValue("Cabbage", "Cabbage"));
            options.add(new ConcreteKeyValue("Carrots", "Carrots"));
            options.add(new ConcreteKeyValue("Celery", "Celery"));
            options.add(new ConcreteKeyValue("Corn", "Corn"));
            options.add(new ConcreteKeyValue("Peas", "Peas"));
        }

        return options;
    }

}
