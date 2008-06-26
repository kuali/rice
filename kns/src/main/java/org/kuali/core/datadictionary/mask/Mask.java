/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.core.datadictionary.mask;

/**
    The displayMask element specifies the type of masking to
    be used to hide the value from un-authorized users.
    There are three types of masking.
 */
public class Mask {   
    protected MaskFormatter maskFormatter;
    protected Class<? extends MaskFormatter> maskFormatterClass;

    /**
     * Masks a data value with the configured maskFormatter;
     */
    public String maskValue(Object value) {
        if (maskFormatter == null) {
            if (maskFormatterClass != null) {
                try {
                    maskFormatter = (MaskFormatter) maskFormatterClass.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to create instance of mask formatter class: " + maskFormatterClass.getName());
                }
            }
            else {
                throw new RuntimeException("Mask formatter not set for secure field.");
            }
        }

        return maskFormatter.maskValue(value);
    }

    /**
     * Gets the maskFormatter attribute.
     * 
     * @return Returns the maskFormatter.
     */
    public MaskFormatter getMaskFormatter() {
        return maskFormatter;
    }

    /**
     * Instance of an object implementing the MaskFormatter interface to be used for masking field values.
     */
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * Gets the maskFormatterClass attribute.
     * 
     * @return Returns the maskFormatterClass.
     */
    public Class<? extends MaskFormatter> getMaskFormatterClass() {
        return maskFormatterClass;
    }

    /**
      The maskFormatterClass element is used when a custom masking
      algorithm is desired.  This element specifies the name of a
      class that will perform the masking for unauthorized users.
    */
    public void setMaskFormatterClass(Class<? extends MaskFormatter> maskFormatterClass) {
        this.maskFormatterClass = maskFormatterClass;
    }

}
