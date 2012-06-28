package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.widget.RichTable;

import java.util.List;
import java.util.Set;

/**
 * Content element that renders a table using the {@link RichTable} widget configured with an Ajax (or Javascript)
 * data source
 *
 * <p>
 * Note this is different from the table layout manager in that it does not render nested components. The data is
 * provided directly to the rich table widget which will create the table rows (unlike the table layout which creates
 * the table from components then invokes the table plugin to decorate). Therefore this component just creates a table
 * element tag and invokes the rich table script
 * </p>
 *
 * <p>
 * Nested HTML can be given through the rich table data. However generally this will be read-only data with possibly
 * some inquiry links
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataTable extends ContentElementBase {
    private static final long serialVersionUID = 6201998559169962349L;

    private RichTable richTable;

    public DataTable() {
        super();
    }

    /**
     * @see org.kuali.rice.krad.uif.component.Component#getComponentsForLifecycle()
     */
    @Override
    public List<Component> getComponentsForLifecycle() {
        List<Component> components = super.getComponentsForLifecycle();

        components.add(richTable);

        return components;
    }

    /**
     * Widget that will render the data table client side
     *
     * @return RichTable instance
     */
    public RichTable getRichTable() {
        return richTable;
    }

    /**
     * Setter for the rich table widget
     *
     * @param richTable
     */
    public void setRichTable(RichTable richTable) {
        this.richTable = richTable;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#getAjaxSource()
     */
    public String getAjaxSource() {
        if (richTable != null) {
            return richTable.getAjaxSource();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#setAjaxSource(java.lang.String)
     */
    public void setAjaxSource(String ajaxSource) {
        if (richTable != null) {
            richTable.setAjaxSource(ajaxSource);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#getHiddenColumns()
     */
    public Set<String> getHiddenColumns() {
        if (richTable != null) {
            return richTable.getHiddenColumns();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#setHiddenColumns(java.util.Set<java.lang.String>)
     */
    public void setHiddenColumns(Set<String> hiddenColumns) {
        if (richTable != null) {
            richTable.setHiddenColumns(hiddenColumns);
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#getSortableColumns()
     */
    public Set<String> getSortableColumns() {
        if (richTable != null) {
            return richTable.getSortableColumns();
        }

        return null;
    }

    /**
     * @see org.kuali.rice.krad.uif.widget.RichTable#setSortableColumns(java.util.Set<java.lang.String>)
     */
    public void setSortableColumns(Set<String> sortableColumns) {
        if (richTable != null) {
            richTable.setSortableColumns(sortableColumns);
        }
    }

}
