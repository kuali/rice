/*
 * Copyright 2006-2013 The Kuali Foundation
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

package org.kuali.rice.krms.test;

import org.junit.Test;

/**
 *
 */
public class RuleManagementContextDefinitionTest {
    /*
    ////
    //// context methods
    ////
    */

    @Test
    public void testCreateContext() {

    }
    /*
    @Override
    public ContextDefinition createContext(ContextDefinition contextDefinition) throws RiceIllegalArgumentException {
        return this.contextBoService.createContext(contextDefinition);
    }

    */

    @Test
    public void testFindCreateContext() {

    }
    /*
    @Override
    public ContextDefinition findCreateContext(ContextDefinition contextDefinition) throws RiceIllegalArgumentException {
        ContextDefinition orig = this.contextBoService.getContextByNameAndNamespace(contextDefinition.getName(), contextDefinition.getNamespace());

        if (orig != null) {
            return orig;
        }

        return this.contextBoService.createContext(contextDefinition);
    }
    */

    @Test
    public void testUpdateContext() {

    }
    /*
    @Override
    public void updateContext(ContextDefinition contextDefinition) throws RiceIllegalArgumentException {
        this.contextBoService.updateContext(contextDefinition);
    }
    */

    @Test
    public void testDeleteContext() {

    }
    /*
    @Override
    public void deleteContext(String id) throws RiceIllegalArgumentException {
        throw new RiceIllegalArgumentException("not implemented yet");
    }
    */

    @Test
    public void testGetContext() {

    }
    /*
    @Override
    public ContextDefinition getContext(String id) throws RiceIllegalArgumentException {
        return this.contextBoService.getContextByContextId(id);
    }
    */

    @Test
    public void testGetContextByNameAndNamespace() {

    }
    /*
    @Override
    public ContextDefinition getContextByNameAndNamespace(String name, String namespace) throws RiceIllegalArgumentException {
        return this.contextBoService.getContextByNameAndNamespace(name, namespace);
    }

     */

    @Test
    public void testFindContextIds() {

    }
    /*
        @Override
    public List<String> findContextIds(QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        GenericQueryResults<ContextBo> results = getCriteriaLookupService().lookup(ContextBo.class, queryByCriteria);

        List<String> list = new ArrayList<String> ();
        for (ContextBo bo : results.getResults()) {
            list.add (bo.getId());
        }

        return list;
    }
     */
}
