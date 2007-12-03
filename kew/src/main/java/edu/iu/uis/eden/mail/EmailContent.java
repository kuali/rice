/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
// Created on Jan 18, 2007

package edu.iu.uis.eden.mail;

/**
 * Class representing customizable content of an email message
 * TODO: supercede this with Spring framework and/or JavaMail mail classes
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailContent {
    private String subject;
    private String body;
    private boolean html;

    public EmailContent() {}

    public EmailContent(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public EmailContent(String subject, String body, boolean html) {
        this.subject = subject;
        this.body = body;
        this.html = html;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setBody(String body, boolean html) {
        this.body = body;
        this.html = html;
    }

    public String getBody() {
        return body;
    }

    public boolean isHtml() {
        return html;
    }

    public String toString() {
        return "[EmailContent: subject=" + subject + ", body=" + body + ", html=" + html + "]";
    }
}
