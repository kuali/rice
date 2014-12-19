/*
 * Copyright 2006-2014 The Kuali Foundation
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
package org.kuali.rice.core.api.mail;

import java.io.Serializable;

/**
 * Email Attachment
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MailAttachment implements Serializable {
    private byte[] content = null;
    private String type;
    private String fileName;

    public byte[] getContent() {
        return content;
    }

    /**
     * @param content The content of the attachment.
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    /**
     * @param type The attachment type
     */
    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName The name of the attachment
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
