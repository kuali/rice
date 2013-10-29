package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.visitor;

import japa.parser.ast.visitor.VoidVisitorAdapter;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.OjbDescriptorRepositoryAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class OjbDescriptorRepositoryAwareVisitor extends VoidVisitorAdapter implements OjbDescriptorRepositoryAware {

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
