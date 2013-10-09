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
package org.kuali.rice.krad.data.jpa.eclipselink;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
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

    private static ConcurrentMap<String, List<Sequence>> sequenceMap =
            new ConcurrentHashMap<String, List<Sequence>>(8, 0.9f, 1);

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

        DatabaseLogin login = session.getLogin();
        for (Sequence sequence : sequences) {
            login.addSequence(sequence);
        }
    }

    protected List<Sequence> loadSequences(Session session) {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
        List<PortableSequenceGenerator> sequenceGenerators = new ArrayList<PortableSequenceGenerator>();
        for (Class<?> entityClass : descriptors.keySet()) {
            PortableSequenceGenerator sequenceGenerator =
                    AnnotationUtils.findAnnotation(entityClass, PortableSequenceGenerator.class);
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
