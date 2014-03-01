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
package org.kuali.rice.kew.impl;

import org.junit.Test;
import org.reflections.Reflections;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.Assert.fail;

/**
 * This test verifies that all of the JPA objects in this module are statically weaved.
 *
 * <p>If one executes this test from within an IDE environment, it very well may not pass if one of the JPA objects
 * under examination was modified and then recompiled by the IDE. The static weaving process is handled by Maven,
 * so without executing the appropriate Maven lifecycle phase, the class will not get weaved. Regardless, this test
 * should *always* pass when executed from the command line.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StaticWeavingTest {

    @Test
    public void testStaticWeaving() {
        // first, scan for all files on the classpath with an @Entity or @MappedSuperClass annotation
        Reflections reflections = new Reflections(getClass().getPackage().getName());
        Set<Class<?>> entityTypes = reflections.getTypesAnnotatedWith(Entity.class);
        Set<Class<?>> superTypes = reflections.getTypesAnnotatedWith(MappedSuperclass.class);
        Set<Class<?>> embeddableTypes = reflections.getTypesAnnotatedWith(Embeddable.class);

        // next, let's assert that they have been statically weaved
        assertStaticWeaved(entityTypes, superTypes, embeddableTypes);
    }

    private void assertStaticWeaved(Set<Class<?>>... types) {
        for (Set<Class<?>> typeSet : types) {
            for (Class<?> type : typeSet) {
                boolean foundWeaved = false;
                Method[] methods = type.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().startsWith("_persistence")) {
                        foundWeaved = true;
                        break;
                    }
                }
                if (!foundWeaved) {
                    fail("(NOTE: it is expected this test may fail if executed from the IDE instead of command line "
                            + "since the IDE will not execute the static weaving automatically). Found a class which is "
                            + "not bytecode weaved (contains no methods starting with '_persistence'): " + type + " "
                            + "In order to resolve this, please ensure that this type is included in "
                            + "META-INF/persistence-weaving.xml");
                }
            }
        }
    }

}
