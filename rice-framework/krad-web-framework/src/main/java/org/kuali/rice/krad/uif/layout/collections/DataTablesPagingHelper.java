/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.layout.collections;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.CollectionGroup;
import org.kuali.rice.krad.uif.lifecycle.ViewLifecycle;
import org.kuali.rice.krad.uif.util.ColumnSort;
import org.kuali.rice.krad.uif.util.MultiColumnComparator;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataTablesPagingHelper {

    public static void processPagingRequest(View view, ViewModel form, CollectionGroup collectionGroup,
            DataTablesInputs dataTablesInputs) {
        if (view == null) {
            return;
        }

        String collectionGroupId = collectionGroup.getId();

        List<ColumnSort> newColumnSorts;
        synchronized (view) {
            // get the collection for this group from the model
            List<Object> modelCollection = ObjectPropertyUtils.getPropertyValue(form,
                    collectionGroup.getBindingInfo().getBindingPath());

            List<ColumnSort> oldColumnSorts =
                    (List<ColumnSort>) ViewLifecycle.getViewPostMetadata().getComponentPostData(collectionGroupId,
                            UifConstants.IdSuffixes.COLUMN_SORTS);

            newColumnSorts = buildColumnSorts(view, form, dataTablesInputs, collectionGroup);

            applyTableJsonSort(modelCollection, oldColumnSorts, newColumnSorts, collectionGroup, form, view);

            // set up the collection group properties related to paging in the collection group to set the bounds for
            // what needs to be rendered
            collectionGroup.setUseServerPaging(true);
            collectionGroup.setDisplayStart(dataTablesInputs.iDisplayStart);
            collectionGroup.setDisplayLength(dataTablesInputs.iDisplayLength);
        }

        // these other params above don't need to stay in the form after this request, but <collectionGroupId>_columnSorts
        // does so that we avoid re-sorting on each request.
        ViewLifecycle.getViewPostMetadata().addComponentPostData(collectionGroupId,
                UifConstants.IdSuffixes.COLUMN_SORTS, newColumnSorts);
    }

    /**
     * Extract the sorting information from the DataTablesInputs into a more generic form.
     *
     * @param form object containing the view's data
     * @param view posted view containing the collection
     * @param dataTablesInputs the parsed request data from dataTables
     * @return the List of ColumnSort elements representing the requested sort columns, types, and directions
     */
    private static List<ColumnSort> buildColumnSorts(View view, ViewModel form, DataTablesInputs dataTablesInputs,
            CollectionGroup collectionGroup) {
        int[] sortCols = dataTablesInputs.iSortCol_; // cols being sorted on (for multi-col sort)
        boolean[] sortable = dataTablesInputs.bSortable_; // which columns are sortable
        String[] sortDir = dataTablesInputs.sSortDir_; // direction to sort

        // parse table options to gather the sort types
        String aoColumnDefsValue = (String) form.getViewPostMetadata().getComponentPostData(collectionGroup.getId(),
                UifConstants.TableToolsKeys.AO_COLUMN_DEFS);

        JsonArray jsonColumnDefs = null;
        if (!StringUtils.isEmpty(aoColumnDefsValue)) { // we'll parse this using a JSON library to make things simpler
            // function definitions are not allowed in JSON
            aoColumnDefsValue = aoColumnDefsValue.replaceAll("function\\([^)]*\\)\\s*\\{[^}]*\\}", "\"REDACTED\"");
            JsonReader jsonReader = Json.createReader(new StringReader(aoColumnDefsValue));
            jsonColumnDefs = jsonReader.readArray();
        }

        List<ColumnSort> columnSorts = new ArrayList<ColumnSort>(sortCols.length);

        for (int sortColsIndex = 0; sortColsIndex < sortCols.length; sortColsIndex++) {
            int sortCol = sortCols[sortColsIndex]; // get the index of the column being sorted on

            if (sortable[sortCol]) {
                String sortType = getSortType(jsonColumnDefs, sortCol);
                ColumnSort.Direction sortDirection = ColumnSort.Direction.valueOf(sortDir[sortColsIndex].toUpperCase());
                columnSorts.add(new ColumnSort(sortCol, sortDirection, sortType));
            }
        }

        return columnSorts;
    }

    /**
     * Get the sort type string from the parsed column definitions object.
     *
     * @param jsonColumnDefs the JsonArray representation of the aoColumnDefs property from the RichTable template
     * options
     * @param sortCol the index of the column to get the sort type for
     * @return the name of the sort type specified in the template options, or the default of "string" if none is
     * found.
     */
    private static String getSortType(JsonArray jsonColumnDefs, int sortCol) {
        String sortType = "string"; // default to string if nothing is spec'd

        if (jsonColumnDefs != null) {
            JsonObject column = jsonColumnDefs.getJsonObject(sortCol);

            if (column.containsKey("sType")) {
                sortType = column.getString("sType");
            }
        }
        return sortType;
    }

    /**
     * Sort the given modelCollection (in place) according to the specified columnSorts.
     *
     * <p>Not all columns will necessarily be directly mapped to the modelCollection, so the collectionGroup and view
     * are available as well for use in calculating those other column values.  However, if all the columns are in fact
     * mapped to the elements of the modelCollection, subclasses should be able to easily override this method to
     * provide custom sorting logic.</p>
     *
     * <p>
     * Create an index array and sort that. The array slots represents the slots in the modelCollection, and
     * the values are indices to the elements in the modelCollection.  At the end, we'll re-order the
     * modelCollection so that the elements are in the collection slots that correspond to the array locations.
     *
     * A small example may be in order.  Here's the incoming modelCollection:
     *
     * modelCollection = { "Washington, George", "Adams, John", "Jefferson, Thomas", "Madison, James" }
     *
     * Initialize the array with its element references all matching initial positions in the modelCollection:
     *
     * reSortIndices = { 0, 1, 2, 3 }
     *
     * After doing our sort in the array (where we sort indices based on the values in the modelCollection):
     *
     * reSortIndices = { 1, 2, 3, 0 }
     *
     * Then, we go back and apply that ordering to the modelCollection:
     *
     * modelCollection = { "Adams, John", "Jefferson, Thomas", "Madison, James", "Washington, George" }
     *
     * Why do it this way instead of just sorting the modelCollection directly?  Because we may need to know
     * the original index of the element e.g. for the auto sequence column.
     * </p>
     *
     * @param modelCollection the collection to sort
     * @param oldColumnSorts the sorting that reflects the current state of the collection
     * @param newColumnSorts the sorting to apply to the collection
     * @param collectionGroup the CollectionGroup that is being rendered
     * @param form object containing the view's data
     * @param view the view
     */
    protected static void applyTableJsonSort(final List<Object> modelCollection, List<ColumnSort> oldColumnSorts,
            final List<ColumnSort> newColumnSorts, final CollectionGroup collectionGroup, ViewModel form,
            final View view) {

        boolean isCollectionEmpty = CollectionUtils.isEmpty(modelCollection);
        boolean isSortingSpecified = !CollectionUtils.isEmpty(newColumnSorts);
        boolean isSortOrderChanged = newColumnSorts != oldColumnSorts && !newColumnSorts.equals(oldColumnSorts);

        if (!isCollectionEmpty && isSortingSpecified && isSortOrderChanged) {
            Integer[] sortIndices = new Integer[modelCollection.size()];
            for (int i = 0; i < sortIndices.length; i++) {
                sortIndices[i] = i;
            }

            MultiColumnComparator comparator = new MultiColumnComparator(modelCollection, collectionGroup,
                    newColumnSorts, form, view);
            Arrays.sort(sortIndices, comparator);

            // apply the sort to the modelCollection
            Object[] sorted = new Object[sortIndices.length];
            for (int i = 0; i < sortIndices.length; i++) {
                sorted[i] = modelCollection.get(sortIndices[i]);
            }

            for (int i = 0; i < sorted.length; i++) {
                modelCollection.set(i, sorted[i]);
            }
        }
    }

    /**
     * Input command processor for supporting DataTables server-side processing.
     *
     * @see <a href="http://datatables.net/usage/server-side">http://datatables.net/usage/server-side</a>
     */
    public static class DataTablesInputs {
        private static final String DISPLAY_START = "iDisplayStart";
        private static final String DISPLAY_LENGTH = "iDisplayLength";
        private static final String COLUMNS = "iColumns";
        private static final String REGEX = "bRegex";
        private static final String REGEX_PREFIX = "bRegex_";
        private static final String SORTABLE_PREFIX = "bSortable_";
        private static final String SORTING_COLS = "iSortingCols";
        private static final String SORT_COL_PREFIX = "iSortCol_";
        private static final String SORT_DIR_PREFIX = "sSortDir_";
        private static final String DATA_PROP_PREFIX = "mDataProp_";
        private static final String ECHO = "sEcho";

        private final int iDisplayStart, iDisplayLength, iColumns, iSortingCols, sEcho;

        // TODO: All search related options are commented out of this class.
        // If we implement search for datatables we'll want to re-activate that code to capture the configuration
        // values from the request

        //        private final String sSearch;
        //        private final Pattern patSearch;

        private final boolean bRegex;
        private final boolean[] /*bSearchable_,*/ bRegex_, bSortable_;
        private final String[] /*sSearch_,*/ sSortDir_, mDataProp_;

        //        private final Pattern[] patSearch_;

        private final int[] iSortCol_;

        public DataTablesInputs(HttpServletRequest request) {
            String s;
            iDisplayStart = (s = request.getParameter(DISPLAY_START)) == null ? 0 : Integer.parseInt(s);
            iDisplayLength = (s = request.getParameter(DISPLAY_LENGTH)) == null ? 0 : Integer.parseInt(s);
            iColumns = (s = request.getParameter(COLUMNS)) == null ? 0 : Integer.parseInt(s);
            bRegex = (s = request.getParameter(REGEX)) == null ? false : new Boolean(s);

            //            patSearch = (sSearch = request.getParameter("sSearch")) == null
            //                    || !bRegex ? null : Pattern.compile(sSearch);
            //            bSearchable_ = new boolean[iColumns];
            //            sSearch_ = new String[iColumns];
            //            patSearch_ = new Pattern[iColumns];

            bRegex_ = new boolean[iColumns];
            bSortable_ = new boolean[iColumns];

            for (int i = 0; i < iColumns; i++) {

                //                bSearchable_[i] = (s = request.getParameter("bSearchable_" + i)) == null ? false
                //                        : new Boolean(s);

                bRegex_[i] = (s = request.getParameter(REGEX_PREFIX + i)) == null ? false : new Boolean(s);

                //                patSearch_[i] = (sSearch_[i] = request.getParameter("sSearch_"
                //                        + i)) == null
                //                        || !bRegex_[i] ? null : Pattern.compile(sSearch_[i]);

                bSortable_[i] = (s = request.getParameter(SORTABLE_PREFIX + i)) == null ? false : new Boolean(s);
            }

            iSortingCols = (s = request.getParameter(SORTING_COLS)) == null ? 0 : Integer.parseInt(s);
            iSortCol_ = new int[iSortingCols];
            sSortDir_ = new String[iSortingCols];

            for (int i = 0; i < iSortingCols; i++) {
                iSortCol_[i] = (s = request.getParameter(SORT_COL_PREFIX + i)) == null ? 0 : Integer.parseInt(s);
                sSortDir_[i] = request.getParameter(SORT_DIR_PREFIX + i);
            }

            mDataProp_ = new String[iColumns];

            for (int i = 0; i < iColumns; i++) {
                mDataProp_[i] = request.getParameter(DATA_PROP_PREFIX + i);
            }

            sEcho = (s = request.getParameter(ECHO)) == null ? 0 : Integer.parseInt(s);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(super.toString());
            sb.append("\n\t" + DISPLAY_START + " = ");
            sb.append(iDisplayStart);
            sb.append("\n\t" + DISPLAY_LENGTH + " = ");
            sb.append(iDisplayLength);
            sb.append("\n\t" + COLUMNS + " = ");
            sb.append(iColumns);

            //            sb.append("\n\tsSearch = ");
            //            sb.append(sSearch);

            sb.append("\n\t" + REGEX + " = ");
            sb.append(bRegex);

            for (int i = 0; i < iColumns; i++) {

                //                sb.append("\n\tbSearchable_").append(i).append(" = ");
                //                sb.append(bSearchable_[i]);

                //                sb.append("\n\tsSearch_").append(i).append(" = ");
                //                sb.append(sSearch_[i]);

                sb.append("\n\t").append(REGEX_PREFIX).append(i).append(" = ");
                sb.append(bRegex_[i]);
                sb.append("\n\t").append(SORTABLE_PREFIX).append(i).append(" = ");
                sb.append(bSortable_[i]);
            }

            sb.append("\n\t").append(SORTING_COLS);
            sb.append(iSortingCols);

            for (int i = 0; i < iSortingCols; i++) {
                sb.append("\n\t").append(SORT_COL_PREFIX).append(i).append(" = ");
                sb.append(iSortCol_[i]);
                sb.append("\n\t").append(SORT_DIR_PREFIX).append(i).append(" = ");
                sb.append(sSortDir_[i]);
            }

            for (int i = 0; i < iColumns; i++) {
                sb.append("\n\t").append(DATA_PROP_PREFIX).append(i).append(" = ");
                sb.append(mDataProp_[i]);
            }

            sb.append("\n\t" + ECHO + " = ");
            sb.append(sEcho);

            return sb.toString();
        }
    }
}
