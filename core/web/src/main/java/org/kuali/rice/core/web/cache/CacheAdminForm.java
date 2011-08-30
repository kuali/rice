package org.kuali.rice.core.web.cache;

import org.kuali.rice.core.api.util.tree.Tree;
import org.kuali.rice.krad.web.form.UifFormBase;

public final class CacheAdminForm extends UifFormBase {
    private Tree<?, String> cacheTree = new Tree<Object, String>();

    public void setCacheTree(Tree<?, String> cacheTree) {
        this.cacheTree = cacheTree;
    }

    public Tree<?, String> getCacheTree() {
        return cacheTree;
    }
}
