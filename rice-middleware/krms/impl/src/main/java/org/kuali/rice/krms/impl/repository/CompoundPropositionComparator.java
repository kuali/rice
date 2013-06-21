/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository;

import java.util.Comparator;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;

/**
 *
 * @author nwright
 */
public class CompoundPropositionComparator implements Comparator<PropositionDefinition> {
    
    @Override
    public int compare(PropositionDefinition o1, PropositionDefinition o2) {
        Integer seq1 = buildKey (o1);
        Integer seq2 = buildKey (o2);
        return seq1.compareTo(seq2);
    }
    
    private static final Integer ZERO = new Integer (0);
    private Integer buildKey (PropositionDefinition prop) {
        if (prop.getCompoundSequenceNumber() != null) {
            return prop.getCompoundSequenceNumber ();
        }
        return ZERO;
    }
}
