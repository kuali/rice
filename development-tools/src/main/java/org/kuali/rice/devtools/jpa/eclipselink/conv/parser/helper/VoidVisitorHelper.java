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

public interface VoidVisitorHelper<A> {

    //- Pre Methods ---------------------------------------
    
	//- Compilation Unit ----------------------------------

	void visitPre(CompilationUnit n, A arg);

	void visitPre(PackageDeclaration n, A arg);

	void visitPre(ImportDeclaration n, A arg);

	void visitPre(TypeParameter n, A arg);

	void visitPre(LineComment n, A arg);

	void visitPre(BlockComment n, A arg);

	//- Body ----------------------------------------------

	void visitPre(ClassOrInterfaceDeclaration n, A arg);

	void visitPre(EnumDeclaration n, A arg);

	void visitPre(EmptyTypeDeclaration n, A arg);

	void visitPre(EnumConstantDeclaration n, A arg);

	void visitPre(AnnotationDeclaration n, A arg);

	void visitPre(AnnotationMemberDeclaration n, A arg);

	void visitPre(FieldDeclaration n, A arg);

	void visitPre(VariableDeclarator n, A arg);

	void visitPre(VariableDeclaratorId n, A arg);

	void visitPre(ConstructorDeclaration n, A arg);

	void visitPre(MethodDeclaration n, A arg);

	void visitPre(Parameter n, A arg);
	
	void visitPre(EmptyMemberDeclaration n, A arg);

	void visitPre(InitializerDeclaration n, A arg);

	void visitPre(JavadocComment n, A arg);

	//- Type ----------------------------------------------

	void visitPre(ClassOrInterfaceType n, A arg);

	void visitPre(PrimitiveType n, A arg);

	void visitPre(ReferenceType n, A arg);

	void visitPre(VoidType n, A arg);

	void visitPre(WildcardType n, A arg);

	//- Expression ----------------------------------------

	void visitPre(ArrayAccessExpr n, A arg);

	void visitPre(ArrayCreationExpr n, A arg);

	void visitPre(ArrayInitializerExpr n, A arg);

	void visitPre(AssignExpr n, A arg);

	void visitPre(BinaryExpr n, A arg);

	void visitPre(CastExpr n, A arg);

	void visitPre(ClassExpr n, A arg);

	void visitPre(ConditionalExpr n, A arg);

	void visitPre(EnclosedExpr n, A arg);

	void visitPre(FieldAccessExpr n, A arg);

	void visitPre(InstanceOfExpr n, A arg);

	void visitPre(StringLiteralExpr n, A arg);

	void visitPre(IntegerLiteralExpr n, A arg);

	void visitPre(LongLiteralExpr n, A arg);

	void visitPre(IntegerLiteralMinValueExpr n, A arg);

	void visitPre(LongLiteralMinValueExpr n, A arg);

	void visitPre(CharLiteralExpr n, A arg);

	void visitPre(DoubleLiteralExpr n, A arg);

	void visitPre(BooleanLiteralExpr n, A arg);

	void visitPre(NullLiteralExpr n, A arg);

	void visitPre(MethodCallExpr n, A arg);

	void visitPre(NameExpr n, A arg);

	void visitPre(ObjectCreationExpr n, A arg);

	void visitPre(QualifiedNameExpr n, A arg);

	void visitPre(ThisExpr n, A arg);

	void visitPre(SuperExpr n, A arg);

	void visitPre(UnaryExpr n, A arg);

	void visitPre(VariableDeclarationExpr n, A arg);

	void visitPre(MarkerAnnotationExpr n, A arg);

	void visitPre(SingleMemberAnnotationExpr n, A arg);

	void visitPre(NormalAnnotationExpr n, A arg);

	void visitPre(MemberValuePair n, A arg);

	//- Statements ----------------------------------------

	void visitPre(ExplicitConstructorInvocationStmt n, A arg);

	void visitPre(TypeDeclarationStmt n, A arg);

	void visitPre(AssertStmt n, A arg);

	void visitPre(BlockStmt n, A arg);

	void visitPre(LabeledStmt n, A arg);

	void visitPre(EmptyStmt n, A arg);

	void visitPre(ExpressionStmt n, A arg);

	void visitPre(SwitchStmt n, A arg);

	void visitPre(SwitchEntryStmt n, A arg);

	void visitPre(BreakStmt n, A arg);

	void visitPre(ReturnStmt n, A arg);

	void visitPre(IfStmt n, A arg);

	void visitPre(WhileStmt n, A arg);

	void visitPre(ContinueStmt n, A arg);

	void visitPre(DoStmt n, A arg);

	void visitPre(ForeachStmt n, A arg);

	void visitPre(ForStmt n, A arg);

	void visitPre(ThrowStmt n, A arg);

	void visitPre(SynchronizedStmt n, A arg);

