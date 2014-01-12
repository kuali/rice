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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityClassListener;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.kuali.rice.krad.data.jpa.DisableVersioning;
import org.kuali.rice.krad.data.jpa.RemoveMapping;
import org.kuali.rice.krad.data.jpa.RemoveMappings;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * EclipseLink Session Customizer which understands {@link PortableSequenceGenerator} annotations and automatically
 * registers custom EclipseLink Sequences.
 *
 * <p>Since SessionCustomizers are stateless instances, and because concrete
 * {@link org.eclipse.persistence.sequencing.Sequence} objects must be registered individually with the EclipseLink
 * session, we lazy generate the Sequence objects using annotation inspection and then register them on each new
 * session using this customizer.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KradEclipseLinkCustomizer implements SessionCustomizer {

    private static ConcurrentMap<String, List<Sequence>> sequenceMap = new ConcurrentHashMap<String, List<Sequence>>(8,
            0.9f, 1);

    /* Keyed by the session name determines if the class descriptors have been modified for the current session. */
    private static ConcurrentMap<String, Boolean> modDescMap = new ConcurrentHashMap<String, Boolean>();

    private static ConcurrentMap<String, List<QueryCustomizerGenerator>> queryCustomizerMap =
            new ConcurrentHashMap<String, List<QueryCustomizerGenerator>>();

    @Override
    public void customize(Session session) throws Exception {
        String sessionName = session.getName();

        // double-checked locking on ConcurrentMap
        List<Sequence> sequences = sequenceMap.get(sessionName);
        if (sequences == null) {
            sequences = sequenceMap.putIfAbsent(sessionName, loadSequences(session));
            if (sequences == null) {
                sequences = sequenceMap.get(sessionName);
            }
        }

        loadQueryCustomizers(session);

        DatabaseLogin login = session.getLogin();
        for (Sequence sequence : sequences) {
            login.addSequence(sequence);
        }

        handleDescriptorModifications(session);

    }

    /**
     * Load Query Customizer based on annotations on fields and call customizer to
     * modify descriptor
     * @param session
     */
    protected void loadQueryCustomizers(Session session) {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
        for (Class<?> entityClass : descriptors.keySet()) {
            for (Field field : entityClass.getDeclaredFields()) {
                String queryCustEntry = entityClass.getName() + "_" + field.getName();
                buildQueryCustomizers(entityClass,field,queryCustEntry);

                List<QueryCustomizerGenerator> queryCustomizers = queryCustomizerMap.get(queryCustEntry);
                if (queryCustomizers != null && !queryCustomizers.isEmpty()) {
                    QueryCustomizer.customizeField(queryCustomizers, descriptors.get(entityClass), field.getName());
                }
            }
        }

    }

    /**
     * Build and populate map of QueryCustomizer annotations
     * @param entityClass
     * @param field
     * @param key
     */
    protected void buildQueryCustomizers(Class<?> entityClass,Field field, String key){
        QueryCustomizerGenerators customizers = field.getAnnotation(QueryCustomizerGenerators.class);
        List<QueryCustomizerGenerator> queryCustomizerGenerators = new ArrayList<QueryCustomizerGenerator>();
        if(customizers != null){
            queryCustomizerGenerators.addAll(Arrays.asList(customizers.value()));
        } else {
            QueryCustomizerGenerator customizer = field.getAnnotation(QueryCustomizerGenerator.class);
            if(customizer != null){
                queryCustomizerGenerators.add(customizer);
            }
        }
        for(QueryCustomizerGenerator customizer : queryCustomizerGenerators){
            List<QueryCustomizerGenerator> queryCustomizers = queryCustomizerMap.get(key);
            if (queryCustomizers == null) {
                queryCustomizers =
                        new ArrayList<QueryCustomizerGenerator>();
                queryCustomizers.add(customizer);
                queryCustomizerMap.putIfAbsent(key, queryCustomizers);
            } else {
                queryCustomizers.add(customizer);
                queryCustomizerMap.put(key,queryCustomizers);
            }
        }
    }

    /**
     * Determines if the class descriptors have been modified for the given session name.
     *
     * @param session the current session.
     */
    protected void handleDescriptorModifications(Session session) {
        String sessionName = session.getName();

        // double-checked locking on ConcurrentMap
        Boolean descModified = modDescMap.get(sessionName);
        if (descModified == null) {
            descModified = modDescMap.putIfAbsent(sessionName, Boolean.FALSE);
            if (descModified == null) {
                descModified = modDescMap.get(sessionName);
            }
        }

        if (Boolean.FALSE.equals(descModified)) {
            modDescMap.put(sessionName, Boolean.TRUE);
            handleDisableVersioning(session);
            handleRemoveMapping(session);
        }
    }

    /**
     * Checks class descriptors for {@link @DisableVersioning} annotations at the class level and removes the version
     * database mapping for optimistic locking.
     *
     * @param session the current session
     */
    protected void handleDisableVersioning(Session session) {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();

        if (descriptors == null || descriptors.isEmpty()) {
            return;
        }

        for (ClassDescriptor classDescriptor : descriptors.values()) {
            if (classDescriptor != null && AnnotationUtils.findAnnotation(classDescriptor.getJavaClass(),
                    DisableVersioning.class) != null) {
                OptimisticLockingPolicy olPolicy = classDescriptor.getOptimisticLockingPolicy();
                if (olPolicy != null) {
                    classDescriptor.setOptimisticLockingPolicy(null);
                }
            }
        }
    }

    /**
     * Checks class descriptors for {@link @RemoveMapping} and {@link RemoveMappings} annotations at the class level
     * and
     * removes any specified mappings from the ClassDescriptor.
     *
     * @param session the current session
     */
    protected void handleRemoveMapping(Session session) {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();

        if (descriptors == null || descriptors.isEmpty()) {
            return;
        }

        for (ClassDescriptor classDescriptor : descriptors.values()) {
            List<RemoveMapping> removeMappings = scanForRemoveMappings(classDescriptor);
            if (!removeMappings.isEmpty()) {
                List<DatabaseMapping> mappingsToRemove = new ArrayList<DatabaseMapping>();
                for (RemoveMapping removeMapping : removeMappings) {
                    if (StringUtils.isBlank(removeMapping.name())) {
                        throw DescriptorException.attributeNameNotSpecified();
                    }
                    DatabaseMapping databaseMapping = classDescriptor.getMappingForAttributeName(removeMapping.name());
                    if (databaseMapping == null) {
                        throw DescriptorException.mappingForAttributeIsMissing(removeMapping.name(), classDescriptor);
                    }
                    mappingsToRemove.add(databaseMapping);
                }
                for (DatabaseMapping mappingToRemove : mappingsToRemove) {
                    classDescriptor.removeMappingForAttributeName(mappingToRemove.getAttributeName());
                }
            }
        }
    }

    protected List<RemoveMapping> scanForRemoveMappings(ClassDescriptor classDescriptor) {
        List<RemoveMapping> removeMappings = new ArrayList<RemoveMapping>();
        RemoveMappings removeMappingsAnnotation = AnnotationUtils.findAnnotation(classDescriptor.getJavaClass(),
                RemoveMappings.class);
        if (removeMappingsAnnotation == null) {
            RemoveMapping removeMappingAnnotation = AnnotationUtils.findAnnotation(classDescriptor.getJavaClass(),
                    RemoveMapping.class);
            if (removeMappingAnnotation != null) {
                removeMappings.add(removeMappingAnnotation);
            }
        } else {
            for (RemoveMapping removeMapping : removeMappingsAnnotation.value()) {
                removeMappings.add(removeMapping);
            }
        }
        return removeMappings;
    }

    protected List<Sequence> loadSequences(Session session) {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
        List<PortableSequenceGenerator> sequenceGenerators = new ArrayList<PortableSequenceGenerator>();
        for (Class<?> entityClass : descriptors.keySet()) {
            PortableSequenceGenerator sequenceGenerator = AnnotationUtils.findAnnotation(entityClass,
                    PortableSequenceGenerator.class);
            if (sequenceGenerator != null) {
                sequenceGenerators.add(sequenceGenerator);
            }
            loadFieldSequences(entityClass, sequenceGenerators);
            for (Method method : entityClass.getMethods()) {
                PortableSequenceGenerator methodSequenceGenerator = method.getAnnotation(
                        PortableSequenceGenerator.class);
                if (methodSequenceGenerator != null) {
                    sequenceGenerators.add(methodSequenceGenerator);
                }
            }
        }
        List<Sequence> sequences = new ArrayList<Sequence>();
        for (PortableSequenceGenerator sequenceGenerator : sequenceGenerators) {
            Sequence sequence = new MaxValueIncrementerSequenceWrapper(sequenceGenerator);
            sequences.add(sequence);
        }
        return sequences;
    }

    protected void loadFieldSequences(Class<?> entityClass, List<PortableSequenceGenerator> sequenceGenerators) {
        for (Field field : entityClass.getDeclaredFields()) {
            PortableSequenceGenerator fieldSequenceGenerator = field.getAnnotation(PortableSequenceGenerator.class);
            if (fieldSequenceGenerator != null) {
                sequenceGenerators.add(fieldSequenceGenerator);
            }
        }
        // next, walk up and check the super class...
        if (entityClass.getSuperclass() != null) {
            loadFieldSequences(entityClass.getSuperclass(), sequenceGenerators);
        }
    }

    private static final class MaxValueIncrementerSequenceWrapper extends Sequence {

        private static final long serialVersionUID = 2375805962996574386L;

        private final String sequenceName;

        MaxValueIncrementerSequenceWrapper(PortableSequenceGenerator sequenceGenerator) {
            super(sequenceGenerator.name(), 0);
            // default sequenceName to the name of the sequence generator if the sequence name was not provided
            if (StringUtils.isBlank(sequenceGenerator.sequenceName())) {
                sequenceName = sequenceGenerator.name();
            } else {
                sequenceName = sequenceGenerator.sequenceName();
            }
        }

        @Override
        public boolean shouldAcquireValueAfterInsert() {
            return false;
        }

        @Override
        public boolean shouldUseTransaction() {
            return true;
        }

        @Override
        public boolean shouldUsePreallocation() {
            return false;
        }

        @Override
        public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
            DataSource dataSource = ((JNDIConnector) writeSession.getLogin().getConnector()).getDataSource();
            DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(dataSource,
                    sequenceName);
            return Long.valueOf(incrementer.nextLongValue());
        }

        @Override
        public Vector<?> getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
            // we're not in the business of pre-fetching/allocating ids
            throw new UnsupportedOperationException(getClass().getName() + " does pre-generate sequence ids");
        }

        @Override
        public void onConnect() {}

        @Override
        public void onDisconnect() {}

        @Override
        public MaxValueIncrementerSequenceWrapper clone() {
            return (MaxValueIncrementerSequenceWrapper) super.clone();
        }

    }

}
