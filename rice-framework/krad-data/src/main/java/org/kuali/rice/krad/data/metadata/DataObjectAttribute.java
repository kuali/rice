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
package org.kuali.rice.krad.data.metadata;

import com.google.common.annotations.Beta;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.provider.PersistenceProvider;
import org.kuali.rice.krad.data.provider.annotation.UifDisplayHint;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

import java.beans.PropertyEditor;
import java.util.Set;

/**
* Attribute metadata
*
* <p>
* Represents attribute metadata (persistent or non-persistent) for a data object.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public interface DataObjectAttribute extends MetadataCommon {

    /**
    * Gets the data object type
    *
    * <p>
    * Gets the data object type to which this attribute belongs.
    * </p>
    *
    * @return class type
    */
	Class<?> getOwningType();

    /**
    * Gets the display attribute name.
    *
    * <p>
    * To be used on attributes which have an associated business key that is shown to users rather than the "internal"
    * key which is likely a meaningless (to the users) sequence number.
    * </p>
    *
    * @return user friendly business key
    */
	String getDisplayAttributeName();

    /**
    * Gets the maximum length.
    *
    * <p>
    * The maximum length value which will be accepted into this field.
    * </p>
    *
    * @return maximum length
    */
	Long getMaxLength();

    /**
    * Gets the minimum length.
    *
    * <p>
    * The minimum length value which will be accepted into this field.
    * </p>
    *
    * @return minimum length
    */
	Long getMinLength();

    /**
    * Determines if attribute is case insensitive.
    *
    * <p>
    * Whether this attribute should be treated as case insensitive when performing lookups and searches.
    * </p>
    *
    * @return attribute case insensitive
    */
	boolean isCaseInsensitive();

    /**
    * Determines if attribute should be forced to upper case.
    *
    * <p>
    * Whether this attribute should be forced to upper case before being sent to the {@link PersistenceProvider}.
    * </p>
    *
    * @return attribute forced upper case
    */
	boolean isForceUppercase();

    /**
    * Determines if attribute is required.
    *
    * <p>
    * Whether (at the data level) this attribute must have a non-null value.
    * </p>
    *
    * @return attribute is required
    */
	boolean isRequired();

    /**
    * BETA: Gets the bean name.
    *
    * <p>
    * The bean name (in the UIF data dictionary), which checks the entered value's characters for correctness.
    * </p>
    *
    * @return bean name
    */
    @Beta
	String getValidCharactersConstraintBeanName();

    /**
    * Gets the property editor.
    *
    * <p>
    * To be used by the persistence layer when loading and persisting the data.
    * (E.g., strip extra characters from phone numbers to leave only the digits for storage in the database.).
    * </p>
    *
    * @return property editor
    */
	PropertyEditor getPropertyEditor();

    /**
    * Determines attribute case insensitivity.
    *
    * <p>
    * Whether this attribute is protected at the persistence level and should be protected by default when included on
    * user interfaces.
    * </p>
    *
    * @return attribute case insensitivity
    */
	boolean isSensitive();

    /**
    * Gets the values if a drop-down.
    *
    * <p>
    * If this field should be rendered using a drop-down list, specify the instance on this property.
    * </p>
    *
    * @return drop-down values
    */
	KeyValuesFinder getValidValues();

    /**
    * Gets the derived krad data type.
    *
    * <p>
    * The derived krad-data data type used by the UIF to help generate the appropriate control and perform default
    * validation.
    * </p>
    *
    * @return derived keard data type
    */
    DataType getDataType();

    /**
    * Determines whether data object is persistent.
    *
    * <p>
    * Whether or not this attribute of the data object is saved to persistent storage when saved via the
    * {@link DataObjectService}.
    * </p>
    *
    * @return whether data object is persistent
    */
	boolean isPersisted();

    /**
    * Gets class type object is inherited from.
    *
    * <p>
    * If this attribute is inherited from a different data object, that object's type. Otherwise null.
    * </p>
    *
    * @return class type inherited from
    */
	Class<?> getInheritedFromType();

    /**
    * Gets inherited attribute name.
    *
    * <p>
    * If this attribute is inherited from a different data object, the source attribute name. Otherwise null.
    * </p>
    *
    * @return inherited attribute name
    */
	String getInheritedFromAttributeName();

    /**
    * Gets inherited parent attribute name.
    *
    * <p>
    * If this attribute is inherited from a different data object, the attribute name on the parent object. Otherwise
    * null.
    * </p>
    *
    * @return inherited parent attribute name
    */
	String getInheritedFromParentAttributeName();

    /**
    * Determines whether attribute is inherited.
    *
    * <p>
    * Whether this attribute is inherited from a different data object.
    * </p>
    *
    * @return whether attribute is inherited
    */
	boolean isInherited();

    /**
    * Gets original data object.
    *
    * <p>
    * Obtains the "original" data object attribute in a chain of embedded attribute definitions.
    * </p>
    *
    * @return original data object
    */
	DataObjectAttribute getOriginalDataObjectAttribute();

    /**
    * BETA: Gets the display hints.
    *
    * <p>
    * Returns a set of display hints which can be used by the UIF layer when displaying these fields.
    * </p>
    *
    * @return display hints
    */
    @Beta
	Set<UifDisplayHint> getDisplayHints();
}
