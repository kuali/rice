package org.kuali.rice.devtools.jpa.eclipselink.conv.parser;

import org.apache.ojb.broker.metadata.DescriptorRepository;

import java.util.Collection;

public interface OjbDescriptorRepositoryAware {
    Collection<DescriptorRepository> getDescriptorRepositories();
}
