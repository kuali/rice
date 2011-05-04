/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.devtools.generators;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JArray;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * This is a description of what this class does - ewestfal don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ImmutableJaxbGenerator {

	public static void main(String[] args) throws Exception {
		
		if (args.length > 2 || args.length < 1) {
			System.err.println("There should be two arguments defined as follows:\n" + 
					"     1. Fully qualified class name of a 'contract' interface\n" + 
					"     2. [Optional] Fully qualified class name of the class to generate.  If not specified, will use the name of the contract interface class and remove \"Contract\" from the end of it.\n");
			System.exit(1);
		}
		
		// argument one should be a fully qualified class name of a "contract" interface
		String contractInterfaceName = args[0];
		
		String className = null;
		// argument two should be the fully qualified class name of the class to generate
		if (args.length == 2) {
			className = args[1];
		} else {
			if (!contractInterfaceName.endsWith("Contract")) {
				throw new IllegalArgumentException("If not explicitly specifying target classname, then contract class name must end with 'Contract'");
			}
			className = contractInterfaceName.substring(0, contractInterfaceName.lastIndexOf("Contract"));
		}
		
		Generator generator = new Generator(contractInterfaceName, className);
		generator.generate();
	}
	
	public static class Generator {
		
		private final String contractInterfaceName;
		private final String className;
		private final JCodeModel codeModel;
		
		public Generator(String contractInterfaceName, String className) {
			this.contractInterfaceName = contractInterfaceName;
			this.className = className;
			this.codeModel = new JCodeModel();
		}
		
		public void generate() throws Exception {
			byte[] javaCode = generateJava();
			System.out.println(new String(javaCode));
		}
		
		private byte[] generateJava() throws Exception {
			
			JDefinedClass classModel = codeModel._class(JMod.PUBLIC | JMod.FINAL, className, ClassType.CLASS);
			Class<?> contractInterface = Class.forName(contractInterfaceName);
			classModel._implements(contractInterface);
			classModel._implements(ModelObjectComplete.class);
			
			List<FieldModel> fields = determineFields(contractInterface);
			
			renderConstantsClass(classModel);
			renderElementsClass(classModel, fields);
			renderClassLevelAnnotations(classModel, fields);
			renderFields(classModel, fields);
			renderFutureElementsField(classModel);			
			renderPrivateJaxbConstructor(classModel, fields);
			renderBuilderConstructor(classModel, fields);
			renderGetters(classModel, fields);
			renderBuilderClass(classModel, fields, contractInterface);
			renderStandardObjectMethods(classModel);
			
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			codeModel.build(new SingleStreamCodeWriter(outputStream));
			return outputStream.toByteArray();
			
		}
		
		private List<FieldModel> determineFields(Class<?> contractInterface) throws Exception {
			List<FieldModel> fieldModels = new ArrayList<FieldModel>();
			
			Method[] methods = contractInterface.getMethods();
			for (Method method : methods) {
				String methodName = method.getName();
				String fieldName = null;
				if (method.getReturnType() != Void.class && method.getParameterTypes().length == 0) {
					if (methodName.startsWith("get")) {
						fieldName = Util.toLowerCaseFirstLetter(methodName.substring(3));
					} else if (methodName.startsWith("is")) {
						fieldName = Util.toLowerCaseFirstLetter(methodName.substring(2));
					} else {
						continue;
					}
					fieldModels.add(new FieldModel(fieldName, method.getReturnType()));
				}
			}
			
			return fieldModels;
		}
		
		private void renderConstantsClass(JDefinedClass classModel) throws Exception {
			
			// define constants class
			JDefinedClass constantsClass = classModel._class(JMod.STATIC, Util.CONSTANTS_CLASS_NAME);
			
			// generate the javadoc on the top of the Constants class
			JDocComment javadoc = constantsClass.javadoc();
			javadoc.append(Util.CONSTANTS_CLASS_JAVADOC);
			
			// render root element name
			JFieldVar rootElementField = constantsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.ROOT_ELEMENT_NAME_FIELD);
			rootElementField.init(JExpr.lit(Util.toLowerCaseFirstLetter(classModel.name())));
			
			// render type name
			JFieldVar typeNameField = constantsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.TYPE_NAME_FIELD);
			typeNameField.init(JExpr.lit(classModel.name() + Util.TYPE_NAME_SUFFIX));
			
			// hash code excludes array
			JFieldVar hashCodeExcludesField = constantsClass.field(JMod.FINAL | JMod.STATIC, String[].class, Util.HASH_CODE_EQUALS_EXCLUDE_FIELD);
			JArray excludeArray = JExpr.newArray(codeModel.ref(String.class));
			JClass coreConstants = codeModel.ref(CoreConstants.class);
			JFieldRef futureElementsRef = coreConstants.staticRef(Util.COMMON_ELEMENTS_CLASS).ref(Util.FUTURE_ELEMENTS_FIELD);
			excludeArray.add(futureElementsRef);
			hashCodeExcludesField.init(excludeArray);
			
		}
		
		private void renderElementsClass(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
			
			// define constants class
			JDefinedClass elementsClass = classModel._class(JMod.STATIC, Util.ELEMENTS_CLASS_NAME);
			
			// generate the javadoc on the top of the Elements class
			JDocComment javadoc = elementsClass.javadoc();
			javadoc.append(Util.ELEMENTS_CLASS_JAVADOC);
			
			// go through each field and create a corresponding constant
			for (FieldModel fieldModel : fields) {
				if (Util.isCommonElement(fieldModel.fieldName)) {
					continue;
				}
				JFieldVar elementFieldVar = elementsClass.field(JMod.FINAL | JMod.STATIC, String.class, Util.toConstantsVariable(fieldModel.fieldName));
				elementFieldVar.init(JExpr.lit(fieldModel.fieldName));
			}
		}
		
		private void renderClassLevelAnnotations(JDefinedClass classModel, List<FieldModel> fields) throws Exception {
			JFieldRef constantsClass = classModel.staticRef(Util.CONSTANTS_CLASS_NAME);
			JFieldRef elementsClass = classModel.staticRef(Util.ELEMENTS_CLASS_NAME);
			JClass coreConstants = codeModel.ref(CoreConstants.class);
			JFieldRef commonElementsRef = coreConstants.staticRef("CommonElements");
			
			// XmlRootElement
			JAnnotationUse rootElementAnnotation = classModel.annotate(XmlRootElement.class);
			rootElementAnnotation.param("name", constantsClass.ref(Util.ROOT_ELEMENT_NAME_FIELD));
			
			// XmlAccessorType
			JAnnotationUse xmlAccessorTypeAnnotation = classModel.annotate(XmlAccessorType.class);
			xmlAccessorTypeAnnotation.param("value", XmlAccessType.NONE);
			
			// XmlType
			JAnnotationUse xmlTypeAnnotation = classModel.annotate(XmlType.class);
			xmlTypeAnnotation.param("name", constantsClass.ref(Util.TYPE_NAME_FIELD));
			JAnnotationArrayMember propOrderMember = xmlTypeAnnotation.paramArray("propOrder");
			for (FieldModel field : fields) {
				if (Util.isCommonElement(field.fieldName)) {
					propOrderMember.param(commonElementsRef.ref(Util.toConstantsVariable(field.fieldName)));
				} else {
					propOrderMember.param(elementsClass.ref(Util.toConstantsVariable(field.fieldName)));
				}
			}
			propOrderMember.param(commonElementsRef.ref("FUTURE_ELEMENTS"));
		}
		
		private void renderFields(JDefinedClass classModel, List<FieldModel> fields) {
			for (FieldModel fieldModel : fields) {
				renderField(classModel, fieldModel);
			}
		}
		
		private void renderField(JDefinedClass classModel, FieldModel fieldModel) {
			JFieldVar field = classModel.field(JMod.PRIVATE | JMod.FINAL, fieldModel.fieldType, fieldModel.fieldName);
			JAnnotationUse annotation = field.annotate(XmlElement.class);
			if (Util.isCommonElement(fieldModel.fieldName)) {
				JClass coreConstants = codeModel.ref(CoreConstants.class);
				JFieldRef commonElementsRef = coreConstants.staticRef("CommonElements");
				annotation.param("name", commonElementsRef.ref(Util.toConstantsVariable(fieldModel.fieldName)));
			} else {
				JClass elementsClass = codeModel.ref(Util.ELEMENTS_CLASS_NAME);
				JFieldRef fieldXmlNameRef = elementsClass.staticRef(Util.toConstantsVariable(fieldModel.fieldName));
				annotation.param("name", fieldXmlNameRef);
			}
			annotation.param("required", false);
		}
		
		private void renderFutureElementsField(JDefinedClass classModel) throws Exception {
			JType collectionType = codeModel.parseType("java.util.Collection<org.w3c.dom.Element>");
			JFieldVar field = classModel.field(JMod.PRIVATE | JMod.FINAL, collectionType, "_futureElements");
			field.init(JExpr._null());
			JAnnotationUse annotation = field.annotate(SuppressWarnings.class);
			annotation.param("value", "unused");
			field.annotate(XmlAnyElement.class);
		}
		
		private void renderPrivateJaxbConstructor(JDefinedClass classModel, List<FieldModel> fields) {
			JMethod method = classModel.constructor(JMod.PRIVATE);
			JBlock body = method.body();
			for (FieldModel fieldModel : fields) {
				body.directStatement("this." + fieldModel.fieldName + " = null;");
			}
			method.javadoc().add("Private constructor used only by JAXB.");
		}
		
		private void renderBuilderConstructor(JDefinedClass classModel, List<FieldModel> fields) {
			JMethod method = classModel.constructor(JMod.PRIVATE);
			method.param(codeModel.ref("Builder"), "builder");
			JBlock body = method.body();
			for (FieldModel fieldModel : fields) {
				body.directStatement("this." + fieldModel.fieldName + " = builder." + Util.generateGetter(fieldModel.fieldName) + ";");
			}
		}
		
		private void renderGetters(JDefinedClass classModel, List<FieldModel> fields) {
			for (FieldModel fieldModel : fields) {
				JMethod getterMethod = classModel.method(JMod.PUBLIC, fieldModel.fieldType, Util.generateGetterName(fieldModel.fieldName));
				JBlock methodBody = getterMethod.body();
				methodBody.directStatement("return this." + fieldModel.fieldName + ";");
				getterMethod.annotate(Override.class);
			}
		}
		
		private void renderBuilderClass(JDefinedClass classModel, List<FieldModel> fields, Class<?> contractInterface) throws Exception {
			
			// define constants class
			JDefinedClass builderClass = classModel._class(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, Util.BUILDER_CLASS_NAME);
			
			// create a literal version of the Builder class so that the code generator won't pre-pend Builder class references with outermost class
			JClass literalBuilderClass = codeModel.ref("Builder");
			
			// generate the javadoc on the top of the Elements class
			JDocComment javadoc = builderClass.javadoc();
			javadoc.append(Util.generateBuilderJavadoc(classModel.name(), contractInterface.getSimpleName()));
			
			builderClass._implements(contractInterface);
			builderClass._implements(ModelBuilder.class);
			builderClass._implements(Serializable.class);
			
			// render the builder fields
			for (FieldModel fieldModel : fields) {
				builderClass.field(JMod.PRIVATE, fieldModel.fieldType, fieldModel.fieldName);
			}
			
			// render default empty constructor for builder
			JMethod constructor = builderClass.constructor(JMod.PRIVATE);
			constructor.body().directStatement("// TODO modify this constructor as needed to pass any required values and invoke the appropriate 'setter' methods");
		
			renderBuilderDefaultCreate(builderClass, literalBuilderClass);
			renderBuilderCreateContract(builderClass, literalBuilderClass, fields, contractInterface);
			renderBuild(builderClass);
			renderGetters(builderClass, fields);
			renderSetters(builderClass, fields);
		}
		
		private void renderBuilderDefaultCreate(JDefinedClass builderClass, JClass literalBuilderClass) {
			JMethod createMethod = builderClass.method(JMod.PUBLIC | JMod.STATIC, literalBuilderClass, "create");
			JBlock createMethodBody = createMethod.body();
			createMethodBody.directStatement("// TODO modify as needed to pass any required values and add them to the signature of the 'create' method");
			createMethodBody.directStatement("return new Builder();");
		}
		
		private void renderBuilderCreateContract(JDefinedClass builderClass, JClass literalBuilderClass, List<FieldModel> fields, Class<?> contractInterface) {
			JMethod createContractMethod = builderClass.method(JMod.PUBLIC | JMod.STATIC, literalBuilderClass, "create");
			JVar contractParam = createContractMethod.param(contractInterface, "contract");
			JBlock body = createContractMethod.body();
			JConditional nullContractCheck = body._if(contractParam.eq(JExpr._null()));
			nullContractCheck._then().directStatement("throw new IllegalArgumentException(\"contract was null\");");
			body.directStatement("// TODO if create() is modified to accept required parameters, this will need to be modified");
			body.directStatement("Builder builder = create();");
			for (FieldModel fieldModel : fields) {
				String fieldName = fieldModel.fieldName;
				body.directStatement("builder." + Util.generateSetter(fieldName, "contract." + Util.generateGetter(fieldName)) + ";");
			}
			body.directStatement("return builder;");
		}
		
		private void renderBuild(JDefinedClass builderClass) {
			JMethod buildMethod = builderClass.method(JMod.PUBLIC, builderClass.outer(), "build");
			buildMethod.body().directStatement("return new " + builderClass.outer().name() + "(this);");
		}
		
		private void renderSetters(JDefinedClass builderClass, List<FieldModel> fields) {
			for (FieldModel fieldModel : fields) {
				String fieldName = fieldModel.fieldName;
				JMethod setterMethod = builderClass.method(JMod.PUBLIC, codeModel.VOID, Util.generateSetterName(fieldName));
				setterMethod.param(fieldModel.fieldType, fieldName);
				setterMethod.body().directStatement("// TODO add validation of input value if required and throw IllegalArgumentException if needed");
				setterMethod.body().directStatement("this." + fieldName + " = " + fieldName + ";");
			}
		}
		
		/**
		 * Generates standard hashCode, equals, and toString methods
		 */
		private void renderStandardObjectMethods(JDefinedClass classModel) {
			
			JClass constantsClass = codeModel.ref(Util.CONSTANTS_CLASS_NAME);
			JFieldRef hashCodeEqualsExcludes = constantsClass.staticRef(Util.HASH_CODE_EQUALS_EXCLUDE_FIELD);
			
			// hashCode
			JMethod hashCodeMethod = classModel.method(JMod.PUBLIC, codeModel.INT, "hashCode");
			JInvocation hcInvoke = codeModel.ref(HashCodeBuilder.class).staticInvoke("reflectionHashCode");
			hcInvoke.arg(JExpr._this());
			hcInvoke.arg(hashCodeEqualsExcludes);
			hashCodeMethod.body()._return(hcInvoke);
			hashCodeMethod.annotate(Override.class);
			
			// equals
			JMethod equalsMethod = classModel.method(JMod.PUBLIC, codeModel.BOOLEAN, "equals");
			JVar objectParam = equalsMethod.param(Object.class, "object");
			JInvocation equalsInvoke = codeModel.ref(EqualsBuilder.class).staticInvoke("reflectionEquals");
			equalsInvoke.arg(objectParam);
			equalsInvoke.arg(JExpr._this());
			equalsInvoke.arg(hashCodeEqualsExcludes);
			equalsMethod.body()._return(equalsInvoke);
			equalsMethod.annotate(Override.class);
			
			// toString
			JMethod toStringMethod = classModel.method(JMod.PUBLIC, String.class, "toString");
			JInvocation toStringInvoke = codeModel.ref(ToStringBuilder.class).staticInvoke("reflectionToString");
			toStringInvoke.arg(JExpr._this());
			toStringMethod.body()._return(toStringInvoke);
			toStringMethod.annotate(Override.class);
			
			
		}
				
	}
	
	private static class FieldModel {
		
		private final String fieldName;
		private final Class<?> fieldType;
		
		private FieldModel(String fieldName, Class<?> fieldType) {
			this.fieldName = fieldName;
			this.fieldType = fieldType;
		}
				
	}
	
}
