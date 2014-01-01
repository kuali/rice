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

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;

public class VoidVisitorHelperBase<A> implements VoidVisitorHelper<A> {
    @Override
    public void visitPre(CompilationUnit n, A arg) {
        
    }

    @Override
    public void visitPre(PackageDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(ImportDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(TypeParameter n, A arg) {
        
    }

    @Override
    public void visitPre(LineComment n, A arg) {
        
    }

    @Override
    public void visitPre(BlockComment n, A arg) {
        
    }

    @Override
    public void visitPre(ClassOrInterfaceDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(EnumDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(EmptyTypeDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(EnumConstantDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(AnnotationDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(AnnotationMemberDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(FieldDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(VariableDeclarator n, A arg) {
        
    }

    @Override
    public void visitPre(VariableDeclaratorId n, A arg) {
        
    }

    @Override
    public void visitPre(ConstructorDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(MethodDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(Parameter n, A arg) {
        
    }

    @Override
    public void visitPre(EmptyMemberDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(InitializerDeclaration n, A arg) {
        
    }

    @Override
    public void visitPre(JavadocComment n, A arg) {
        
    }

    @Override
    public void visitPre(ClassOrInterfaceType n, A arg) {
        
    }

    @Override
    public void visitPre(PrimitiveType n, A arg) {
        
    }

    @Override
    public void visitPre(ReferenceType n, A arg) {
        
    }

    @Override
    public void visitPre(VoidType n, A arg) {
        
    }

    @Override
    public void visitPre(WildcardType n, A arg) {
        
    }

    @Override
    public void visitPre(ArrayAccessExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ArrayCreationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ArrayInitializerExpr n, A arg) {
        
    }

    @Override
    public void visitPre(AssignExpr n, A arg) {
        
    }

    @Override
    public void visitPre(BinaryExpr n, A arg) {
        
    }

    @Override
    public void visitPre(CastExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ClassExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ConditionalExpr n, A arg) {
        
    }

    @Override
    public void visitPre(EnclosedExpr n, A arg) {
        
    }

    @Override
    public void visitPre(FieldAccessExpr n, A arg) {
        
    }

    @Override
    public void visitPre(InstanceOfExpr n, A arg) {
        
    }

    @Override
    public void visitPre(StringLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(IntegerLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(LongLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(IntegerLiteralMinValueExpr n, A arg) {
        
    }

    @Override
    public void visitPre(LongLiteralMinValueExpr n, A arg) {
        
    }

    @Override
    public void visitPre(CharLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(DoubleLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(BooleanLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(NullLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPre(MethodCallExpr n, A arg) {
        
    }

    @Override
    public void visitPre(NameExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ObjectCreationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(QualifiedNameExpr n, A arg) {
        
    }

    @Override
    public void visitPre(ThisExpr n, A arg) {
        
    }

    @Override
    public void visitPre(SuperExpr n, A arg) {
        
    }

    @Override
    public void visitPre(UnaryExpr n, A arg) {
        
    }

    @Override
    public void visitPre(VariableDeclarationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(MarkerAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(SingleMemberAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(NormalAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPre(MemberValuePair n, A arg) {
        
    }

    @Override
    public void visitPre(ExplicitConstructorInvocationStmt n, A arg) {
        
    }

    @Override
    public void visitPre(TypeDeclarationStmt n, A arg) {
        
    }

    @Override
    public void visitPre(AssertStmt n, A arg) {
        
    }

    @Override
    public void visitPre(BlockStmt n, A arg) {
        
    }

    @Override
    public void visitPre(LabeledStmt n, A arg) {
        
    }

    @Override
    public void visitPre(EmptyStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ExpressionStmt n, A arg) {
        
    }

    @Override
    public void visitPre(SwitchStmt n, A arg) {
        
    }

    @Override
    public void visitPre(SwitchEntryStmt n, A arg) {
        
    }

    @Override
    public void visitPre(BreakStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ReturnStmt n, A arg) {
        
    }

    @Override
    public void visitPre(IfStmt n, A arg) {
        
    }

    @Override
    public void visitPre(WhileStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ContinueStmt n, A arg) {
        
    }

    @Override
    public void visitPre(DoStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ForeachStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ForStmt n, A arg) {
        
    }

    @Override
    public void visitPre(ThrowStmt n, A arg) {
        
    }

    @Override
    public void visitPre(SynchronizedStmt n, A arg) {
        
    }

    @Override
    public void visitPre(TryStmt n, A arg) {
        
    }

    @Override
    public void visitPre(CatchClause n, A arg) {
        
    }

    @Override
    public void visitPost(CompilationUnit n, A arg) {
        
    }

    @Override
    public void visitPost(PackageDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(ImportDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(TypeParameter n, A arg) {
        
    }

    @Override
    public void visitPost(LineComment n, A arg) {
        
    }

    @Override
    public void visitPost(BlockComment n, A arg) {
        
    }

    @Override
    public void visitPost(ClassOrInterfaceDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(EnumDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(EmptyTypeDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(EnumConstantDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(AnnotationDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(AnnotationMemberDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(FieldDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(VariableDeclarator n, A arg) {
        
    }

    @Override
    public void visitPost(VariableDeclaratorId n, A arg) {
        
    }

    @Override
    public void visitPost(ConstructorDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(MethodDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(Parameter n, A arg) {
        
    }

    @Override
    public void visitPost(EmptyMemberDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(InitializerDeclaration n, A arg) {
        
    }

    @Override
    public void visitPost(JavadocComment n, A arg) {
        
    }

    @Override
    public void visitPost(ClassOrInterfaceType n, A arg) {
        
    }

    @Override
    public void visitPost(PrimitiveType n, A arg) {
        
    }

    @Override
    public void visitPost(ReferenceType n, A arg) {
        
    }

    @Override
    public void visitPost(VoidType n, A arg) {
        
    }

    @Override
    public void visitPost(WildcardType n, A arg) {
        
    }

    @Override
    public void visitPost(ArrayAccessExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ArrayCreationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ArrayInitializerExpr n, A arg) {
        
    }

    @Override
    public void visitPost(AssignExpr n, A arg) {
        
    }

    @Override
    public void visitPost(BinaryExpr n, A arg) {
        
    }

    @Override
    public void visitPost(CastExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ClassExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ConditionalExpr n, A arg) {
        
    }

    @Override
    public void visitPost(EnclosedExpr n, A arg) {
        
    }

    @Override
    public void visitPost(FieldAccessExpr n, A arg) {
        
    }

    @Override
    public void visitPost(InstanceOfExpr n, A arg) {
        
    }

    @Override
    public void visitPost(StringLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(IntegerLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(LongLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(IntegerLiteralMinValueExpr n, A arg) {
        
    }

    @Override
    public void visitPost(LongLiteralMinValueExpr n, A arg) {
        
    }

    @Override
    public void visitPost(CharLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(DoubleLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(BooleanLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(NullLiteralExpr n, A arg) {
        
    }

    @Override
    public void visitPost(MethodCallExpr n, A arg) {
        
    }

    @Override
    public void visitPost(NameExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ObjectCreationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(QualifiedNameExpr n, A arg) {
        
    }

    @Override
    public void visitPost(ThisExpr n, A arg) {
        
    }

    @Override
    public void visitPost(SuperExpr n, A arg) {
        
    }

    @Override
    public void visitPost(UnaryExpr n, A arg) {
        
    }

    @Override
    public void visitPost(VariableDeclarationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(MarkerAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(SingleMemberAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(NormalAnnotationExpr n, A arg) {
        
    }

    @Override
    public void visitPost(MemberValuePair n, A arg) {
        
    }

    @Override
    public void visitPost(ExplicitConstructorInvocationStmt n, A arg) {
        
    }

    @Override
    public void visitPost(TypeDeclarationStmt n, A arg) {
        
    }

    @Override
    public void visitPost(AssertStmt n, A arg) {
        
    }

    @Override
    public void visitPost(BlockStmt n, A arg) {
        
    }

    @Override
    public void visitPost(LabeledStmt n, A arg) {
        
    }

    @Override
    public void visitPost(EmptyStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ExpressionStmt n, A arg) {
        
    }

    @Override
    public void visitPost(SwitchStmt n, A arg) {
        
    }

    @Override
    public void visitPost(SwitchEntryStmt n, A arg) {
        
    }

    @Override
    public void visitPost(BreakStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ReturnStmt n, A arg) {
        
    }

    @Override
    public void visitPost(IfStmt n, A arg) {
        
    }

    @Override
    public void visitPost(WhileStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ContinueStmt n, A arg) {
        
    }

    @Override
    public void visitPost(DoStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ForeachStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ForStmt n, A arg) {
        
    }

    @Override
    public void visitPost(ThrowStmt n, A arg) {
        
    }

    @Override
    public void visitPost(SynchronizedStmt n, A arg) {
        
    }

    @Override
    public void visitPost(TryStmt n, A arg) {
        
    }

    @Override
    public void visitPost(CatchClause n, A arg) {
        
    }
}
