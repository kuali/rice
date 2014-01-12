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
package org.kuali.rice.scripts

import groovy.util.logging.Log
import org.junit.Assert
import org.junit.Test
import org.springframework.util.ResourceUtils

/**
 * Tests for the {@link ClassParserUtils} class.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Log
public class ClassParserUtilsTest {

    static def parserTestDir = "ClassParserUtilsTest/"

    String getPath(String filename) {
        return ResourceUtils.getFile("classpath:" + filename).absolutePath;
    }

    File getFile(String filename) {
        return new File(getPath(filename))
    }

    /**
     * test parsing a sample action form class
     * */
    @Test
    void testParseActionFormClass() {
        def file = getFile(parserTestDir + "SampleForm.java")
        def actionFormData = ClassParserUtils.parseClassFile(file.text, false)
        Assert.assertEquals("form package contains", "edu.sampleu.bookstore.document.web", actionFormData.package)
        Assert.assertEquals("form imports count", 3, actionFormData.imports.size())
        Assert.assertEquals("form imports contains", "edu.sampleu.bookstore.bo.BookOrder", actionFormData.imports[0])
        Assert.assertEquals("form members count", 2, actionFormData.members.size())
        Assert.assertTrue("form members contains ", actionFormData.members.any { it.fieldName == "serialVersionUID" })
        Assert.assertEquals("form class contains", "SampleForm", actionFormData.className)
        Assert.assertEquals("form extends contains", "KualiTransactionalDocumentFormBase", actionFormData.parentClass)
        log.finer "public methods" + actionFormData.methods
        Assert.assertEquals("public methods count", 3, actionFormData.methods.findAll { it.accessModifier == "public" }.size())
        Assert.assertTrue("public methods contains", actionFormData.methods.any { it.accessModifier == "public" && it.methodName == "getNewBookOrder" })
        Assert.assertEquals("private methods count", 0, actionFormData.methods.findAll { it.accessModifier == "private" }.size())
    }

    @Test
    void testParseClassFieldWithValue() {
        def lineText = "  public Integer testcase = 1;"
        def field = ClassParserUtils.parseFieldDeclaration(lineText)
        Assert.assertNotNull("field name not null", field.fieldName)
        Assert.assertEquals("found correct field name", "testcase", field.fieldName)

    }

    @Test
    void testParseClassFieldWithMap() {
        def lineText = "  public Map<String, String> testcase;"
        def field = ClassParserUtils.parseFieldDeclaration(lineText)
        Assert.assertNotNull("field name not null", field.fieldName)
        Assert.assertEquals("found correct field name", "testcase", field.fieldName)

    }

    @Test
    void testParseClassField() {
        def lineText = "  public Integer testcase;"
        def field = ClassParserUtils.parseFieldDeclaration(lineText)
        Assert.assertNotNull("field name not null", field.fieldName)
        Assert.assertNull("no field value set", field.fieldValue)
    }

    @Test
    void testParseMethodDeclaration() {
        def lineText = "  public final ActionForward addBookOrder(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {"
        def methodElement = ClassParserUtils.parseMethodDeclaration(lineText, [])
        log.finer "method data is " + methodElement
        Assert.assertEquals("access modifier does not match", "public", methodElement.accessModifier)
        Assert.assertEquals("non access modifier does not match", 1, methodElement.nonAccessModifiers.size())
        Assert.assertEquals("non access modifier does not match", "final", methodElement.nonAccessModifiers[0])
        Assert.assertEquals("method name does not match", "addBookOrder", methodElement.methodName)
        Assert.assertEquals("return type does not match", "ActionForward", methodElement.returnType)
        Assert.assertEquals("parameter count does not match", 4, methodElement.parameters.size())
    }

    @Test
    void testParseMethodDeclarationWithMap() {

        def lineText = "  public final Map<String,String> addBookOrder(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {"
        def methodElement = ClassParserUtils.parseMethodDeclaration(lineText, [])
        Assert.assertEquals("return type does not match", "Map<String,String>", methodElement.returnType);

        lineText = "    private Map<String, String> getHashMapToFindActiveAward(String goToAwardNumber) {";
        methodElement = ClassParserUtils.parseMethodDeclaration(lineText, [])
        Assert.assertEquals("return type does not match", "Map<String, String>", methodElement.returnType);

    }

    @Test
    void testParseClassFieldWithModifiers() {
        def lineText = "  public static final Integer testcase;"
        def field = ClassParserUtils.parseFieldDeclaration(lineText)
        Assert.assertNotNull("field name not null", field.fieldName)
        Assert.assertNull("no field value set", field.fieldValue)
        Assert.assertEquals("modifiers size does not match", 2, field.nonAccessModifiers.size())
    }


    @Test
    void testParseClassDeclaration() {
        def defaultExpectedClassElement = [accessModifier: "public", nonAccessModifier: [],  \
                 className: "MyClass", parentClass: "YourClass", interfaces: ["MyInterface", "MyOtherInterface"]];
        def publicClassLine = "  public class MyClass extends YourClass implements MyInterface,MyOtherInterface"
        def abstractClassLine = "  public abstract class MyClass extends YourClass implements MyInterface,MyOtherInterface"
        def staticClassLine = "  public static final class MyClass extends YourClass implements MyInterface,MyOtherInterface"
        def annotations = []

        def classElement = ClassParserUtils.parseClassDeclaration(publicClassLine, annotations)
        checkClassElements("standard class", defaultExpectedClassElement, classElement);
        def abstractClassElement = ClassParserUtils.parseClassDeclaration(abstractClassLine, annotations)
        checkClassElements("abstract class", defaultExpectedClassElement, abstractClassElement);
        def staticClassElement = ClassParserUtils.parseClassDeclaration(staticClassLine, annotations)
        checkClassElements("abstract class", defaultExpectedClassElement, staticClassElement);

    }

    void checkClassElements(String messagePrefix, Map expectedClassElement, Map actualClassElement) {
        Assert.assertEquals(messagePrefix + " parent class does not match", expectedClassElement.accessModifier, actualClassElement.accessModifier)
        Assert.assertEquals(messagePrefix + " class name does not match", expectedClassElement.className, actualClassElement.className)
        Assert.assertEquals(messagePrefix + " parent class does not match", expectedClassElement.parentClass, actualClassElement.parentClass)
        Assert.assertTrue(messagePrefix + " interfaces does not match", expectedClassElement.interfaces.containsAll(actualClassElement.interfaces));
    }
}