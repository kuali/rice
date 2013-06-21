/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository;

import java.util.Comparator;
import org.kuali.rice.krms.api.repository.typerelation.TypeTypeRelation;

/**
 *
 * @author nwright
 */
public class TypeTypeRelationSequenceComparator implements Comparator<TypeTypeRelation> {

    @Override
    public int compare(TypeTypeRelation o1, TypeTypeRelation o2) {
        Integer seq1 = o1.getSequenceNumber();
        if (seq1 == null) {
            seq1 = 0;
        }
        Integer seq2 = o2.getSequenceNumber();
        if (seq2 == null) {
            seq2 = 0;
        }
        return seq1.compareTo(seq2);
    }
}
