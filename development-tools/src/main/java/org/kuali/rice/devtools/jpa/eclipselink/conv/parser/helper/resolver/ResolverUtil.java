package org.kuali.rice.devtools.jpa.eclipselink.conv.parser.helper.resolver;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.body.TypeDeclaration;

public final class ResolverUtil {

    private ResolverUtil() {
        throw new UnsupportedOperationException("do not call");
    }

    public static boolean canFieldBeAnnotated(FieldDeclaration node) {
        final TypeDeclaration dclr = (TypeDeclaration) node.getParentNode();
        if (!ModifierSet.isStatic(node.getModifiers()) && dclr.getParentNode() instanceof CompilationUnit) {
            //handling nested classes
            return true;
        }
        return false;
    }
}
