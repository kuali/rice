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
package org.kuali.rice.krad.data.provider.jpa.eclipselink;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;
import org.kuali.rice.krad.data.platform.generator.DatabasePlatformIdGenerator;
import org.kuali.rice.krad.data.platform.generator.IdGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;

public class KradEclipseLinkCustomizer implements SessionCustomizer {

    public KradEclipseLinkCustomizer() {
        System.out.println("Creating new instance of customizer");
    }

    @Override
    public void customize(Session session) throws Exception {
        Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
        for (Class<?> entityClass : descriptors.keySet()) {
            PortableSequenceGenerator sequenceGenerator =
                    AnnotationUtils.findAnnotation(entityClass, PortableSequenceGenerator.class);
            // TODO need to also check for the annotation at the field/method level as well
            // TODO also need to be caching this stuff on the class probably
            if (sequenceGenerator != null) {
                // TODO still lots to do here, need to consider the other parameters on the generator as well,
                // also if we are using a database platform that supports native sequences, can probably just
                // use the built-in NativeSequence class?
                //
                // to make the check: session.getPlatform().supportsNativeSequenceNumbers()
                IdGenerator generator = new DatabasePlatformIdGenerator(sequenceGenerator.name());
                org.eclipse.persistence.sequencing.Sequence sequence = new IdGeneratorSequenceWrapper(generator);
                session.getLogin().addSequence(sequence);
            }
        }
    }

    /**
     * An EclipseLink {@link org.eclipse.persistence.sequencing.Sequence} which wraps the Rice IdGenerator
     */
    private static class IdGeneratorSequenceWrapper extends org.eclipse.persistence.sequencing.Sequence {
        /**
         * The IdGenerator this Sequence defers to
         */
        private IdGenerator generator;

        private IdGeneratorSequenceWrapper(IdGenerator generator) {
            // be sure to assign an initial size of 0, otherwise base implementation
            // attempts to pre-fetch ids
            super(generator.getName(), 0);
            this.generator = generator;
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
            // invoke the IdGenerator with the Connection
            // Invocation of EntityManager here is:
            // 1) not practically possible without caching it on a ThreadLocal upstream
            // 2) does not work anyway because the EM will attempt to pre-emptively flush before querying
            //    which is exactly what we don't want to happen in the case we are obtaining an id for a new object
            return new JdbcTemplate(((JNDIConnector) writeSession.getLogin().getConnector()).getDataSource()).execute(new ConnectionCallback<Object>() {
                @Override
                public Object doInConnection(Connection con) throws SQLException, DataAccessException {
                    return generator.getNextValue(con);
                }
            });
        }

        @Override
        public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
            // we're not in the business of pre-fetching/allocating ids
            throw new UnsupportedOperationException("IdGeneratorSequenceWrapper cannot pre-generate sequence ids");
        }
        @Override
        public void onConnect() {}
        @Override
        public void onDisconnect() {}
    }

}
