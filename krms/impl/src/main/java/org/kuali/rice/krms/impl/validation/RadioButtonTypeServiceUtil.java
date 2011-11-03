/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krms.impl.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableRadioButtonGroup;
import org.kuali.rice.krms.api.repository.type.KrmsAttributeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeAttribute;
import org.kuali.rice.krms.api.repository.type.KrmsTypeDefinition;
import org.kuali.rice.krms.api.repository.type.KrmsTypeRepositoryService;
import org.kuali.rice.krms.impl.repository.KrmsRepositoryServiceLocator;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RadioButtonTypeServiceUtil {

    public List<RemotableAttributeField> getAttributeFields(@WebParam(name = "krmsTypeId") String krmsTypeId) throws RiceIllegalArgumentException {

        if (StringUtils.isBlank(krmsTypeId)) {
            throw new RiceIllegalArgumentException("krmsTypeId must be non-null and non-blank");
        }

        List<RemotableAttributeField> results = new ArrayList<RemotableAttributeField>();

        // keep track of how to sort these
        final Map<String, Integer> sortCodeMap = new HashMap<String, Integer>();

        KrmsTypeDefinition krmsType =
                KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService().getTypeById(krmsTypeId);

        if (krmsType == null) {
            throw new RiceIllegalArgumentException("krmsTypeId must be a valid id of a KRMS type");
        } else {
            // translate attributes

            List<KrmsTypeAttribute> typeAttributes = krmsType.getAttributes();
            Map<String, Integer> attribDefIdSequenceNumbers = new TreeMap<String, Integer>();
            Map<String, String> unsortedIdLables = new TreeMap<String, String>();
            if (!CollectionUtils.isEmpty(typeAttributes)) {
                // translate the attribute and store the sort code in our map
                for (KrmsTypeAttribute typeAttribute : typeAttributes) {

                    KrmsTypeRepositoryService typeRepositoryService = KrmsRepositoryServiceLocator.getKrmsTypeRepositoryService();

                    KrmsAttributeDefinition attributeDefinition =
                            typeRepositoryService.getAttributeDefinitionById(typeAttribute.getAttributeDefinitionId());

                    attribDefIdSequenceNumbers.put(attributeDefinition.getId(), typeAttribute.getSequenceNumber());
                    unsortedIdLables.put(attributeDefinition.getId(), attributeDefinition.getLabel());
                }

                Map<String, String> sortedKeyLabelMap = new TreeMap<String, String>(new LabelSequenceComparator(attribDefIdSequenceNumbers));
                sortedKeyLabelMap.putAll(unsortedIdLables);

                RemotableAttributeField attributeField = translateTypeAttribute(krmsType, sortedKeyLabelMap);
                results.add(attributeField);
            }
        }
        return results;
    }

    private RemotableAttributeField translateTypeAttribute(KrmsTypeDefinition krmsType,
            Map<String, String> keyLabels) {

        RemotableAttributeField.Builder builder = RemotableAttributeField.Builder.create(krmsType.getName());

        RemotableRadioButtonGroup.Builder controlBuilder = RemotableRadioButtonGroup.Builder.create(keyLabels);

        builder.setLongLabel(krmsType.getName());
        builder.setName(krmsType.getName());
        builder.setRequired(true);
        List<String> defaultValue = new ArrayList<String>();
        defaultValue.add(keyLabels.get(keyLabels.keySet().toArray()[0]));
        builder.setDefaultValues(defaultValue);

//            builder.setHelpSummary("helpSummary: " + krmsType.getDescription());
//            builder.setHelpDescription("helpDescription: " + krmsType.getDescription());
        builder.setControl(controlBuilder);
//            builder.setMaxLength(400);

        return builder.build();
    }

    /**
     * Sort the keys by their sequence number, if the sequence numbers are the same then fall back to the regular natural key order.
     */
    class LabelSequenceComparator implements Comparator {
        Map<String, Integer> keySequences = new TreeMap<String, Integer>();
        public LabelSequenceComparator(Map<String, Integer> labelSequence) {
            this.keySequences.putAll(labelSequence);
        }
        @Override
        public int compare(Object o, Object o1) {
            if (keySequences.get(o) != null && keySequences.get(o1) != null) {
                int compare = keySequences.get(o).compareTo(keySequences.get(o1));
                if (compare == 0) { // if the sequence number is the same, fall back to key natural order
                    return ((String)o).compareTo((String)o1);
                }
                return compare;
            }
            return 1; // don't throw anything away (ie return 0)
        }
    }
}
