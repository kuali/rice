package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper;

import japa.parser.ast.Node;

public interface AnnotationResolver {
    String getFullyQualifiedName();
    Level getLevel();
    NodeData resolve(Node node, String arg);
}