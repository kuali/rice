package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper;

import japa.parser.ast.Node;
import org.apache.ojb.broker.metadata.DescriptorRepository;

import java.util.Collection;

public interface AnnotationResolver {
    String getFullyQualifiedName();
    Level getLevel();
    NodeData resolve(Node node, Object arg);
}