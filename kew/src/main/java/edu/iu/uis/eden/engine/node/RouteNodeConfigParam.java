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
package edu.iu.uis.eden.engine.node;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A route node definition configuration parameter.  RouteNodeConfigParameters are
 * extracted when the route node is parsed, and depend on route node implementation.
 * (well, they actually depend on the route node parser because the parser is what
 * will parse them, but the parser is not specialized per-node-type at this point) 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 *
 */
public class RouteNodeConfigParam extends KeyValuePair implements Serializable {
    private static final long serialVersionUID = 5592421070149273014L;

    /**
     * Primary key
     */
    private Long id;
    /**
     * Foreign key to routenode table
     */
    private RouteNode routeNode;

    public RouteNodeConfigParam() {}

    public RouteNodeConfigParam(RouteNode routeNode, String key, String value) {
        super(key, value);
        this.routeNode = routeNode;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the routeNode
     */
    public RouteNode getRouteNode() {
        return this.routeNode;
    }
    /**
     * @param routeNode the routeNode to set
     */
    public void setRouteNode(RouteNode routeNode) {
        this.routeNode = routeNode;
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", id)
                                        .append("routeNode", routeNode == null ? null : routeNode.getRouteNodeId())
                                        .append("key", key)
                                        .append("value", value)
                                        .toString();
    }
}