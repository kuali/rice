package org.kuali.rice.core.api.uif;

/** utility class for copying widgets. */
final class WidgetCopy {

    private WidgetCopy() {
        throw new IllegalArgumentException("do not call.");
    }

    public static RemotableAbstractWidget.Builder toBuilder(Widget w) {
        if (w == null) {
            throw new IllegalArgumentException("w is null");
        }

        if (w instanceof RemotableDatepicker || w instanceof RemotableDatepicker.Builder) return RemotableDatepicker.Builder.create();
        if (w instanceof RemotableTextExpand || w instanceof RemotableTextExpand.Builder) return RemotableTextExpand.Builder.create();

        if (w instanceof RemotableQuickFinder) {
            RemotableQuickFinder.Builder b = RemotableQuickFinder.Builder.create(((RemotableQuickFinder) w).getBaseLookupUrl(),  ((RemotableQuickFinder) w).getDataObjectClass());
            b.setFieldConversions(((RemotableQuickFinder) w).getFieldConversions());
            b.setLookupParameters(((RemotableQuickFinder) w).getLookupParameters());
            return b;
        } else if (w instanceof RemotableQuickFinder.Builder) {
            RemotableQuickFinder.Builder b = RemotableQuickFinder.Builder.create(((RemotableQuickFinder.Builder) w).getBaseLookupUrl(),  ((RemotableQuickFinder.Builder) w).getDataObjectClass());
            b.setFieldConversions(((RemotableQuickFinder.Builder) w).getFieldConversions());
            b.setLookupParameters(((RemotableQuickFinder.Builder) w).getLookupParameters());
            return b;
        }
        throw new UnsupportedOperationException(w.getClass().getName() + " not supported");
    }
}
