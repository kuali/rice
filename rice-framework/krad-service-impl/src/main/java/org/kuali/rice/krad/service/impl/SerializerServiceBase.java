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
import org.apache.commons.lang.reflect.FieldUtils;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentSerializerService;
import org.kuali.rice.krad.service.LegacyDataAdapter;
import org.kuali.rice.krad.service.SerializerService;
import org.kuali.rice.krad.service.XmlObjectSerializerService;
import org.kuali.rice.krad.service.util.DateTimeConverter;
import org.kuali.rice.krad.util.documentserializer.AlwaysTruePropertySerializibilityEvaluator;
import org.kuali.rice.krad.util.documentserializer.PropertySerializabilityEvaluator;
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
    //protected ThreadLocal<SerializationState> serializationStates;
    protected ThreadLocal<PropertySerializabilityEvaluator> evaluators;

    // this ThreadLocal stores a reference to the PathTracker being tracked by XStream
    protected ThreadLocal<PathTracker> currentPathTracker;

    protected Map<String, SerializationState> pathToSerializationState;

    public SerializerServiceBase() {
        //serializationStates = new ThreadLocal<SerializationState>();
        evaluators = new ThreadLocal<>();
        currentPathTracker = new ThreadLocal<>();
        pathToSerializationState = new HashMap<>();

        xstream = new XStream(new ProxyAndStateAwareJavaReflectionProvider());
        xstream.setMarshallingStrategy(new PathAwareReferenceByXPathMarshallingStrategy(currentPathTracker));
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
     * @see org.kuali.rice.krad.service.DocumentSerializerService#serializeDocumentToXmlForRouting(org.kuali.rice.krad.document.Document)
     */
    public String serializeBusinessObjectToXml(Object businessObject) {
        PropertySerializabilityEvaluator propertySerizabilityEvaluator =
                getPropertySerizabilityEvaluator(businessObject);
        evaluators.set(propertySerizabilityEvaluator);
        //SerializationState state = new SerializationState(); //createNewDocumentSerializationState(document);
        //serializationStates.set(state);

        //Object xmlWrapper = null;//wrapDocumentWithMetadata(document);
        String xml;
        if (propertySerizabilityEvaluator instanceof AlwaysTruePropertySerializibilityEvaluator) {
            xml = getXmlObjectSerializerService().toXml(businessObject);
        } else {
            xml = xstream.toXML(businessObject);
        }

        evaluators.set(null);
        //serializationStates.set(null);
        return xml;
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

//    protected SerializationState constructStateFromPathTracker(PathTracker pathTracker, PropertyType propertyType) {
//        SerializationState serializationState = new SerializationState();
//        serializationState.addSerializedProperty(pathTracker.peekElement(), propertyType);
//        pathTracker.getPath()
//    }

//    public class PathTrackerSerializationState extends SerializationState {
//
//        private final PathTracker pathTracker;
//
//        public PathTrackerSerializationState(PathTracker pathTracker) {
//            this.pathTracker = pathTracker;
//        }
//
//        @Override
//        public int numPropertyElements() {
//            return pathTracker.depth();
//        }
//
//        @Override
//        public void addSerializedProperty(String elementName, PropertyType propertyType) {
//            throw new UnsupportedOperationException("Operation not supported");
//        }
//
//        @Override
//        public void removeSerializedProperty() {
//            throw new UnsupportedOperationException("Operation not supported");
//        }
//
//        @Override
//        public String getElementName(int propertyIndex) {
//            return pathTracker.peekElement(propertyIndex);
//        }
//
//        @Override
//        public PropertyType getPropertyType(int propertyIndex) {
//            return super.getPropertyType(propertyIndex);
//        }
//    }

    protected SerializationState registerSerializationState(PathTracker pathTracker, PropertySerializabilityEvaluator evaluator, Object object) {
        String pathString = pathTracker.getPath().toString();
        SerializationState state = pathToSerializationState.get(pathString);
        if (state == null) {
            int indexOfLastSlash = pathString.lastIndexOf("/");
            if (indexOfLastSlash == -1) {
                throw new IllegalStateException("Expected a path");
            }
            String parentPathString = pathString.substring(0, indexOfLastSlash);
            SerializationState parentState = pathToSerializationState.get(parentPathString);
            if (parentState == null && parentPathString.isEmpty()) {
                parentState = new SerializationState();
            } else if (parentState == null) {
                throw new IllegalStateException("No parent state found");
            }
            state = new SerializationState(parentState);
            state.addSerializedProperty(pathTracker.peekElement(), evaluator.determinePropertyType(object));
            pathToSerializationState.put(pathString, state);
        }
        return state;
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
            SerializationState state = registerSerializationState(pathTracker, evaluator, object);

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

    public class PathAwareReferenceByXPathMarshallingStrategy extends ReferenceByXPathMarshallingStrategy {
        private final ThreadLocal<PathTracker> pathTrackerThreadLocal;
        public PathAwareReferenceByXPathMarshallingStrategy(ThreadLocal<PathTracker> pathTrackerThreadLocal) {
            super(ReferenceByXPathMarshallingStrategy.RELATIVE);
            this.pathTrackerThreadLocal = pathTrackerThreadLocal;
        }
        @Override
        protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
            TreeMarshaller treeMarshaller = super.createMarshallingContext(writer, converterLookup, mapper);
            try {
                PathTracker pathTracker = (PathTracker)FieldUtils.readField(treeMarshaller, "pathTracker", true);
                if (pathTracker == null) {
                    throw new IllegalStateException("The pathTracker on xstream marshaller is null");
                }
                this.pathTrackerThreadLocal.set(pathTracker);
                return treeMarshaller;
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

    protected SerializationState createNewDocumentSerializationState(Document document) {
        return new SerializationState();
    }

    @Required
	public void setLegacyDataAdapter(LegacyDataAdapter legacyDataAdapter) {
		this.legacyDataAdapter = legacyDataAdapter;
	}
}

