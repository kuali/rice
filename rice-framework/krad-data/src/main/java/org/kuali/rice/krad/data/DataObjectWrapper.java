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
package org.kuali.rice.krad.data;

import org.kuali.rice.krad.data.metadata.DataObjectMetadata;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

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
     * Returns the value of the primary key for the wrapped data object, or null if the wrapped object has no value for
     * it's primary key.
     *
     * <p>If the primary key consists of multiple values, this method will return an instance of {@link CompoundKey},
     * otherwise the single primary key value will be returned. If the primary key consists of multiple values but
     * those values are only partially populated, this method will return null.</p>
     *
     * @return the single primary key value for the wrapped data object, or null if it has no fully-populated primary
     *         key value
     */
    Object getPrimaryKeyValue();

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

    /**
     * Returns the value of the foreign key for the specified relationship on the wrapped data object, or null if the
     * wrapped object has no value for the requested foreign key.
     *
     * <p>If the foreign key is a compound/composite and consists of multiple values, this method will return an
     * instance of {@link CompoundKey}, otherwise the single foreign key value will be returned. If the foreign key is
     * compound/composite but
     * those values are only partially populated, this method will return null.</p>
     *
     * <p>It is common that a data object may have more than one field or set of fields that constitute a specific
     * foreign key for the specified relationship. In such cases there would be an attribute (or attributes) which
     * represent the foreign key as well as the related object itself. For example, consider the following
     * scenario:</p>
     *
     * <pre>
     * {@code
     * public class One {
     *     String twoId;
     *     Two two;
     * }
     * }
     * </pre>
     *
     * <p>In this case, {@code twoId} is an attribute that serves as a foreign key to {@code Two}, but the {@code two}
     * attribute would contain an internal {@code twoId} attribute which is the primary key value for {@code Two} and
     * represents the foreign key in this case.</p>
     *
     * <p>In cases like above, the {@code twoId} attribute on the {@code One} class would take precedence unless it
     * contains a null value, in which case this method will attempt to extract a non-null foreign key value from the
     * related object.</p>
     *
     * @param relationshipName the name of the relationship on the wrapped data object for which to determine the
     * foreign key value
     * @return the single foreign key value on the wrapped data object for the given relationship name, or null if it
     *         has no fully-populated foreign key value
     * @throws IllegalArgumentException if the given relationshipName does not represent a valid relationship for this
     * data object
     */
    Object getForeignKeyValue(String relationshipName);

    /**
     * As {@link #getForeignKeyValue(String)} except only returns the value for the "attribute" foreign key value. If
     * the wrapped data object has no attribute foreign key for the given relationship, this method will return null.
     *
     * @param relationshipName the name of the relationship on the wrapped data object for which to determine the
     * foreign key value
     * @return the single foreign key attribute value on the wrapped data object for the given relationship name, or
     *         null if it has no fully-populated foreign key attribute value
     * @throws IllegalArgumentException if the given relationshipName does not represent a valid relationship for this
     * data object
     */
    Object getForeignKeyAttributeValue(String relationshipName);

    /**
     * Get property type for property name on object, this can be a nested property and method will
     * recursively use the metadata to find type.
     * @param objectType - Root object type
     * @param propertyName - Property name
     * @return Class of propertyName
     */
    Class<?> getPropertyTypeNullSafe(Class<?> objectType, String propertyName);

    /**
     * Fetches and populates the value for the relationship with the given name on the wrapped data object.
     *
     * <p>This is done by identifying the current foreign key attribute value for the relationship using the algorithm
     * described on {@link #getForeignKeyAttributeValue(String)} and then loading the related object using that foreign
     * key value, updating the relationship value on the wrapped data object afterward.</p>
     *
     * <p>If the foreign key value is null or the loading of the related object using the foreign key returns a null
     * value, this method will set the relationship value to null.</p>
     *
     * @param relationshipName the name of the relationship on the wrapped data object to refresh
     * @throws IllegalArgumentException if the given relationshipName does not represent a valid relationship for this
     * data object
     * @throws DataAccessException if there is a data access problem when attempting to refresh the relationship
     */
    void fetchRelationship(String relationshipName);

}