	void visitPre(TryStmt n, A arg);

	void visitPre(CatchClause n, A arg);


    //- Post Methods --------------------------------------

    //- Compilation Unit ----------------------------------

    void visitPost(CompilationUnit n, A arg);

    void visitPost(PackageDeclaration n, A arg);

    void visitPost(ImportDeclaration n, A arg);

    void visitPost(TypeParameter n, A arg);

    void visitPost(LineComment n, A arg);

    void visitPost(BlockComment n, A arg);

    //- Body ----------------------------------------------

    void visitPost(ClassOrInterfaceDeclaration n, A arg);

    void visitPost(EnumDeclaration n, A arg);

    void visitPost(EmptyTypeDeclaration n, A arg);

    void visitPost(EnumConstantDeclaration n, A arg);

    void visitPost(AnnotationDeclaration n, A arg);

    void visitPost(AnnotationMemberDeclaration n, A arg);

    void visitPost(FieldDeclaration n, A arg);

    void visitPost(VariableDeclarator n, A arg);

    void visitPost(VariableDeclaratorId n, A arg);

    void visitPost(ConstructorDeclaration n, A arg);

    void visitPost(MethodDeclaration n, A arg);

    void visitPost(Parameter n, A arg);

    void visitPost(EmptyMemberDeclaration n, A arg);

    void visitPost(InitializerDeclaration n, A arg);

    void visitPost(JavadocComment n, A arg);

    //- Type ----------------------------------------------

    void visitPost(ClassOrInterfaceType n, A arg);

    void visitPost(PrimitiveType n, A arg);

    void visitPost(ReferenceType n, A arg);

    void visitPost(VoidType n, A arg);

    void visitPost(WildcardType n, A arg);

    //- Expression ----------------------------------------

    void visitPost(ArrayAccessExpr n, A arg);

    void visitPost(ArrayCreationExpr n, A arg);

    void visitPost(ArrayInitializerExpr n, A arg);

    void visitPost(AssignExpr n, A arg);

    void visitPost(BinaryExpr n, A arg);

    void visitPost(CastExpr n, A arg);

    void visitPost(ClassExpr n, A arg);

    void visitPost(ConditionalExpr n, A arg);

    void visitPost(EnclosedExpr n, A arg);

    void visitPost(FieldAccessExpr n, A arg);

    void visitPost(InstanceOfExpr n, A arg);

    void visitPost(StringLiteralExpr n, A arg);

    void visitPost(IntegerLiteralExpr n, A arg);

    void visitPost(LongLiteralExpr n, A arg);

    void visitPost(IntegerLiteralMinValueExpr n, A arg);

    void visitPost(LongLiteralMinValueExpr n, A arg);

    void visitPost(CharLiteralExpr n, A arg);

    void visitPost(DoubleLiteralExpr n, A arg);

    void visitPost(BooleanLiteralExpr n, A arg);

    void visitPost(NullLiteralExpr n, A arg);

    void visitPost(MethodCallExpr n, A arg);

    void visitPost(NameExpr n, A arg);

    void visitPost(ObjectCreationExpr n, A arg);

    void visitPost(QualifiedNameExpr n, A arg);

    void visitPost(ThisExpr n, A arg);

    void visitPost(SuperExpr n, A arg);

    void visitPost(UnaryExpr n, A arg);

    void visitPost(VariableDeclarationExpr n, A arg);

    void visitPost(MarkerAnnotationExpr n, A arg);

    void visitPost(SingleMemberAnnotationExpr n, A arg);

    void visitPost(NormalAnnotationExpr n, A arg);

    void visitPost(MemberValuePair n, A arg);

    //- Statements ----------------------------------------

    void visitPost(ExplicitConstructorInvocationStmt n, A arg);

    void visitPost(TypeDeclarationStmt n, A arg);

    void visitPost(AssertStmt n, A arg);

    void visitPost(BlockStmt n, A arg);

    void visitPost(LabeledStmt n, A arg);

    void visitPost(EmptyStmt n, A arg);

    void visitPost(ExpressionStmt n, A arg);

    void visitPost(SwitchStmt n, A arg);

    void visitPost(SwitchEntryStmt n, A arg);

    void visitPost(BreakStmt n, A arg);

    void visitPost(ReturnStmt n, A arg);

    void visitPost(IfStmt n, A arg);

    void visitPost(WhileStmt n, A arg);

    void visitPost(ContinueStmt n, A arg);

    void visitPost(DoStmt n, A arg);

    void visitPost(ForeachStmt n, A arg);

    void visitPost(ForStmt n, A arg);

    void visitPost(ThrowStmt n, A arg);

    void visitPost(SynchronizedStmt n, A arg);

    void visitPost(TryStmt n, A arg);

    void visitPost(CatchClause n, A arg);

}
