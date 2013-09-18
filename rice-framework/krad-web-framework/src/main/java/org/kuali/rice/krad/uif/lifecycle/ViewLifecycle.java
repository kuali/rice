/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lifecycle object created during the view processing to hold event registrations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see LifecycleEventListener
 */
public class ViewLifecycle implements Serializable {
    private static final long serialVersionUID = -4767600614111642241L;

    public static enum LifecycleEvent {
        LIFECYCLE_COMPLETE
    }

    private List<EventRegistration> eventRegistrations;

    public ViewLifecycle() {
        eventRegistrations = new ArrayList<EventRegistration>();
    }

    /**
     * Registers the given component as a listener for the lifecycle complete event for the given event component.
     *
     * <p>
     * The {@link org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent#LIFECYCLE_COMPLETE} is thrown
     * immediately after the finalize phase has been completed for a component. This can be useful if a component needs
     * to set state after the lifecycle has been completed on another component (for example, it might depend on
     * properties of that component that are set during the finalize phase of that component)
     * </p>
     *
     * @param eventComponent component the event will occur for
     * @param listenerComponent component to invoke when the event is thrown
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent
     * @see LifecycleEventListener
     */
    public void registerLifecycleCompleteListener(Component eventComponent, LifecycleEventListener listenerComponent) {
        EventRegistration eventRegistration = new EventRegistration(LifecycleEvent.LIFECYCLE_COMPLETE, eventComponent,
                listenerComponent);

        eventRegistrations.add(eventRegistration);
    }

    /**
     * Invoked when an event occurs to invoke registered listeners.
     *
     * @param event event that has occurred
     * @param view view instance the lifecycle is being executed for
     * @param model object containing the model data
     * @param eventComponent component instance the event occurred on/for
     * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent
     */
    public void invokeEventListeners(LifecycleEvent event, View view, Object model, Component eventComponent) {
        for (EventRegistration registration : eventRegistrations) {
            if (registration.getEvent().equals(event) && (registration.getEventComponent() == eventComponent)) {
                registration.getEventListener().processEvent(event, view, model, eventComponent);
            }
        }
    }

    /**
     * Registration of an event.
     */
    protected class EventRegistration implements Serializable {
        private static final long serialVersionUID = -5077429381388641016L;

        private LifecycleEvent event;

        private Component eventComponent;
        private LifecycleEventListener eventListener;

        public EventRegistration(LifecycleEvent event, Component eventComponent,
                LifecycleEventListener eventListener) {
            this.event = event;
            this.eventComponent = eventComponent;
            this.eventListener = eventListener;
        }

        /**
         * Event the registration is for.
         *
         * @return event enum
         * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.LifecycleEvent
         */
        public LifecycleEvent getEvent() {
            return event;
        }

        /**
         * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.EventRegistration#getEvent()
         */
        public void setEvent(LifecycleEvent event) {
            this.event = event;
        }

        /**
         * Component instance the event should occur for/on.
         *
         * @return Component instance for event
         */
        public Component getEventComponent() {
            return eventComponent;
        }

        /**
         * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.EventRegistration#getEventComponent()
         */
        public void setEventComponent(Component eventComponent) {
            this.eventComponent = eventComponent;
        }

        /**
         * Listener class that should be invoked when the event occurs.
         *
         * @return LifecycleEventListener instance
         */
        public LifecycleEventListener getEventListener() {
            return eventListener;
        }

        /**
         * @see org.kuali.rice.krad.uif.lifecycle.ViewLifecycle.EventRegistration#getEventListener()
         */
        public void setEventListener(LifecycleEventListener eventListener) {
            this.eventListener = eventListener;
        }
    }
}
