package org.kuali.rice.shareddata.impl.state

import org.junit.Test
import org.kuali.rice.shareddata.api.state.State
import junit.framework.Assert


class StateBoTest {
  @Test
  public void testNotEqualsWithCampus() {
    State immutable = State.Builder.create("MI", "Michigan","US").build();
    StateBo bo = StateBo.from(immutable)
    Assert.assertFalse(bo.equals(immutable))
    Assert.assertFalse(immutable.equals(bo))
  }
}
