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
package org.kuali.rice.devtools.jpa.eclipselink.conv.parser;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.VariableDeclarator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ParserUtil {

    private ParserUtil() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * In Java variables can be defined like the following:
     * int i, j, k;
     *
     * When mapping fields in xml this is not a problem.  However when using annotation on a field,
     * Each field should be defined separately.  This helper will deconstruct these fields such
     * that later AST analysis will not need to account for field defined on a separate line.
     */
    public static void deconstructMultiDeclarations(Collection<FieldDeclaration> fields) {

        for (FieldDeclaration field : fields) {

            ClassOrInterfaceDeclaration parent = (ClassOrInterfaceDeclaration) field.getParentNode();

            //these are chained together
            if (field.getVariables().size() > 1) {
                int index = parent.getMembers().indexOf(field);
                parent.getMembers().remove(index);
                List<FieldDeclaration> deconstructed = new ArrayList<FieldDeclaration>();
                for (VariableDeclarator v : field.getVariables()) {
                    FieldDeclaration f = new FieldDeclaration(field.getJavaDoc(), field.getModifiers(), field.getAnnotations(), field.getType(), Collections.singletonList(v));
                    f.setComment(field.getComment());
                    f.setParentNode(field.getParentNode());
                    deconstructed.add(f);
                }
                parent.getMembers().addAll(index, deconstructed);
            }
        }
    }

    public static List<FieldDeclaration> getFieldMembers(List<BodyDeclaration> members) {
        if (members != null) {
            List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
            for (BodyDeclaration member : members) {
                if (member instanceof FieldDeclaration) {
                    fields.add((FieldDeclaration) member);
                }
            }
            return fields;
        }
        return Collections.emptyList();
    }

    public static String getFieldName(FieldDeclaration field) {
        if (field.getVariables().size() > 1) {
            throw new IllegalArgumentException("cannot handle multiple variable declarations on a single line.  This should have been cleaned up earlier.");
        }

        return field.getVariables().get(0).getId().getName();
    }

    public static void sortImports(List<ImportDeclaration> imports) {
        Collections.sort(imports, new Comparator<ImportDeclaration>() {
            @Override
            public int compare(ImportDeclaration o1, ImportDeclaration o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
    }
}
