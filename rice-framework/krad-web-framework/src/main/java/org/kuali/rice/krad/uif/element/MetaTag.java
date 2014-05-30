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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * Renders a meta tag in the head of the html document.
 *
 * <p>The meta tag component can be used to create meta tags by defining the attributes:
 * name, content and http_equiv</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "metaTag", parent = "Uif-MetaTag")
public class MetaTag extends ContentElementBase {
    private static final long serialVersionUID = -3479173950568700937L;

    private String name;
    private String content;
    private String http_equiv;

    public MetaTag() {
        super();
    }

    /**
     * Name attribute for meta tag.
     *
     * <p>Name attribute to be rendered on this meta tag</p>
     *
     * @return  name attribute of meta tag
     */
    @BeanTagAttribute
    public String getName() {
        return name;
    }

    /**
     * @see MetaTag#getName()
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Content attribute for meta tag.
     *
     * <p>Content attribute to be rendered on this meta tag</p>
     *
     * @return  content attribute of meta tag
     */
    @BeanTagAttribute
    public String getContent() {
        return content;
    }

    /**
     * @see MetaTag#getContent()
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Http_equiv attribute for meta tag.
     *
     * <p>Http_equiv attribute to be rendered on this meta tag</p>
     *
     * @return  http_equiv attribute of meta tag
     */
    @BeanTagAttribute
    public String getHttp_equiv() {
        return http_equiv;
    }

    /**
     * @see MetaTag#getHttp_equiv()
     */
    public void setHttp_equiv(String http_equiv) {
        this.http_equiv = http_equiv;
    }
}
