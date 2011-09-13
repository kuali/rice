package org.kuali.rice.core.web.cache;

import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.web.form.UifFormBase;

import java.util.ArrayList;
import java.util.Collection;

public final class CacheAdminForm extends UifFormBase {
    private Tree<String, String> cacheTree = new Tree<String, String>();

    //it would be nice if this were a cacheTree of selected nodes so it doesn't have to be parsed
    private Collection<String> flush = new ArrayList<String>();

    public void setCacheTree(Tree<String, String> cacheTree) {
        this.cacheTree = cacheTree;
    }

    public Tree<String, String> getCacheTree() {
        return cacheTree;
    }

    public Collection<String> getFlush() {
        return flush;
    }

    public void setFlush(Collection<String> flush) {
        this.flush = flush;
    }
}
