/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.api.repository;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;

/**
 * a Sub-interface to help manage the translation methods as separate business
 * logic
 *
 * @author nwright
 */
public interface TranslateBusinessMethods {

    public String translateNaturalLanguageForObject(String naturalLanguageUsageId,
            String typeId, String krmsObjectId,
            String languageCode) throws RiceIllegalArgumentException;

    public String translateNaturalLanguageForProposition(String naturalLanguageUsageId,
            PropositionDefinition propositionDefinintion,
            String languageCode)
            throws RiceIllegalArgumentException;

    public NaturalLanguageTree translateNaturalLanguageTreeForProposition(String naturalLanguageUsageId,
            PropositionDefinition propositionDefinintion,
            String languageCode)
            throws RiceIllegalArgumentException;
}
