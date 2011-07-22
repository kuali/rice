package org.kuali.rice.kew.api.doctype;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractJaxbModelObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.core.api.util.CollectionUtils;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlRootElement(name = RoutePath.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RoutePath.Constants.TYPE_NAME, propOrder = {
        RoutePath.Elements.PROCESSES,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class RoutePath extends AbstractJaxbModelObject implements RoutePathContract {

    private static final long serialVersionUID = -7177305375323986864L;

    @XmlElementWrapper(name = Elements.PROCESSES, required = false)
    @XmlElement(name = Elements.PROCESS, required = false)
    private final List<Process> processes;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    private RoutePath() {
        this.processes = null;
    }

    private RoutePath(Builder builder) {
        this.processes = new ArrayList<Process>();
        if (builder.getProcesses() != null) {
            for (Process.Builder processBuilder : builder.getProcesses()) {
                this.processes.add(processBuilder.build());
            }
        }
    }
    
    public Process getPrimaryProcess() {
        for (Process process : processes) {
            if (process.isInitial()) {
                return process;
            }
        }        
        return null;
    }

    @Override
    public List<Process> getProcesses() {
        return CollectionUtils.unmodifiableListNullSafe(this.processes);
    }

    /**
     * A builder which can be used to construct {@link RoutePath} instances. Enforces the
     * constraints of the {@link RoutePathContract}.
     */
    public final static class Builder implements Serializable, ModelBuilder, RoutePathContract {

        private static final long serialVersionUID = -6916424305298043710L;

        private List<Process.Builder> processes;

        private Builder() {}

        public static Builder create() {
            Builder builder = new Builder();
            builder.setProcesses(new ArrayList<Process.Builder>());
            return builder;
        }

        public static Builder create(RoutePathContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            List<Process.Builder> processBuilders = new ArrayList<Process.Builder>();
            for (ProcessContract process : contract.getProcesses()) {
                processBuilders.add(Process.Builder.create(process));
            }
            builder.setProcesses(processBuilders);
            return builder;
        }

        public RoutePath build() {
            return new RoutePath(this);
        }

        @Override
        public List<Process.Builder> getProcesses() {
            return this.processes;
        }

        public void setProcesses(List<Process.Builder> processes) {
            this.processes = processes;
        }

    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "routePath";
        final static String TYPE_NAME = "RoutePathType";
    }

    /**
     * A private class which exposes constants which define the XML element names to use when this
     * object is marshalled to XML.
     */
    static class Elements {
        final static String PROCESSES = "processes";
        final static String PROCESS = "process";
    }

}
