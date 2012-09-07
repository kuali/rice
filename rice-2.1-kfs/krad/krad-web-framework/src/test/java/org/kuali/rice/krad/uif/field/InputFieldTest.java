package org.kuali.rice.krad.uif.field;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.keyvalues.KeyValuesFinderFactory;
import org.kuali.rice.krad.keyvalues.PlaceholderKeyValuesFinder;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.component.BindingInfo;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import java.util.*;

/**
 * tests InputField object and methods
 *
**/
public class InputFieldTest {

    View view = null;
    TestModel model = null;
    KeyValuesFinder optionsFinder = null;
    BindingInfo bindingInfo = null;


    @Before
    public void setUp() {
        view = Mockito.mock(View.class);
        optionsFinder = Mockito.mock(KeyValuesFinder.class);
        bindingInfo = Mockito.mock(BindingInfo.class);
        model = new TestModel();
    }

    @Test
    public void testPerformFinalizeWithNonStringFieldOptions() throws Exception {
        // setup options finder
        Map<String, String> map = new HashMap<String, String>();
        map.put("testInteger", "1");
        optionsFinder = KeyValuesFinderFactory.fromMap(map);

        // setup preconditions (view status is final; bindinginfo return testInteger)
        when(view.getViewStatus()).thenReturn(UifConstants.ViewStatus.FINAL);
        when(bindingInfo.getBindingPath()).thenReturn("testInteger");

        // setup input field with binding info and readonly
        InputField testObj = new InputField();        
        testObj.setBindingInfo(bindingInfo);
        testObj.setReadOnly(true);
        testObj.setOptionsFinder(optionsFinder);

        testObj.performFinalize(view, model, testObj);

    }

    // Simple model object to return testInteger integer
    private class TestModel {
        public int getTestInteger() {
            return 1;
        }
    }

}
