/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krad.data;

import java.util.List;
import java.util.Map;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

/**
 * Wraps a data object and it's associated metadata. Provides additional utility methods to access portions of the data
 * object based on the metadata that's available.
 *
 * <p>This interface extends the {@link BeanWrapper} interface provided by Spring which means property references can
 * be nested and may be auto-grown depending on the setting of {@link #isAutoGrowNestedPaths()}</p>
 *
 * @param <T> the type of the data object instance which is wrapped by this accessor
 */
public interface DataObjectWrapper<T> extends BeanWrapper {

    /**
     * Return the type of the wrapped data object.
     *
     * @return the type of the wrapped data instance, or <code>null</code> if no wrapped object has been set
     */
    @Override
    Class<T> getWrappedClass();

    /**
     * Returns the data object wrapped by this accessor.
     *
     * @return the data object wrapped by this accessor
     */
    @Override
    T getWrappedInstance();

    /**
     * Returns the metadata of the data object wrapped by this accessor.
     *
     * @return the metadata of the data object wrapped by this accessor
     */
    DataObjectMetadata getMetadata();

    /**
     * Get the current value of the specified property, but suppresses any
     * {@link org.springframework.beans.NullValueInNestedPathException}s that would be thrown if a null value is
     * encountered in a nested path and just returns null instead. This method is essentially a convenience method to
     * prevent calling code from having to wrap calls to {@link #getPropertyValue(String)} with a try-catch block to
     * check for NullValueInNestedPathExceptions.
     *
     * @param propertyName the name of the property to get the value of
     * (may be a nested path and/or an indexed/mapped property)
     * @return the value of the property, or null if any null values were encountered in nested paths
     * @throws org.springframework.beans.InvalidPropertyException if there is no such property or
     * if the property isn't readable
     * @throws org.springframework.beans.PropertyAccessException if the property was valid but the
     * accessor method failed
     */
    Object getPropertyValueNullSafe(String propertyName) throws BeansException;

    /**
     * Returns a map containing the values of the primary keys on this data object. The key of this map will be the
     * attribute name of the primary key field on the data object and the value will the value of that primary key
     * attribute on the data object wrapped by this accessor. If this data object has no primary key fields, an empty
     * map will be returned.
     *
     * @return a map of primary key values where the key is the attribute name of the primary key field and the value
     *         is the value of that primary key field on the wrapped data object
     */
    Map<String, Object> getPrimaryKeyValues();

    /**
     * Determines if the given data object is equal to the data object wrapped by this accessor based on primary key
     * values only. If the given object is null, then this method will always return false.
     *
     * @param object the object to compare to the data object wrapped by this accessor
     * @return true if the primary key values are equal, false otherwise
     */
    boolean equalsByPrimaryKey(T object);

	/**
	 * Returns whether all fields in the primary key are populated with a non-null/non-blank value.
	 */
	boolean areAllPrimaryKeyAttributesPopulated();

	/**
	 * Returns whether any fields in the primary key is populated with a non-null/non-blank value.
	 */
	boolean areAnyPrimaryKeyAttributesPopulated();

	/**
	 * Returns the list of field of the primary key which have a null or blank value.
	 */
	List<String> getUnpopulatedPrimaryKeyAttributeNames();
}
