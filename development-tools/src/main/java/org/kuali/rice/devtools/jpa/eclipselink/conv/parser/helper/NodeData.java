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