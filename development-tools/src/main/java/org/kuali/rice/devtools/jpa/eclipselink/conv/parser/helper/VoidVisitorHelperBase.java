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

public class VoidVisitorHelperBase implements VoidVisitorHelper<Object>{
    @Override
    public void visitPre(CompilationUnit n, Object arg) {
        
    }

    @Override
    public void visitPre(PackageDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(ImportDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(TypeParameter n, Object arg) {
        
    }

    @Override
    public void visitPre(LineComment n, Object arg) {
        
    }

    @Override
    public void visitPre(BlockComment n, Object arg) {
        
    }

    @Override
    public void visitPre(ClassOrInterfaceDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(EnumDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(EmptyTypeDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(EnumConstantDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(AnnotationDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(AnnotationMemberDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(FieldDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(VariableDeclarator n, Object arg) {
        
    }

    @Override
    public void visitPre(VariableDeclaratorId n, Object arg) {
        
    }

    @Override
    public void visitPre(ConstructorDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(MethodDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(Parameter n, Object arg) {
        
    }

    @Override
    public void visitPre(EmptyMemberDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(InitializerDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPre(JavadocComment n, Object arg) {
        
    }

    @Override
    public void visitPre(ClassOrInterfaceType n, Object arg) {
        
    }

    @Override
    public void visitPre(PrimitiveType n, Object arg) {
        
    }

    @Override
    public void visitPre(ReferenceType n, Object arg) {
        
    }

    @Override
    public void visitPre(VoidType n, Object arg) {
        
    }

    @Override
    public void visitPre(WildcardType n, Object arg) {
        
    }

    @Override
    public void visitPre(ArrayAccessExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ArrayCreationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ArrayInitializerExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(AssignExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(BinaryExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(CastExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ClassExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ConditionalExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(EnclosedExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(FieldAccessExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(InstanceOfExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(StringLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(IntegerLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(LongLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(IntegerLiteralMinValueExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(LongLiteralMinValueExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(CharLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(DoubleLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(BooleanLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(NullLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(MethodCallExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(NameExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ObjectCreationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(QualifiedNameExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(ThisExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(SuperExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(UnaryExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(VariableDeclarationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(MarkerAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(SingleMemberAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(NormalAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPre(MemberValuePair n, Object arg) {
        
    }

    @Override
    public void visitPre(ExplicitConstructorInvocationStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(TypeDeclarationStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(AssertStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(BlockStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(LabeledStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(EmptyStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ExpressionStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(SwitchStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(SwitchEntryStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(BreakStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ReturnStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(IfStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(WhileStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ContinueStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(DoStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ForeachStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ForStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(ThrowStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(SynchronizedStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(TryStmt n, Object arg) {
        
    }

    @Override
    public void visitPre(CatchClause n, Object arg) {
        
    }

    @Override
    public void visitPost(CompilationUnit n, Object arg) {
        
    }

    @Override
    public void visitPost(PackageDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(ImportDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(TypeParameter n, Object arg) {
        
    }

    @Override
    public void visitPost(LineComment n, Object arg) {
        
    }

    @Override
    public void visitPost(BlockComment n, Object arg) {
        
    }

    @Override
    public void visitPost(ClassOrInterfaceDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(EnumDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(EmptyTypeDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(EnumConstantDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(AnnotationDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(AnnotationMemberDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(FieldDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(VariableDeclarator n, Object arg) {
        
    }

    @Override
    public void visitPost(VariableDeclaratorId n, Object arg) {
        
    }

    @Override
    public void visitPost(ConstructorDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(MethodDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(Parameter n, Object arg) {
        
    }

    @Override
    public void visitPost(EmptyMemberDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(InitializerDeclaration n, Object arg) {
        
    }

    @Override
    public void visitPost(JavadocComment n, Object arg) {
        
    }

    @Override
    public void visitPost(ClassOrInterfaceType n, Object arg) {
        
    }

    @Override
    public void visitPost(PrimitiveType n, Object arg) {
        
    }

    @Override
    public void visitPost(ReferenceType n, Object arg) {
        
    }

    @Override
    public void visitPost(VoidType n, Object arg) {
        
    }

    @Override
    public void visitPost(WildcardType n, Object arg) {
        
    }

    @Override
    public void visitPost(ArrayAccessExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ArrayCreationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ArrayInitializerExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(AssignExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(BinaryExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(CastExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ClassExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ConditionalExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(EnclosedExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(FieldAccessExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(InstanceOfExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(StringLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(IntegerLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(LongLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(IntegerLiteralMinValueExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(LongLiteralMinValueExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(CharLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(DoubleLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(BooleanLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(NullLiteralExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(MethodCallExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(NameExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ObjectCreationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(QualifiedNameExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(ThisExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(SuperExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(UnaryExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(VariableDeclarationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(MarkerAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(SingleMemberAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(NormalAnnotationExpr n, Object arg) {
        
    }

    @Override
    public void visitPost(MemberValuePair n, Object arg) {
        
    }

    @Override
    public void visitPost(ExplicitConstructorInvocationStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(TypeDeclarationStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(AssertStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(BlockStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(LabeledStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(EmptyStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ExpressionStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(SwitchStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(SwitchEntryStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(BreakStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ReturnStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(IfStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(WhileStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ContinueStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(DoStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ForeachStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ForStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(ThrowStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(SynchronizedStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(TryStmt n, Object arg) {
        
    }

    @Override
    public void visitPost(CatchClause n, Object arg) {
        
    }
}
