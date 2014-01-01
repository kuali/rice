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
package org.kuali.rice.coreservice.impl.parameter;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.coreservice.api.component.Component;
import org.kuali.rice.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.ParameterContract;
import org.kuali.rice.coreservice.api.parameter.ParameterKey;
import org.kuali.rice.coreservice.api.parameter.ParameterRepositoryService;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.coreservice.api.parameter.ParameterTypeContract;
import org.kuali.rice.coreservice.impl.component.DerivedComponentBo;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParameterRepositoryServiceImplTest {
    private static final String PARAM_NAME = "SHIELDS_ON";
    private static final String PARAM_APP_ID = "BORG_HUNT";
    private static final String PARAM_NAME_SPACE = "TNG";
    private static final String PARAM_COMP_CODE = "C";
    private static final String PARAM_VALUE = "true";
    private static final String PARAM_DESCR = "turn the shields on";

    private static final String PARAM_TYPE_NAME = "Config";
    private static final String PARAM_TYPE_CODE = "PC";


    static final Parameter parameter = createParameter();
    static final ParameterKey key =
            ParameterKey.create(PARAM_APP_ID, PARAM_NAME_SPACE, PARAM_COMP_CODE, PARAM_NAME);

    ParameterBo bo = ParameterBo.from(parameter);

    @Mock private DataObjectService dataObjectService;
    @InjectMocks private ParameterRepositoryServiceImpl parameterRepositoryService
                                        = new ParameterRepositoryServiceImpl();

    private ParameterRepositoryService parameterService = parameterRepositoryService;

    @Test(expected = IllegalArgumentException.class)
    public void test_create_parameter_null_parameter() throws Exception{
        getParameterRepositoryService().createParameter(null);
    }

    @Test(expected = IllegalStateException.class)
    public void test_create_parameter_exists() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        getParameterRepositoryService().createParameter(parameter);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_create_parameter_does_not_exist() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        when(dataObjectService.save(any(ParameterBo.class),any(PersistenceOption.FLUSH.getClass()))).thenReturn(bo);
        getParameterRepositoryService().createParameter(parameter);
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
        verify(dataObjectService, times(1)).save(any(ParameterBo.class), any(PersistenceOption.FLUSH.getClass()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_update_parameter_null_parameter() throws Exception{
        getParameterRepositoryService().updateParameter(null);
    }

    @Test
    public void test_update_parameter_exists() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        when(dataObjectService.save(any(ParameterBo.class))).thenReturn(bo);
        Parameter param = getParameterRepositoryService().updateParameter(parameter);
        assertTrue("Parameter retrieved after update", param != null);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
        verify(dataObjectService, times(1)).save(any(ParameterBo.class));

    }

    @Test(expected = IllegalStateException.class)
    public void test_update_parameter_does_not_exist() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        getParameterRepositoryService().updateParameter(parameter);
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_parameter_null_key() throws Exception{
        getParameterRepositoryService().getParameter(null);
    }

    @Test
    public void test_get_parameter_exists() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        Parameter param = getParameterRepositoryService().getParameter(key);
        assertEquals("Parameters are equal",param,parameter);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_does_not_exist() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        assertNull("Returned null parameter",getParameterRepositoryService().getParameter(key));
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_value_as_string_not_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        assertEquals(parameter.getValue(), getParameterRepositoryService().getParameterValueAsString(key));
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_value_as_string_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        assertNull(getParameterRepositoryService().getParameterValueAsString(key));
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_value_as_boolean_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        assertNull(getParameterRepositoryService().getParameterValueAsBoolean(key));
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    private void test_get_parameter_value_as_boolean_not_null(String value, boolean bValue) {
        ParameterBo bo = ParameterBo.from(parameter);
        bo.setValue(value);
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        assertEquals(bValue, getParameterRepositoryService().getParameterValueAsBoolean(key));
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_value_as_boolean_not_null_Y() throws Exception{
        test_get_parameter_value_as_boolean_not_null("Y", true);
    }

    @Test
    public void test_get_parameter_value_as_boolean_not_null_true() throws Exception{
        //mixed case
        test_get_parameter_value_as_boolean_not_null("tRue", true);
    }

    @Test
    public void test_get_parameter_value_as_boolean_not_null_N() throws Exception{
        test_get_parameter_value_as_boolean_not_null("N", false);
    }

    @Test
    public void test_get_parameter_value_as_boolean_not_null_false() throws Exception{
        //mixed case
        test_get_parameter_value_as_boolean_not_null("fAlse", false);
    }

    @Test
    public void test_get_parameter_value_as_boolean_not_null_not_boolean() throws Exception{
        bo.setValue("not boolean");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        assertNull(getParameterRepositoryService().getParameterValueAsBoolean(key));
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_values_as_string_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        Collection<String> parameterList = getParameterRepositoryService().getParameterValuesAsString(key);
        assertTrue(parameterList.isEmpty());
        assertImmutableList(parameterList);
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_values_as_string_not_null_multiple_values() throws Exception{
        bo.setValue("foo; bar  ; baz ");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        Collection<String> paramValueList = getParameterRepositoryService().getParameterValuesAsString(key);
        assertTrue(paramValueList != null && paramValueList.size() == 3 && paramValueList.contains("foo") &&
                paramValueList.contains("bar") && paramValueList.contains("baz"));
        assertImmutableList(paramValueList);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_parameter_values_as_string_not_null_single_values() throws Exception{
        bo.setValue("a value ");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        Collection<String> paramValueList = getParameterRepositoryService().getParameterValuesAsString(key);
        assertTrue(paramValueList != null && paramValueList.size() == 1 &&
                      paramValueList.contains("a value"));
        assertImmutableList(paramValueList);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_value_as_string_null_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValueAsString(key,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_value_as_string_empty_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValueAsString(key,"");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_value_as_string_whitespace_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValueAsString(key, "  ");
    }

    @Test
    public void test_get_sub_parameter_value_as_string_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        assertNull(getParameterRepositoryService().getSubParameterValueAsString(key, "foo"));
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_sub_parameter_value_as_string_single_match() throws Exception{
        //adding whitespace
        bo.setValue("foo= f1; bar=b1; baz=z1");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        assertEquals("f1", getParameterRepositoryService().getSubParameterValueAsString(key, "foo"));
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_sub_parameter_value_as_string_multiple_match() throws Exception{
        bo.setValue("foo=f1; bar=b1; foo=f2");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);
        //should return first match
        assertEquals("f1", getParameterRepositoryService().getSubParameterValueAsString(key, "foo"));
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_values_as_string_null_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValuesAsString(key, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_values_as_string_empty_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValuesAsString(key, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_get_sub_parameter_values_as_string_whitespace_subparameter() throws Exception{
        getParameterRepositoryService().getSubParameterValuesAsString(key, "  ");
    }

    @Test
    public void test_get_sub_parameter_values_as_string_null() throws Exception{
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(null);
        Collection<String> values = getParameterRepositoryService().getSubParameterValuesAsString(key, "foo");
        assertTrue(values.isEmpty());
        assertImmutableList(values);
        verify(dataObjectService, times(2)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_sub_parameter_values_as_string_single_match() throws Exception {
        //adding whitespace
        bo.setValue("foo= f1, f2 , f3; bar=b1; baz=z1");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);

        Collection<String> values = getParameterRepositoryService().getSubParameterValuesAsString(key, "foo");
        assertTrue(values.size() == 3);
        assertTrue(values.contains("f1"));
        assertTrue(values.contains("f2"));
        assertTrue(values.contains("f3"));
        assertImmutableList(values);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }

    @Test
    public void test_get_sub_parameter_values_as_string_multiple_match() throws Exception{
        //adding whitespace
        bo.setValue("foo= f1, f2 , f3; bar=b1; foo=f4,f5");
        when(dataObjectService.find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class))).thenReturn(bo);

        Collection<String> values = getParameterRepositoryService().getSubParameterValuesAsString(key, "foo");
        assertTrue(values.size() == 3);
        assertTrue(values.contains("f1"));
        assertTrue(values.contains("f2"));
        assertTrue(values.contains("f3"));
        assertImmutableList(values);
        verify(dataObjectService, times(1)).find(Matchers.argThat(new ClassOrSubclassMatcher<ParameterBo>(
                ParameterBo.class)), any(QueryByCriteria.class));
    }








    private void assertImmutableList(Collection<?> immutableList){
        try{
            immutableList.add(null);
            fail("Should not be able to add to immutable list");
        } catch(UnsupportedOperationException e){

        }
    }

    class ClassOrSubclassMatcher<T> extends BaseMatcher<Class<T>> {

        private final Class<T> targetClass;

        public ClassOrSubclassMatcher(Class<T> targetClass) {
            this.targetClass = targetClass;
        }

        @SuppressWarnings("unchecked")
        public boolean matches(Object obj) {
            if (obj != null) {
                if (obj instanceof Class) {
                    return targetClass.isAssignableFrom((Class<T>) obj);
                }
            }
            return false;
        }

        public void describeTo(Description desc) {
            desc.appendText("Matches a class or subclass");
        }
    }








    private static Parameter createParameter() {
        final ParameterTypeContract parameterTypeContract = new ParameterTypeContract() {
            @Override
            public String getName() {
                return PARAM_TYPE_NAME;
            }

            @Override
            public String getCode() {
                return PARAM_TYPE_CODE;
            }

            @Override
            public String getObjectId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public Long getVersionNumber() {
                return 1L;
            }
        };

        ParameterContract parameterContract = new ParameterContract() {
            @Override
            public String getApplicationId() {
                return PARAM_APP_ID;
            }

            @Override
            public String getNamespaceCode() {
                return PARAM_NAME_SPACE;
            }

            @Override
            public String getComponentCode() {
                return PARAM_COMP_CODE;
            }

            @Override
            public String getName() {
                return PARAM_NAME;
            }

            @Override
            public String getValue() {
                return PARAM_VALUE;
            }

            @Override
            public String getDescription() {
                return PARAM_DESCR;
            }

            @Override
            public EvaluationOperator getEvaluationOperator() {
                return EvaluationOperator.ALLOW;
            }

            @Override
            public ParameterTypeContract getParameterType() {
                return parameterTypeContract;
            }

            @Override
            public String getObjectId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public Long getVersionNumber() {
                return 1L;
            }
        };

        return Parameter.Builder.create(parameterContract).build();

    }

    public void setParameterRepositoryService(ParameterRepositoryService parameterRepositoryService){
        this.parameterService = parameterRepositoryService;
    }

    public ParameterRepositoryService getParameterRepositoryService(){
        return parameterService;
    }


}
