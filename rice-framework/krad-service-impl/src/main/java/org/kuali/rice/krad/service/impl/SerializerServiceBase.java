/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.krad.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.ReferenceByXPathMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.PathTracker;
import com.thoughtworks.xstream.mapper.Mapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentSerializerService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.SerializerService;
import org.kuali.rice.krad.service.XmlObjectSerializerService;
import org.kuali.rice.krad.service.util.DateTimeConverter;
import org.kuali.rice.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertyType;
import org.kuali.rice.krad.util.documentserializer.SerializationState;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.AutoPopulatingList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the {@link DocumentSerializerService}.  If no &lt;workflowProperties&gt; have been defined in the
 * data dictionary for a document type (i.e. {@link Document#getDocumentPropertySerizabilityEvaluator()} returns an instance of
 * {@link AlwaysTruePropertySerializibilityEvaluator}), then this service will revert to using the {@link XmlObjectSerializerService}
 * bean, which was the old way of serializing a document for routing.  If workflowProperties are defined, then this implementation
 * will selectively serialize items.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class SerializerServiceBase implements SerializerService  {

	protected LegacyDataAdapter legacyDataAdapter;
    protected XmlObjectSerializerService xmlObjectSerializerService;

    protected XStream xstream;

    // ThreadLocals to track state during serialization

    protected ThreadLocal<PropertySerializabilityEvaluator> evaluators;
    protected ThreadLocal<Map<String, SerializationState>> pathToSerializationState;
    protected ThreadLocal<PathTracker> currentPathTracker;

    public SerializerServiceBase() {
        evaluators = new ThreadLocal<>();
        currentPathTracker = new ThreadLocal<>();
        pathToSerializationState = new ThreadLocal<>();

        xstream = new XStream(new ProxyAndStateAwareJavaReflectionProvider());
        xstream.setMarshallingStrategy(new PathTrackerSmugglingMarshallingStrategy(currentPathTracker));
        xstream.registerConverter(new ProxyConverter(xstream.getMapper(), xstream.getReflectionProvider() ));
        try {
        	Class<?> objListProxyClass = Class.forName("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
            xstream.addDefaultImplementation(ArrayList.class, objListProxyClass);
            xstream.addDefaultImplementation(AutoPopulatingList.class, objListProxyClass);
        } catch ( Exception ex ) {
        	// Do nothing - this will blow if the OJB class does not exist, which it won't in some installs
        }
        xstream.registerConverter(new AutoPopulatingListConverter(xstream.getMapper()));
        xstream.registerConverter(new DateTimeConverter());
    }

    /**
     * Execute the specified {@link Serializer} with the appropriate setup and tear down, and return the serialized XML
     * when done.
     */
    protected <T> String doSerialization(PropertySerializabilityEvaluator evaluator, T object, Serializer<T> serializer) {
        try {
            evaluators.set(evaluator);
            pathToSerializationState.set(new HashMap<String, SerializationState>());
            currentPathTracker.set(null);
            return serializer.serialize(object);
        } finally {
            evaluators.set(null);
            pathToSerializationState.set(null);
            currentPathTracker.set(null);
        }
    }

    public String serializeBusinessObjectToXml(Object businessObject) {
        final PropertySerializabilityEvaluator propertySerizabilityEvaluator =
                getPropertySerizabilityEvaluator(businessObject);
        return doSerialization(propertySerizabilityEvaluator, businessObject, new Serializer<Object>() {
            @Override
            public String serialize(Object object) {
                String xml;
                if (propertySerizabilityEvaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
                    xml = getXmlObjectSerializerService().toXml(object);
                } else {
                    xml = xstream.toXML(object);
                }
                return xml;
            }
        });
    }

    /**
     * Method called by the ProxyAndStateAwareJavaReflectionProvider during serialization to determine if a field
     * should be omitted from the serialized form.
     *
     * <p>This is a short circuit check that will avoid more expensive calls in to the PropertySerializabilityEvaluator
     * if it returns true.</p>
     *
     * @param field the field
     * @return true if the field should be omitted
     */
    protected boolean ignoreField(Field field) {
        return false;
    }

    /**
     * Get the appropriate {@link PropertySerializabilityEvaluator} for the given dataObject.
     *
     * @param dataObject the data object
     * @return the evaluator
     */
    protected abstract PropertySerializabilityEvaluator getPropertySerizabilityEvaluator(Object dataObject);

    protected PathTracker getCurrentPathTracker() {
        PathTracker pathTracker = currentPathTracker.get();
        if (pathTracker == null) {
            throw new IllegalStateException("No XStream PathTracker is bound to the current thread");
        }
        return pathTracker;
    }

    /**
     * Parse the given explicit XPath expression to find the path to the parent XML element.
     *
     * @param pathString
     * @return the parent path, or empty string if the given path represents the root path of the xml document
     * @throws IllegalArgumentException if the given path is not a valid path (i.e. doesn't contain a "/")
     */
    private String parseParentPath(String pathString) {
        int indexOfLastSlash = pathString.lastIndexOf("/");
        if (indexOfLastSlash == -1) {
            throw new IllegalArgumentException("Expected a path");
        }
        return pathString.substring(0, indexOfLastSlash);
    }

    /**
     * Returns the SerializationState for the given path string
     */
    private SerializationState determineSerializationState(String pathString) {
        if (pathToSerializationState.get().isEmpty()) {
            pathToSerializationState.get().put(pathString, new SerializationState());
        }
        return searchSerializationState(pathString, pathString);
    }

    /**
     * Attempts to find the SerializationState for the given path string, searching the parent paths if none found
     */
    private SerializationState searchSerializationState(String pathString, String originalPath) {
        if (StringUtils.isBlank(pathString)) {
            throw new IllegalStateException("Failed to find existing SerializationState for path: " + originalPath);
        }
        SerializationState state = pathToSerializationState.get().get(pathString);
        return state != null ? state : searchSerializationState(parseParentPath(pathString), originalPath);
    }

    /**
     * Records the given serialization state if it is not already registered.
     */
    private void registerSerializationStateForField(SerializationState state, String fieldName, PropertyType propertyType, String parentPath) {
        String path = parentPath + "/" + fieldName;
        if (pathToSerializationState.get().get(path) == null) {
            SerializationState newState = new SerializationState(state);
            newState.addSerializedProperty(fieldName, propertyType);
            pathToSerializationState.get().put(path, newState);
        }
    }

    /**
     * A simple functional interface that defines a method which executes serialization to an XML string
     */
    protected interface Serializer<T> {
        String serialize(T object);
    }

    public class ProxyConverter extends ReflectionConverter {
        public ProxyConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
            super(mapper, reflectionProvider);
        }
        @Override
		public boolean canConvert(Class clazz) {
            return clazz.getName().contains("CGLIB") || clazz.getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl");
        }

        @Override
		public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
            if (obj.getClass().getName().equals("org.apache.ojb.broker.core.proxy.ListProxyDefaultImpl")) {
                List copiedList = new ArrayList();
                List proxiedList = (List) obj;
                for (Iterator iter = proxiedList.iterator(); iter.hasNext();) {
                    copiedList.add(iter.next());
                }
                context.convertAnother( copiedList );
            }
            else {
                super.marshal(legacyDataAdapter.resolveProxy(obj), writer, context);
            }
        }

        @Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            return null;
        }
    }

    public class ProxyAndStateAwareJavaReflectionProvider extends PureJavaReflectionProvider {
        @Override
        public void visitSerializableFields(Object object, Visitor visitor) {
            PathTracker pathTracker = getCurrentPathTracker();
            PropertySerializabilityEvaluator evaluator = evaluators.get();
            String currentPath = pathTracker.getPath().toString();
            SerializationState state = determineSerializationState(currentPath);


            for (Iterator iterator = fieldDictionary.serializableFieldsFor(object.getClass()); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!fieldModifiersSupported(field)) {
                    continue;
                }

                if (ignoreField(field)) {
                    continue;
                }

                validateFieldAccess(field);

                initializeField(object, field);

                Object value = null;
                try {
                    value = field.get(object);
                } catch (IllegalArgumentException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
                }

                if (evaluator.isPropertySerializable(state, object, field.getName(), value)) {
                    if (value != null && legacyDataAdapter.isProxied(value)) {
                        // resolve proxies after we determine that it's serializable
                        value = legacyDataAdapter.resolveProxy(value);
                    }
                    PropertyType propertyType = evaluator.determinePropertyType(value);
                    registerSerializationStateForField(state, field.getName(), propertyType, currentPath);
                    visitor.visit(field.getName(), field.getType(), field.getDeclaringClass(), value);
                }
            }
        }

        protected void initializeField(Object object, Field field) {
        }
    }

    public class AutoPopulatingListConverter extends CollectionConverter {

    	public AutoPopulatingListConverter(Mapper mapper){
    		super(mapper);
    	}

        @Override
    	public boolean canConvert(Class clazz) {
    		return clazz.equals(AutoPopulatingList.class);
        }

    }

    private static class PathTrackerSmugglingMarshallingStrategy extends ReferenceByXPathMarshallingStrategy {

        private final ThreadLocal<PathTracker> pathTrackerThreadLocal;

        public PathTrackerSmugglingMarshallingStrategy(ThreadLocal<PathTracker> pathTrackerThreadLocal) {
            super(ReferenceByXPathMarshallingStrategy.RELATIVE);
            this.pathTrackerThreadLocal = pathTrackerThreadLocal;
        }

        @Override
        protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
            TreeMarshaller treeMarshaller = super.createMarshallingContext(writer, converterLookup, mapper);
            smugglePathTracker(treeMarshaller);
            return treeMarshaller;
        }

        /**
         * Shhh...don't tell anybody, but we are going to smuggle the PathTracker out of here so we can
         * reference it during marshalling in our custom reflection provider.
         *
         * This is really an XStream internal API so has the potential to break us horribly if they change the
         * implementation in the future. We are betting on our unit tests catching that if it happens.
         */
        private void smugglePathTracker(TreeMarshaller treeMarshaller) {
            try {
                PathTracker pathTracker = (PathTracker)FieldUtils.readField(treeMarshaller, "pathTracker", true);
                if (pathTracker == null) {
                    throw new IllegalStateException("The pathTracker on xstream marshaller is null");
                }
                this.pathTrackerThreadLocal.set(pathTracker);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

    }

    protected XmlObjectSerializerService getXmlObjectSerializerService() {
        return this.xmlObjectSerializerService;
    }

    @Required
    public void setXmlObjectSerializerService(XmlObjectSerializerService xmlObjectSerializerService) {
        this.xmlObjectSerializerService = xmlObjectSerializerService;
    }

    @Required
	public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
		this.legacyDataAdapter = legacyDataAdapter;
	}
}

