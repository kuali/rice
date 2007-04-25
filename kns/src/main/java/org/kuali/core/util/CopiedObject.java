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
package org.kuali.core.util;

import java.io.Serializable;

/**
 * Container class to simplify getting both a deepCopied object and its size returned from a single call to deepCopy.
 */
public class CopiedObject {
    private Serializable content;
    private long size;
    private long oldSize;

    public CopiedObject() {
        oldSize = -1;
    }


    /**
     * @return current value of bytes.
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the bytes attribute value.
     * 
     * @param bytes The bytes to set.
     */
    public void setSize(long bytes) {
        this.size = bytes;
    }

    /**
     * @return current value of cacheableObject.
     */
    public Serializable getContent() {
        return content;
    }

    /**
     * Sets the cacheableObject attribute value.
     * 
     * @param cacheableObject The cacheableObject to set.
     */
    public void setContent(Serializable cacheableObject) {
        this.content = cacheableObject;
    }


    /**
     * @return current value of oldSize.
     */
    public long getOldSize() {
        return oldSize;
    }


    /**
     * Sets the oldSize attribute value.
     * 
     * @param oldSize The oldSize to set.
     */
    public void setOldSize(long oldSize) {
        this.oldSize = oldSize;
    }


}
