/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package edu.iu.uis.eden.plugin.attributes;

import java.util.List;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routetemplate.ResolvedQualifiedRole;
import edu.iu.uis.eden.routetemplate.Role;

/**
 * A special type of attribute that is used exclusively for resolving abstract roles
 * to concrete responsibilities (users and groups).  A RoleAttribute provides resolution
 * for a set of abstract "role" names.  These are published via the {@link #getRoleNames()}
 * method, which returns a list of {@link Role}, which is a combination of class name (attribute implementation class),
 * abstract role name, and optional label and return url (DOCME: what is return url used for?).
 * 
 * <p>RoleAttribute lifecycle:
 * <ol>
 *   <li>A RoleAttribute is defined on a Rule, via the <code>role</code> element, with the syntax:
 *       <i>fully qualified class name</i><b>!</b><i>abstract role name</i>. E.g.:<br/>
 *   <blockquote><code>&lt;role&gt;edu.whatever.attribute.SomeAttribute!RoleName&lt;/role&gt;</code></blockquote>
 *   </li>
 *   <li>When the <code>&lt;role&gt;</code> element is parsed, the Rule's "responsibility" is set to the role element value
 *       and the responsibility is marked to indicate that it is a role ("R", {@link EdenConstants#RULE_RESPONSIBILITY_ROLE_ID})</li>
 *   <li>When a Rule that is configured with a Role responsibility is fired, {@link #getQualifiedRoleNames(String, DocumentContent)}
 *       is called to return a list of "qualified" role names.  Qualified role names are role names which have been qualified
 *       with some relevant contextual information (e.g. from the document) that is useful for subsequent responsibility
 *       resolution.</li>
 *   <li>{@link #resolveQualifiedRole(RouteContext, String, String)} is immediately called for each of the qualified role names
 *       returned in the previous step, and it returns a {@link ResolvedQualifiedRole} containing the
 *       list of concrete recipients ({@link Id}s).</li>
 *   <li>({@link edu.iu.uis.eden.routetemplate.UnqualifiedRoleAttribute} base class can be used to simplify this
 *       two-step process)</li>
 * </ol>
 * 
 * <p>Relationship to WorkflowAttribute: all RoleAttribute implementations are also
 * WorkflowAttribute implementations (is this true? should RoleAttribute extend WorkflowAttribute in that case?)
 * 
 * <p>Methods of WorkflowAttribute interface fulfilled by RoleAttribute:
 * <ol>
 *   <li>??</li>
 * </ol>
 * Methods of WorkflowAttribute interface not relevant to RoleAttribute:
 * <ol>
 *   <li>{@link WorkflowAttribute#isMatch(DocumentContent, List)}</li>
 *   <li>??</li>
 * </ol>
 * 
 * @see WorkflowAttribute
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface RoleAttribute {

    /**
     * List of {@link Role}s this RoleAttribute supports
     * @return list of {@link Role}s this RoleAttribute supports
     */
    public List<Role> getRoleNames();
    
    /**
     * Returns a String which represent the qualified role name of this role for the given
     * roleName and docContent.
     * @param roleName the role name (without class prefix)
     * @param documentContent the document content
     * @throws EdenUserNotFoundException
     */
    public List<String> getQualifiedRoleNames(String roleName, DocumentContent documentContent) throws EdenUserNotFoundException;
    
    /**
     * Returns a List of Workflow Users which are members of the given qualified role.
     * @param routeContext the RouteContext
     * @param roleName the roleName (without class prefix)
     * @param qualifiedRole one of the the qualified role names returned from the {@link #getQualifiedRoleNames(String, DocumentContent)} method
     * @return ResolvedQualifiedRole containing recipients, role label (most likely the roleName), and an annotation 
     */
    public ResolvedQualifiedRole resolveQualifiedRole(RouteContext routeContext, String roleName, String qualifiedRole) throws EdenUserNotFoundException;
    
}