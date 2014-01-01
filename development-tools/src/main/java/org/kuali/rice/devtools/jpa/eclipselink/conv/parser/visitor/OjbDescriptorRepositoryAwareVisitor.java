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
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor;

import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.OjbDescriptorRepositoryAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class OjbDescriptorRepositoryAwareVisitor extends VoidVisitorAdapter<String> implements OjbDescriptorRepositoryAware {

    private final Collection<DescriptorRepository> descriptorRepositories;

    public OjbDescriptorRepositoryAwareVisitor(Collection<DescriptorRepository> descriptorRepositories) {
        if (descriptorRepositories == null || descriptorRepositories.isEmpty()) {
            throw new IllegalArgumentException("descriptorRepositories cannot be null");
        }

        this.descriptorRepositories = Collections.unmodifiableCollection(new ArrayList<DescriptorRepository>(descriptorRepositories));
    }

    @Override
    public Collection<DescriptorRepository> getDescriptorRepositories() {
        return this.descriptorRepositories;
    }
}
