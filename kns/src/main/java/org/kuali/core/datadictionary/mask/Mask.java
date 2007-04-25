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

import java.io.Serializable;


/**
 * Contains a mask configuration and method to mask a data value.
 * 
 * 
 */
public class Mask implements Serializable {
    static final public long serialVersionUID = 0L;
    
    private MaskFormatter maskFormatter;
    private Class maskFormatterClass;

    /**
     * Masks a data value with the configured maskFormatter;
     * 
     * @param value
     * @return
     */
    public String maskValue(Object value) {
        if (maskFormatter == null) {
            if (maskFormatterClass != null) {
                try {
                    maskFormatter = (MaskFormatter) maskFormatterClass.newInstance();
                }
                catch (Exception e) {
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
     * Sets the maskFormatter attribute value.
     * 
     * @param maskFormatter The maskFormatter to set.
     */
    public void setMaskFormatter(MaskFormatter maskFormatter) {
        this.maskFormatter = maskFormatter;
    }

    /**
     * Gets the maskFormatterClass attribute.
     * 
     * @return Returns the maskFormatterClass.
     */
    public Class getMaskFormatterClass() {
        return maskFormatterClass;
    }

    /**
     * Sets the maskFormatterClass attribute value.
     * 
     * @param maskFormatterClass The maskFormatterClass to set.
     */
    public void setMaskFormatterClass(Class maskFormatterClass) {
        this.maskFormatterClass = maskFormatterClass;
    }

}
