package org.kuali.rice.kim.impl.common.attribute;

import org.kuali.rice.kim.api.common.attribute.KimAttributeDataHistoryContract;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class KimAttributeDataHistoryBo extends KimAttributeDataBo implements KimAttributeDataHistoryContract {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KimAttributeDataHistoryBo.class);
    private static final long serialVersionUID = 1L;
    //private Long historyId;

    public abstract void setAssignedToHistoryId(Long s);

   // @Override
   // public Long getHistoryId() {
   //     return historyId;
   // }

   // public void setHistoryId(Long historyId) {
   //     this.historyId = historyId;
   // }


    /** creates a list of KimAttributeDataBos from attributes, kimTypeId, and assignedToId. */
    /*public static <T extends KimAttributeDataHistoryBo> List<T> createFrom(Class<T> type, Map<String, String> attributes, String kimTypeId) {
        if (attributes == null) {
            //purposely not using Collections.emptyList() b/c we do not want to return an unmodifiable list.
            return new ArrayList<T>();
        }
        List<T> attrs = new ArrayList<T>();
        for (Map.Entry<String, String> it : attributes.entrySet()) {
            //return attributes.entrySet().collect {
            KimTypeAttribute attr = getKimTypeInfoService().getKimType(kimTypeId).getAttributeDefinitionByName(it.getKey());
            KimType theType = getKimTypeInfoService().getKimType(kimTypeId);
            if (attr != null && StringUtils.isNotBlank(it.getValue())) {
                try {
                    T newDetail = type.newInstance();
                    newDetail.setKimAttributeId(attr.getKimAttribute().getId());
                    newDetail.setKimAttribute(KimAttributeBo.from(attr.getKimAttribute()));
                    newDetail.setKimTypeId(kimTypeId);
                    newDetail.setKimType(KimTypeBo.from(theType));
                    newDetail.setAttributeValue(it.getValue());
                    attrs.add(newDetail);
                } catch (InstantiationException e) {
                    LOG.error(e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    LOG.error(e.getMessage(), e);
                }

            }
        }
        return attrs;
    }*/
}
