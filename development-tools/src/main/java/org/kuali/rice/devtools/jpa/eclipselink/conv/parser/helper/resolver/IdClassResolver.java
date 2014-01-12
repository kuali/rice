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
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.Type;
import japa.parser.ast.type.VoidType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.rice.devtools.jpa.eclipselink.conv.ojb.OjbUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.ParserUtil;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.AnnotationResolver;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.Level;
import org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.NodeData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class IdClassResolver implements AnnotationResolver {
    private static final Log LOG = LogFactory.getLog(IdClassResolver.class);

    public static final String PACKAGE = "javax.persistence";
    public static final String SIMPLE_NAME = "IdClass";

    private final Collection<DescriptorRepository> descriptorRepositories;

    public IdClassResolver(Collection<DescriptorRepository> descriptorRepositories) {
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

        final Collection<FieldDescriptor> primaryKeyDescriptors = getPrimaryKeyDescriptors(mappedClass);

        if (primaryKeyDescriptors != null && primaryKeyDescriptors.size() > 1  && nodeContainsPkFields(dclr,
                primaryKeyDescriptors)) {
            final NodeAndImports<ClassOrInterfaceDeclaration> primaryKeyClass = createPrimaryKeyClass(name, primaryKeyDescriptors);
            final String pkClassName = primaryKeyClass.node.getName();
            return new NodeData(new SingleMemberAnnotationExpr(new NameExpr(SIMPLE_NAME), new NameExpr(name + "." + pkClassName + ".class")),
                    new ImportDeclaration(new QualifiedNameExpr(new NameExpr(PACKAGE), SIMPLE_NAME), false, false), primaryKeyClass.imprts, primaryKeyClass.node);

        }
        return null;
    }

    private boolean nodeContainsPkFields(TypeDeclaration dclr, Collection<FieldDescriptor> pks) {
        for (FieldDescriptor pk : pks) {
            boolean contains = false;
            for (FieldDeclaration field : ParserUtil.getFieldMembers(dclr.getMembers())) {
                if (field.getVariables().get(0).getId().getName().equals(pk.getAttributeName())) {
                    contains =  true;
                    break;
                }
            }

            if (!contains) {
                return false;
            }
        }

        return true;
    }

    private Collection<FieldDescriptor> getPrimaryKeyDescriptors(String clazz) {
        final Collection<FieldDescriptor> pks = new ArrayList<FieldDescriptor>();

        final ClassDescriptor cd = OjbUtil.findClassDescriptor(clazz, descriptorRepositories);
        if (cd != null) {
            //This causes a stackoverflow and appears to not work correctly
            //return cd.getPkFields().length > 1;
            int i = 0;
            FieldDescriptor[] fds = cd.getFieldDescriptions();
            if (fds != null) {
                for (FieldDescriptor fd : fds) {
                    if (fd.isPrimaryKey()) {
                        pks.add(fd);
                    }
                }
            }
        }
        return pks;
    }

    private NodeAndImports<ClassOrInterfaceDeclaration> createPrimaryKeyClass(String parentName, Collection<FieldDescriptor> primaryKeyDescriptors) {
        final String newName = parentName + "Id";
        final Collection<ImportDeclaration> requiredImports = new ArrayList<ImportDeclaration>();
        final ClassOrInterfaceDeclaration dclr = new ClassOrInterfaceDeclaration(ModifierSet.PUBLIC | ModifierSet.STATIC | ModifierSet.FINAL, false, newName);
        dclr.setInterface(false);
        final List<ClassOrInterfaceType> implmnts = new ArrayList<ClassOrInterfaceType>();
        implmnts.add(new ClassOrInterfaceType("Serializable"));
        final ClassOrInterfaceType comparableImplmnts = new ClassOrInterfaceType("Comparable");
        comparableImplmnts.setTypeArgs(Collections.<Type>singletonList(new ClassOrInterfaceType(newName)));
        implmnts.add(comparableImplmnts);

        dclr.setImplements(implmnts);
        requiredImports.add(new ImportDeclaration(new QualifiedNameExpr(new NameExpr("java.io"), "Serializable"), false, false));
        final List<BodyDeclaration> members = new ArrayList<BodyDeclaration>();

        for (FieldDescriptor fd : primaryKeyDescriptors) {
            final String simpleTypeName = ResolverUtil.getType(fd.getClassDescriptor().getClassNameOfObject(),
                    fd.getAttributeName()).getSimpleName();
            final String attrName = fd.getAttributeName();

            members.add(new FieldDeclaration(ModifierSet.PRIVATE, new ClassOrInterfaceType(simpleTypeName), new VariableDeclarator(new VariableDeclaratorId(attrName))));
        }

        for (FieldDescriptor fd : primaryKeyDescriptors) {
            final String simpleTypeName = ResolverUtil.getType(fd.getClassDescriptor().getClassNameOfObject(),
                    fd.getAttributeName()).getSimpleName();
            final String attrName = fd.getAttributeName();
            final MethodDeclaration getter = new MethodDeclaration(ModifierSet.PUBLIC, new ClassOrInterfaceType(simpleTypeName), "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1));
            getter.setBody(new BlockStmt(Collections.<Statement>singletonList(new ReturnStmt(new FieldAccessExpr(new ThisExpr(), attrName)))));
            members.add(getter);

            final MethodDeclaration setter = new MethodDeclaration(ModifierSet.PUBLIC, new VoidType(), "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1),
                    Collections.singletonList(new Parameter(new ClassOrInterfaceType(simpleTypeName), new VariableDeclaratorId(attrName))));

            setter.setBody(new BlockStmt(Collections.<Statement>singletonList(new ExpressionStmt(
                    new AssignExpr(new FieldAccessExpr(new ThisExpr(), attrName), new NameExpr(attrName), AssignExpr.Operator.assign)))));
            members.add(setter);
        }

        final NodeAndImports<MethodDeclaration> toString = createPrimaryKeyToString(primaryKeyDescriptors);
        final NodeAndImports<MethodDeclaration> equals = createPrimaryKeyEquals(primaryKeyDescriptors, newName);
        final NodeAndImports<MethodDeclaration> hashCode = createPrimaryKeyHashCode(primaryKeyDescriptors);
        final NodeAndImports<MethodDeclaration> compareTo = createPrimaryKeyCompareTo(primaryKeyDescriptors, newName);

        members.add(toString.node);
        members.add(equals.node);
        members.add(hashCode.node);
        members.add(compareTo.node);

        if (toString.imprts != null) {
            requiredImports.addAll(toString.imprts);
        }

        if (equals.imprts != null) {
            requiredImports.addAll(equals.imprts);
        }

        if (hashCode.imprts != null) {
            requiredImports.addAll(hashCode.imprts);
        }

        if (compareTo.imprts != null) {
            requiredImports.addAll(compareTo.imprts);
        }

        dclr.setMembers(members);

        return new NodeAndImports<ClassOrInterfaceDeclaration>(dclr, requiredImports);
    }

    private NodeAndImports<MethodDeclaration> createPrimaryKeyToString(Collection<FieldDescriptor> primaryKeyDescriptors) {
        final MethodDeclaration toString = new MethodDeclaration(ModifierSet.PUBLIC, new ClassOrInterfaceType("String"), "toString");
        toString.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));
        Expression toStringBuilderExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType("ToStringBuilder"), Collections.<Expression>singletonList(new ThisExpr()));
        for (FieldDescriptor f : primaryKeyDescriptors) {
            final List<Expression> args = new ArrayList<Expression>();
            args.add(new StringLiteralExpr(f.getAttributeName()));
            args.add(new FieldAccessExpr(new ThisExpr(), f.getAttributeName()));
            toStringBuilderExpr = new MethodCallExpr(toStringBuilderExpr, "append", args);
        }
        toStringBuilderExpr = new MethodCallExpr(toStringBuilderExpr, "toString");
        final BlockStmt toStringBody = new BlockStmt(Collections.<Statement>singletonList(new ReturnStmt(toStringBuilderExpr)));
        toString.setBody(toStringBody);

        return new NodeAndImports<MethodDeclaration>(toString,
                Collections.singleton(new ImportDeclaration(new QualifiedNameExpr(new NameExpr("org.apache.commons.lang.builder"), "ToStringBuilder"), false, false)));
    }

    private NodeAndImports<MethodDeclaration> createPrimaryKeyEquals(Collection<FieldDescriptor> primaryKeyDescriptors, String enclosingClassName) {
        final MethodDeclaration equals = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(PrimitiveType.Primitive.Boolean), "equals",
                Collections.singletonList(new Parameter(new ClassOrInterfaceType("Object"), new VariableDeclaratorId("other"))));
        equals.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));

        final Statement ifEqualNullStmt = new IfStmt(new BinaryExpr(new NameExpr("other"), new NullLiteralExpr(), BinaryExpr.Operator.equals), new ReturnStmt(new BooleanLiteralExpr(false)), null);
        final Statement ifEqualThisStmt = new IfStmt(new BinaryExpr(new NameExpr("other"), new ThisExpr(), BinaryExpr.Operator.equals), new ReturnStmt(new BooleanLiteralExpr(true)), null);
        final Statement ifEqualClassStmt = new IfStmt(new BinaryExpr(new MethodCallExpr(new NameExpr("other"), "getClass"), new MethodCallExpr(new ThisExpr(), "getClass"), BinaryExpr.Operator.notEquals), new ReturnStmt(new BooleanLiteralExpr(false)), null);
        final Statement rhsStmt = new ExpressionStmt(new VariableDeclarationExpr(ModifierSet.FINAL,
                new ClassOrInterfaceType(enclosingClassName), Collections.singletonList(new VariableDeclarator(
                new VariableDeclaratorId("rhs"),
                new CastExpr(new ClassOrInterfaceType(enclosingClassName), new NameExpr("other"))))));

        Expression equalsBuilderExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType("EqualsBuilder"), Collections.<Expression>emptyList());

        for (FieldDescriptor f : primaryKeyDescriptors) {
            final List<Expression> args = new ArrayList<Expression>();
            args.add(new FieldAccessExpr(new ThisExpr(), f.getAttributeName()));
            args.add(new FieldAccessExpr(new NameExpr("rhs"), f.getAttributeName()));
            equalsBuilderExpr = new MethodCallExpr(equalsBuilderExpr, "append", args);
        }

        equalsBuilderExpr = new MethodCallExpr(equalsBuilderExpr, "isEquals");
        final List<Statement> statements = new ArrayList<Statement>();
        statements.add(ifEqualNullStmt);
        statements.add(ifEqualThisStmt);
        statements.add(ifEqualClassStmt);
        statements.add(rhsStmt);
        statements.add(new ReturnStmt(equalsBuilderExpr));
        final BlockStmt equalsBody = new BlockStmt(statements);
        equals.setBody(equalsBody);

        return new NodeAndImports<MethodDeclaration>(equals,
                Collections.singleton(new ImportDeclaration(new QualifiedNameExpr(new NameExpr("org.apache.commons.lang.builder"), "EqualsBuilder"), false, false)));
    }

    private NodeAndImports<MethodDeclaration> createPrimaryKeyHashCode(Collection<FieldDescriptor> primaryKeyDescriptors) {
        final MethodDeclaration hashCode = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(PrimitiveType.Primitive.Int), "hashCode");
        hashCode.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));
        final List<Expression> ctorArgs = new ArrayList<Expression>();
        ctorArgs.add(new IntegerLiteralExpr("17"));
        ctorArgs.add(new IntegerLiteralExpr("37"));
        Expression hashCodeExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType("HashCodeBuilder"), ctorArgs);

        for (FieldDescriptor f : primaryKeyDescriptors) {
            final List<Expression> args = new ArrayList<Expression>();
            args.add(new FieldAccessExpr(new ThisExpr(), f.getAttributeName()));
            hashCodeExpr = new MethodCallExpr(hashCodeExpr, "append", args);
        }

        hashCodeExpr = new MethodCallExpr(hashCodeExpr, "toHashCode");
        final BlockStmt equalsBody = new BlockStmt(Collections.<Statement>singletonList(new ReturnStmt(hashCodeExpr)));
        hashCode.setBody(equalsBody);

        return new NodeAndImports<MethodDeclaration>(hashCode,
                Collections.singleton(new ImportDeclaration(new QualifiedNameExpr(new NameExpr("org.apache.commons.lang.builder"), "HashCodeBuilder"), false, false)));
    }

    private NodeAndImports<MethodDeclaration> createPrimaryKeyCompareTo(Collection<FieldDescriptor> primaryKeyDescriptors, String enclosingClassName) {
        final MethodDeclaration compareTo = new MethodDeclaration(ModifierSet.PUBLIC, new PrimitiveType(PrimitiveType.Primitive.Int), "compareTo",
                Collections.singletonList(new Parameter(new ClassOrInterfaceType(enclosingClassName), new VariableDeclaratorId("other"))));
        compareTo.setAnnotations(Collections.<AnnotationExpr>singletonList(new MarkerAnnotationExpr(new NameExpr("Override"))));

        Expression compareToBuilderExpr = new ObjectCreationExpr(null, new ClassOrInterfaceType("CompareToBuilder"), Collections.<Expression>emptyList());

        for (FieldDescriptor f : primaryKeyDescriptors) {
            final List<Expression> args = new ArrayList<Expression>();
            args.add(new FieldAccessExpr(new ThisExpr(), f.getAttributeName()));
            args.add(new FieldAccessExpr(new NameExpr("other"), f.getAttributeName()));
            compareToBuilderExpr = new MethodCallExpr(compareToBuilderExpr, "append", args);
        }

        compareToBuilderExpr = new MethodCallExpr(compareToBuilderExpr, "toComparison");
        final List<Statement> statements = new ArrayList<Statement>();
        statements.add(new ReturnStmt(compareToBuilderExpr));
        final BlockStmt equalsBody = new BlockStmt(statements);
        compareTo.setBody(equalsBody);

        return new NodeAndImports<MethodDeclaration>(compareTo,
                Collections.singleton(new ImportDeclaration(new QualifiedNameExpr(new NameExpr("org.apache.commons.lang.builder"), "CompareToBuilder"), false, false)));
    }

    private class NodeAndImports<T extends Node> {
        T node;
        Collection<ImportDeclaration> imprts;

        NodeAndImports(T node, Collection<ImportDeclaration> imprts) {
            this.node = node;
            this.imprts = imprts;
        }
    }
}
