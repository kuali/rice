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
import org.kuali.rice.kns.web.format.BigDecimalFormatter;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.KualiIntegerCurrencyFormatter;
import org.kuali.rice.kns.web.format.PercentageFormatter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * This is a description of what this class does - delyea don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradConfigurableWebBindingInitializer extends ConfigurableWebBindingInitializer {

	@Override
    public void initBinder(WebDataBinder binder, WebRequest request) {
        binder.registerCustomEditor(AbstractKualiDecimal.class, new KualiFormatterPropertyEditor(BigDecimalFormatter.class));
        binder.registerCustomEditor(KualiDecimal.class, new KualiFormatterPropertyEditor(CurrencyFormatter.class)); 
        binder.registerCustomEditor(KualiInteger.class, new KualiFormatterPropertyEditor(KualiIntegerCurrencyFormatter.class));
        binder.registerCustomEditor(KualiPercent.class, new KualiFormatterPropertyEditor(PercentageFormatter.class));

	    super.initBinder(binder, request);
    }

}
