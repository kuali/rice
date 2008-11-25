<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<kul:page headerTitle="Route Log" transactionalDocument="false"
	showDocumentInfo="false" htmlFormAction="RouteLog" docTitle="Route Log">



	<kul:tabTop
		tabTitle="ID: ${routeHeader.routeHeaderId}"
		defaultOpen="true"
		>
		<div class="tab-container" align=center>
		<table width="100%" border=0 cellpadding=0 cellspacing=0
			class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left">
				<bean-el:message
					key="routeLog.RouteLog.header.label.documentTitle" />
				</kul:htmlAttributeHeaderCell>

				<td class="datacell" colspan="3"><c:out
					value="${routeHeader.docTitle}" />&nbsp;</td>

			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left">
				<bean-el:message
					key="routeLog.RouteLog.header.label.documentType" />
					</kul:htmlAttributeHeaderCell>
				<td width="25%" class="datacell"><a
					href="
               						<c:url value="DocumentType.do">
										<c:param name="docTypeId" value="${routeHeader.documentTypeId}" />
										<c:param name="methodToCall" value="report"/>
									</c:url>"><c:out
					value="${routeHeader.documentType.label}" /> </a>&nbsp; &nbsp;</td>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.created" /></kul:htmlAttributeHeaderCell>
				<td class="datacell" width="25%"><fmt:formatDate
					value="${routeHeader.createDate}"
					pattern="${Constants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.initiator" /></kul:htmlAttributeHeaderCell>
				<td  class="datacell" width="25%"><c:set
					var="initiatorDisplayName"
					value="${routeHeader.initiatorUser.displayName}" /> <c:if
					test="${kewUserSession.workflowUser.workflowId != routeHeader.initiatorUser.workflowId}">
					<c:set var="initiatorDisplayName"
						value="${routeHeader.initiatorUser.displayNameSafe}" />
				</c:if> <a
					href="
               						<c:url value="${UrlResolver.userReportUrl}">
										<c:param name="workflowId" value="${routeHeader.initiatorWorkflowId}" />
										<c:param name="methodToCall" value="report" />
										<c:param name="showEdit" value="no" />
									</c:url>"><c:out
					value="${initiatorDisplayName}" /> </a>&nbsp;</td>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.lastModified" /></kul:htmlAttributeHeaderCell>
				<td  class="datacell" width="25%"><fmt:formatDate
					value="${routeHeader.statusModDate}"
					pattern="${Constants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.routeStatus" /></kul:htmlAttributeHeaderCell>
				<td class="datacell" width="25%"><b><c:out
					value="${routeHeader.routeStatusLabel}" /></b>&nbsp;</td>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.lastApproved" /></kul:htmlAttributeHeaderCell>
				<td  class="datacell" width="25%"><fmt:formatDate
					value="${routeHeader.approvedDate}"
					pattern="${Constants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.routeNodes" /></kul:htmlAttributeHeaderCell>
				<td class="datacell"><c:out
					value="${routeHeader.currentRouteLevelName}" />&nbsp;</td>
				<kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message
					key="routeLog.RouteLog.header.label.finalized" /></kul:htmlAttributeHeaderCell>
				<td class="datacell" width="25%"><fmt:formatDate
					value="${routeHeader.finalizedDate}"
					pattern="${Constants.DEFAULT_DATE_FORMAT_PATTERN}" />&nbsp;</td>

			</tr>
		</table>
		</div>
	</kul:tabTop>
	<!-- Need to do this in order to pass the value into a tag attribute -->
	<bean:define id="actionsTakenLabel">
 		<bean-el:message key="routeLog.RouteLog.actionsTaken.label.actionsTaken"/>
	</bean:define>

	<c:if test="${! empty routeHeader.actionsTaken}">
	<kul:tab
		tabTitle="${actionsTakenLabel}"
		defaultOpen="true">
		<div class="tab-container" align=center>
			<table width="100%" border=0 cellspacing=0 cellpadding=0>
                  <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                    <!-- might need to remove -->


                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.action"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.takenBy"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.forDelegator"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.timeDate"/>
                    </kul:htmlAttributeHeaderCell>
                    <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.RouteLog.actionsTaken.label.annotation"/>
                    </kul:htmlAttributeHeaderCell>

                  </tr>

					<c:forEach var="actionTaken" items="${routeHeader.actionsTaken}" varStatus="atStatus">
                            <tr >

	                          	<kul:htmlAttributeHeaderCell scope="col" align="left">
	                          		<c:if test="${! empty actionTaken.actionRequests}">
	                            	<a id="A<c:out value="${atStatus.count}" />" onclick="rend(this, false)">
		                              <img src="images/tinybutton-show.gif" alt="show" width=45 height=15 border=0
		                              align=absmiddle id="F<c:out value="${atStatus.count}" />"></a>
		                            </c:if>&nbsp;
		                        </kul:htmlAttributeHeaderCell>

		                        <td align="center" class="datacell">
	                               <b><c:out value="${actionTaken.actionTakenLabel}" /></b>
	                            </td>
	                            <td align="left" class="datacell">
	                               <a style="color:white" href="
										<c:url value="${UrlResolver.userReportUrl}">
											<c:param name="workflowId" value="${actionTaken.workflowId}" />
											<c:param name="methodToCall" value="report" />
											<c:param name="showEdit" value="no" />
										</c:url>"><c:out value="${actionTaken.workflowUser.displayName}" />
									</a>
	                       		</td>
	                       		<td align="left" class="headercell4">
							        <c:if test="${actionTaken.forDelegator}">
							        <c:set var="actionDisplayName" value="${actionTaken.workflowUser.displayName}"/>
							      <c:if test="${kewUserSession.workflowUser.workflowId != actionTaken.workflowUser.workflowId}">
  							        <c:set var="actionDisplayName" value="${actionTaken.workflowUser.displayNameSafe}"/>
							      </c:if>
											<a style="color:white" href="
									  			<c:url value="${UrlResolver.userReportUrl}">
													<c:param name="workflowId" value="${actionTaken.delegatorWorkflowId}" />
													<c:param name="methodToCall" value="report" />
													<c:param name="showEdit" value="no" />
												</c:url>"><c:out value="${actionDisplayName}" />
											</a>
							         </c:if>&nbsp;
						         </td>
						         <td align="center" class="headercell4">
						             <b><fmt:formatDate value="${actionTaken.actionDate}" pattern="${Constants.DEFAULT_DATE_FORMAT_PATTERN}" /></b>
						         </td>
						         <td align="left" class="headercell4">
						             <c:out value="${actionTaken.annotation}" />&nbsp;
						             <!--ActionTakenId:<c:out value="${actionTaken.actionTakenId}" />-->
						         </td>

                            </tr>

                            <tr id="G<c:out value="${atStatus.count}" />" style="display: none;" >
                            	<td width=8 class="bordercell-left"><img src="images/pixel_clear.gif" alt="" width=8 height=8></td>
                            	<td>&nbsp;</td>
								<td colspan="5">
									<table width="100%" border=0 cellspacing=0 cellpadding=0>
					                  <tr>
					                    <td align="center" class="headercell3-b-l" width="5%">&nbsp;</td>
					  		            <td align="center" width="15%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></td>
					  		            <td align="center" width="15%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></td>
					  		            <td align="center" width="22%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></td>
					  		            <td align="center" width="40%" class="headercell3-b-l"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></td>
					                  </tr>
	                              	<c:forEach var="actionRequest" items="${actionTaken.actionRequests}" varStatus="arStatus">
	                              		<c:if test="${actionRequest.parentActionRequest == null}">
											<c:set var="level" value="1" scope="request"/>
											<c:set var="index" value="${atStatus.count}z${arStatus.index}" scope="request" />
											<c:set var="actionRequest" value="${actionRequest}" scope="request"/>
											<c:set var="KualiForm" value="${KualiForm}"/>
											<c:set var="hasChildren" value="${! empty actionRequest.childrenRequests}" scope="request"/>
											<jsp:include page="ActionRequest.jsp" flush="true" />
										</c:if>
									</c:forEach>
									</table>
                              	</td>
                              	<td width=8 class="bordercell-right"><img src="images/pixel_clear.gif" alt="" width=8 height=8></td>
                            </tr>
	                </c:forEach>
                </table>
		</div>
		</kul:tab>
		</c:if>

		 <c:if test="${KualiForm.pendingActionRequestCount > 0}">
		<kul:tab
		tabTitle="Pending Action Requests"
		defaultOpen="true">
		<div class="tab-container" align=center>
			<table width="100%" border=0 cellspacing=0 cellpadding=0>
			                  <tr>
			                    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
			  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></kul:htmlAttributeHeaderCell>
			  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></kul:htmlAttributeHeaderCell>
			  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></kul:htmlAttributeHeaderCell>
			  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></kul:htmlAttributeHeaderCell>
			                  </tr>

			                        <c:set var="shiftIndex" value="5000" scope="request"/>
			                        <c:forEach var="actionRequest" items="${KualiForm.rootRequests}" varStatus="arStatus">
			                        	<c:if test="${actionRequest.pending}">
			                        	     <c:set var="level" value="0" scope="request"/>
					                         <c:set var="index" value="${arStatus.index + shiftIndex}" scope="request" />
					                         <c:set var="actionRequest" value="${actionRequest}" scope="request"/>
					                         <jsp:include page="ActionRequest.jsp" flush="true" />
					                      </c:if>
			                        </c:forEach>

			                </table>
		</div>
		</kul:tab>
		</c:if>

		 <c:if test="${KualiForm.lookFuture}">
		<kul:tab
		tabTitle="Future Action Requests"
		defaultOpen="true">
		<div class="tab-container" align=center>
			<table width="100%" border=0 cellspacing=0 cellpadding=0>

                	<tr>
                		<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                		<c:choose>
	                		<c:when test="${KualiForm.showFuture}">
		                		<td>
		                			<c:choose>
		                				<c:when test="${KualiForm.showFutureHasError}">
		                					<div class="exception-error-div"><span class="exception-error"><c:out value="${KualiForm.showFutureError}"/></span></div>
		                				</c:when>
		                				<c:otherwise>
					                <table width="100%" border=0 cellspacing=0 cellpadding=0>
					                  <tr>
					                    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
					  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.action"/></kul:htmlAttributeHeaderCell>
					  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.requestedOf"/></kul:htmlAttributeHeaderCell>
					  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.timeDate"/></kul:htmlAttributeHeaderCell>
					  		            <kul:htmlAttributeHeaderCell scope="col" align="left"><bean-el:message key="routeLog.ActionRequests.actionRequests.label.annotation"/></kul:htmlAttributeHeaderCell>
					                  </tr>
					                  <tr>
					                  	<td>
					                        <c:set var="shiftIndex" value="6000" scope="request"/>
					                        <c:forEach var="actionRequest" items="${KualiForm.futureRootRequests}" varStatus="arStatus">
					                        	<c:if test="${actionRequest.pending}">
					                        	     <c:set var="level" value="0" scope="request"/>
							                         <c:set var="index" value="${arStatus.index + shiftIndex}" scope="request" />
							                         <c:set var="actionRequest" value="${actionRequest}" scope="request"/>
							                         <jsp:include page="ActionRequest.jsp" flush="true" />
							                      </c:if>
					                        </c:forEach>
					                    </td>
					                  </tr>
					                </table>
		                				</c:otherwise>
		                			</c:choose>
		                		</td>
		                	</c:when>
		                	<c:otherwise>
		                		<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
		                	</c:otherwise>
		                </c:choose>
						<kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                	</tr>

                </table>
		</div>
		</kul:tab>
		</c:if>

	<kul:panelFooter />




</kul:page>
