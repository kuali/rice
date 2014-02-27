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
package org.kuali.rice.core.framework.util.spring;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.instrument.classloading.SimpleThrowawayClassLoader;

import java.lang.instrument.ClassFileTransformer;

/**
 * A LoadTimeWeaver which will attempt to identify an available LoadTimeWeaver using the
 * {@link DefaultContextLoadTimeWeaver}, falling back to a no-op LoadTimeWeaver if none is found.
 *
 * <p>The no-op weaver essentially doesn't execute any class transformers. This allows for us to still have a handle on
 * a LoadTimeWeaver which we can inject into other beans, but can still run the application without having to
 * specify an instrumentable classloader or an agent.</p>
 *
 * <p>It is important to only use this in cases where the load-time weaving actually *is* optional! A valid example of
 * this would be an application which is using static weaving at build-time but falling back to load-time weaving for
 * development purposes (since the IDE itself can't do the weaving for us when it compiles). This is what the Kuali Rice
 * project does currently. Having this optional weaver allows this configuration to be maintained but allows a Kuali
 * Rice client appliction to rely on the static weaving that is provided without being required to also configure and
 * enable load-time weaving.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class OptionalContextLoadTimeWeaver implements LoadTimeWeaver, BeanClassLoaderAware, DisposableBean {

    private DefaultContextLoadTimeWeaver loadTimeWeaver;
    private boolean loadTimeWeaverLoaded;
    private ClassLoader beanClassLoader;

    public OptionalContextLoadTimeWeaver() {
        this.loadTimeWeaver = new DefaultContextLoadTimeWeaver();
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        try {
            this.beanClassLoader = beanClassLoader;
            this.loadTimeWeaver.setBeanClassLoader(beanClassLoader);
            loadTimeWeaverLoaded = true;
        } catch (IllegalStateException e) {
            // this would happen in the default weaver class if no load-time weaver could be determined, in our case we
            // want to make the LTW optional, so we will ignore this and allow the LTW to be null
        }
    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {
        if (loadTimeWeaverLoaded) {
            this.loadTimeWeaver.addTransformer(transformer);
        }
    }

    @Override
    public ClassLoader getInstrumentableClassLoader() {
        if (loadTimeWeaverLoaded) {
            return this.loadTimeWeaver.getInstrumentableClassLoader();
        } else {
            return beanClassLoader;
        }
    }

    @Override
    public ClassLoader getThrowawayClassLoader() {
        if (loadTimeWeaverLoaded) {
            return this.loadTimeWeaver.getThrowawayClassLoader();
        } else {
            return new SimpleThrowawayClassLoader(getInstrumentableClassLoader());
        }
    }

    @Override
    public void destroy() throws Exception {
        if (loadTimeWeaverLoaded) {
            this.loadTimeWeaver.destroy();
        }
    }
}
