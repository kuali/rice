/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.service.impl;

import java.util.Collection;
import java.util.Map;

import org.kuali.rice.kim.bo.AbstractAttributeBase;
import org.kuali.rice.kim.bo.AbstractQualifiedRoleAttribute;
import org.kuali.rice.kim.dto.AbstractQualifiedRoleAttributeDTO;

/**
 * This is a description of what this class does - lindholm don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ImplUtils {

	/**
	 * This method ...
	 *
	 * @param requiredAttributes
	 * @param attributes
	 */
	public static boolean hasAllAttributes(
			final Map<String, String> requiredAttributes,
			final Collection<? extends AbstractAttributeBase> attributes) {

		for (String key : requiredAttributes.keySet()) {
			if (!hasAttribute(attributes, key, requiredAttributes.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * This method ...
	 *
	 * @param qualifiedAttributes
	 * @param attributes
	 * @return
	 */
	public static boolean hasAllQualifiedAttributes(
			final Map<String, String> qualifiedAttributes,
			final Collection<? extends AbstractQualifiedRoleAttribute> attributes) {

		for (String key : qualifiedAttributes.keySet()) {
			if (!hasQualifiedAttribute(attributes, key, qualifiedAttributes.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * This method ...
	 *
	 * @param qualifiedAttributes
	 * @param attributes
	 * @return
	 */
	public static boolean hasAllQualifiedAttributeDtos(final Map<String, String> qualifiedAttributes, final Map<String, ? extends AbstractQualifiedRoleAttributeDTO> attributes) {
		for (String key : qualifiedAttributes.keySet()) {
			if (!hasQualifiedAttributeDto(attributes, key, qualifiedAttributes.get(key))) {
				return false;
			}
		}
		return true;

	}

	/**
	 *
	 * This method ...
	 *
	 * @param attributes
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean hasAttribute(
			final Collection<? extends AbstractAttributeBase> attributes,
			final String name, final String value) {

		for (AbstractAttributeBase ra : attributes) {
			if (name.equals(ra.getAttributeName())
					&& value.equals(ra.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * This method ...
	 *
	 * @param attributes
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean hasQualifiedAttribute(
			final Collection<? extends AbstractQualifiedRoleAttribute> attributes,
			final String name, final String value) {

		for (AbstractQualifiedRoleAttribute ra : attributes) {
			if (name.equals(ra.getAttributeName())
					&& value.equals(ra.getAttributeValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * This method ...
	 *
	 * @param attributes
	 * @param name
	 * @param value
	 * @return
	 */
	public static boolean hasQualifiedAttributeDto(
			final Map<String, ? extends AbstractQualifiedRoleAttributeDTO> attributes,
			final String name, final String value) {

		for (String key : attributes.keySet()) {
			AbstractQualifiedRoleAttributeDTO ra = attributes.get(key);

			if (name.equals(ra.getAttributeName())
					&& value.equals(ra.getAttributeValue())) {
				return true;
			}
		}
		return false;
	}

}
