<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<%-- kul:rowDisplay
  rows="${_field.containerRows}"
  numberOfColumns="${_isMaintenance ? _numberOfColumns : _field.numberOfColumnsForCollection}"
  depth="${_depth + 1}"
  rowsHidden="${_rowsHidden}"
  rowsReadOnly="${_rowsReadOnly}"/ --%>
<kul:rowDisplay
  rows="${_rows}"
  numberOfColumns="${_numberOfColumns}"
  depth="${_depth}"
  rowsHidden="${_rowsHidden}"
  rowsReadOnly="${_rowsReadOnly}"/>