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
package org.kuali.rice.kns.uif.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.uif.DataBinding;
import org.kuali.rice.kns.uif.container.View;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * Utility class provides methods for working with a <code>View</code> instance
 * and a group of models
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ViewModelUtils {

	/**
	 * Determines the model <code>Class</code> that the given attribute belongs
	 * to
	 * <p>
	 * Model is determined by inspecting the modelClasses Map of the
	 * <code>View</code>. If the Map only contains one entry, that entry value
	 * (giving the Class) is returned. Otherwise the the Map is searched for key
	 * that equals the modelName property for the component.
	 * </p>
	 * 
	 * @param view
	 *            - the view instance containing the field and model
	 *            configuration
	 * @param component
	 *            - the component to find the associated model class for
	 * @return Class<?> for the model which the attribute belongs, or Null if a
	 *         model was not found
	 */
	public static final Class<?> getModelClassForComponent(View view, DataBinding component) {
		Class<?> modelClass = null;

		Map<String, Class<?>> modelClasses = view.getModelClasses();
		if (modelClasses.size() == 1) {
			modelClass = modelClasses.values().iterator().next();
		}
		else {
			if (modelClasses.containsKey(component.getModelName())) {
				modelClass = modelClasses.get(component.getModelName());
			}
		}

		return modelClass;
	}

	/**
	 * Determines the model instance the component is associated with. If only
	 * one model is given, that model instance is returned, otherwise the
	 * <code>Map</code> is inspected to find the entry with key equal to the
	 * components model name.
	 * 
	 * @param models
	 *            - Map of model entries to search
	 * @param component
	 *            - component instance to find model for
	 * @return Object model instance or Null if a model was not found
	 */
	public static final Object getModelForComponent(Map<String, Object> models, DataBinding component) {
		Object model = null;

		if (models.size() == 1) {
			model = models.values().iterator().next();
		}
		else {
			if (models.containsKey(component.getModelName())) {
				model = models.get(component.getModelName());
			}
		}

		return model;
	}

	/**
	 * Determines the model associated with the given component and then
	 * retrieves the property value from the model as a <code>Collection</code>
	 * 
	 * @param models
	 *            - Map of model entries to search and retrieve value from
	 * @param component
	 *            - component instance which binds to the collection property
	 * @return List<Object> from the model or Null if the model property was not
	 *         found
	 */
	public static final List<Object> getCollectionFromModels(Map<String, Object> models, DataBinding component) {
		List<Object> modelCollection = null;

		Object model = getModelForComponent(models, component);
		if (model != null) {
			BeanWrapper beanWrapper = new BeanWrapperImpl(model);
			modelCollection = (List<Object>) beanWrapper.getPropertyValue(component.getBindingPath());
		}

		return modelCollection;
	}

}
