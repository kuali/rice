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
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.expr.AnnotationExpr;

import java.util.Collection;

public class NodeData {
    final AnnotationExpr annotation;
    final ImportDeclaration annotationImport;
    final Collection<ImportDeclaration> additionalImports;
    final ClassOrInterfaceDeclaration nestedDeclaration;


    public NodeData(AnnotationExpr annotation, ImportDeclaration annotationImport) {
        this(annotation, annotationImport, null, null);
    }

    public NodeData(AnnotationExpr annotation, ImportDeclaration annotationImport, Collection<ImportDeclaration> additionalImports) {
        this(annotation, annotationImport, additionalImports, null);
    }

    public NodeData(AnnotationExpr annotation, ImportDeclaration annotationImport, ClassOrInterfaceDeclaration nestedDeclaration) {
        this(annotation, annotationImport, null, nestedDeclaration);
    }

    public NodeData(AnnotationExpr annotation, ImportDeclaration annotationImport, Collection<ImportDeclaration> additionalImports, ClassOrInterfaceDeclaration nestedDeclaration) {
        this.annotation = annotation;
        this.annotationImport = annotationImport;
        this.additionalImports = additionalImports;
        this.nestedDeclaration = nestedDeclaration;
    }
}