/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.web.spring;

import org.kuali.rice.core.util.type.AbstractKualiDecimal;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.core.util.type.KualiInteger;
import org.kuali.rice.core.util.type.KualiPercent;
import org.kuali.rice.core.web.format.BigDecimalFormatter;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.KualiIntegerCurrencyFormatter;
import org.kuali.rice.core.web.format.PercentageFormatter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * Registers standard PropertyEditors used in binding for all http requests. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifConfigurableWebBindingInitializer extends ConfigurableWebBindingInitializer {

	@Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(AbstractKualiDecimal.class, new UifKnsFormatterPropertyEditor(BigDecimalFormatter.class));
        binder.registerCustomEditor(KualiDecimal.class, new UifKnsFormatterPropertyEditor(CurrencyFormatter.class)); 
        binder.registerCustomEditor(KualiInteger.class, new UifKnsFormatterPropertyEditor(KualiIntegerCurrencyFormatter.class));
        binder.registerCustomEditor(KualiPercent.class, new UifKnsFormatterPropertyEditor(PercentageFormatter.class));
        
        // TODO do we need this since we are switching to spring tags
        binder.registerCustomEditor(boolean.class, new UifBooleanEditor());
        
	    super.initBinder(binder, request);
    }

}
