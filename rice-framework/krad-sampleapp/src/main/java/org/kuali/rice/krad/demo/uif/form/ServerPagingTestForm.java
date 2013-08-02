/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.form;

import org.kuali.rice.krad.web.form.UifFormBase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * form for the server paging component library page
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServerPagingTestForm extends UifFormBase {

    private static final long serialVersionUID = -8790636700086973158L;

    // Fields needed on component library forms
    private String themeName;
    private String exampleShown;
    private String currentExampleIndex;

    List<ServerPagingTestObject> collection1;

    public ServerPagingTestForm() {
        int collection1Size = 1000;
        this.collection1 = new ArrayList<ServerPagingTestObject>(collection1Size);
        Random random = new Random(2);

        for (int i = 1; i < collection1Size; i++) {
            ServerPagingTestObject listItem = new ServerPagingTestObject();

            listItem.setIntegerField(i);

            // random rating between 8 and 10
            BigDecimal rating = new BigDecimal(BigInteger.valueOf(random.nextInt(200) + 800), 2);
            listItem.setDoubleField(rating);

            String standardDesc = ", I give XKCD " + i + " a rating of " + listItem.getDoubleField();

            if (rating.doubleValue() < 8.3) {
                listItem.setStringField("Funny" + standardDesc);
            } else if (rating.doubleValue() < 8.6) {
                listItem.setStringField("Good one" + standardDesc);
            } else if (rating.doubleValue() < 8.9) {
                listItem.setStringField("Really liked it" + standardDesc);
            } else if (rating.doubleValue() < 9.2) {
                listItem.setStringField("Clever" + standardDesc);
            } else if (rating.doubleValue() < 9.5) {
                listItem.setStringField("Brilliant" + standardDesc);
            } else if (rating.doubleValue() < 9.8) {
                listItem.setStringField("Stupendous" + standardDesc);
            } else {
                listItem.setStringField("Celestial" + standardDesc);
            }

            collection1.add(listItem);
        }
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getExampleShown() {
        return exampleShown;
    }

    public void setExampleShown(String exampleShown) {
        this.exampleShown = exampleShown;
    }

    public String getCurrentExampleIndex() {
        return currentExampleIndex;
    }

    public void setCurrentExampleIndex(String currentExampleIndex) {
        this.currentExampleIndex = currentExampleIndex;
    }

    public List<ServerPagingTestObject> getCollection1() {
        return collection1;
    }

    public void setCollection1(List<ServerPagingTestObject> collection1) {
        this.collection1 = collection1;
    }

}
