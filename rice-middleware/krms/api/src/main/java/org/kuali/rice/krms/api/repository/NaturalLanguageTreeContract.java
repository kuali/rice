/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.api.repository;

import java.util.List;

/**
 * Natural Language representation of a proposition
 * 
 * @see NaturalLanguageTree
 */
public interface NaturalLanguageTreeContract {

    /**
     * Returns the natural language representation for this node in the tree
     * 
     * @return the natural language representation for this node in the tree
     */
    String getNaturalLanguage();
    
    /**
     * Returns the natural language for children of this node
     * 
     * @return the natural language for children of this node
     */
    List<? extends NaturalLanguageTreeContract> getChildren();

    
}
