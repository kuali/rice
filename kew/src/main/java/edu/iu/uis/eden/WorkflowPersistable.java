package edu.iu.uis.eden;

import java.io.Serializable;

public interface WorkflowPersistable extends Serializable {

    /**
     * @deprecated this method is dangerous and not really deterministic, especially in regards to
     * circular references, etc.  In most of the cases where we use this, we are using it to simply
     * strip primary keys and lock version numbers from object graphs.
     */
  public Object copy(boolean preserveKeys);
  
}
