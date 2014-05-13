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
package org.kuali.rice.kew.api.action

import org.junit.Assert
import org.junit.Test

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller

/**
 * Unit test for the ValidActions DTO
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
class ValidActionsTest {

    @Test
    public void testMarshallUnmarshall() throws Exception {
        ValidActions.Builder validActionsBuilder = ValidActions.Builder.create();

        validActionsBuilder.addValidAction(ActionType.ACKNOWLEDGE);
        validActionsBuilder.addValidAction(ActionType.FYI);

        ValidActions validActions = validActionsBuilder.build();

        // verify that we can marshall a ValidActions
        JAXBContext jc = JAXBContext.newInstance(ValidActions.class);
        Marshaller marshaller = jc.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(validActions, sw);
        String xml = sw.toString();
        // System.out.println(xml);

        // verify that we can unmarshall a ValidActions...
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        ValidActions unmarshalledValidActions = unmarshaller.unmarshal(new StringReader(xml));

        // ...and that what we get is equal to the original object
        Assert.assertEquals(validActions, unmarshalledValidActions);
    }

}
