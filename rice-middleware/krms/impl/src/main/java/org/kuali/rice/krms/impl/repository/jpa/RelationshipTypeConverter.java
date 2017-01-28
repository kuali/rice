/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krms.impl.repository.jpa;

import org.kuali.rice.krms.api.repository.typerelation.RelationshipType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * A JPA converter that converts String code values to corresponding RelationshipType
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Converter
public class RelationshipTypeConverter implements AttributeConverter<RelationshipType, String> {

    @Override
    public String convertToDatabaseColumn(RelationshipType objectValue) {
        if (objectValue != null) {
            return objectValue.getCode();
        }

        return null;
    }

    @Override
    public RelationshipType convertToEntityAttribute(String dataValue) {
        RelationshipType result = null;

        if (dataValue != null) {
            result = RelationshipType.fromCode(dataValue);
        }

        return result;
    }
}