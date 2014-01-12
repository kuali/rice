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
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.Node;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.Collection;

public class MappedSuperClassResolver implements AnnotationResolver {
    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "MappedSuperclass";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public MappedSuperClassResolver(Collection<DescriptorRepository> descriptorRepositories) {
        this.descriptorRepositories = descriptorRepositories;
    }

    @Override
    public String getFullyQualifiedName() {
        return PACKAGE + "." + SIMPLE_NAME;
    }

    @Override
    public Level getLevel() {
        return Level.CLASS;
    }

    @Override
    public NodeData resolve(Node node, String mappedClass) {
        if (!(node instanceof ClassOrInterfaceDeclaration)) {
            throw new IllegalArgumentException("this annotation belongs only on ClassOrInterfaceDeclaration");
        }

        final TypeDeclaration dclr = (TypeDeclaration) node;
        if (!(dclr.getParentNode() instanceof CompilationUnit)) {
            //handling nested classes
            return null;
        }

        final String name = dclr.getName();
        final String pckg = ((CompilationUnit) dclr.getParentNode()).getPackage().getName().toString();
        final String enclosingClass = pckg + "." + name;
        if (!enclosingClass.equals(mappedClass)) {
            return new NodeData(new MarkerAnnotationExpr(new NameExpr(SIMPLE_NAME)),
                new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false));
        }
        return null;
    }
}
