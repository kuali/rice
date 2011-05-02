package org.kuali.rice.krms.impl.repository

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase

public class AgendaItemBo extends PersistableBusinessObjectBase {

	def String id
	def String agendaId
	def String ruleId
	def String subAgendaId
	def String nextTrueId
	def String nextFalseId
	def String nextAfterId 
	
	def AgendaItemBo whenTrue;
	def AgendaItemBo whenFalse;
	def AgendaItemBo always;
	
	public String getUl(AgendaItemBo firstItem) {
		return ("<ul>" + getUlHelper(firstItem) + "</ul>");
	}
	
	public String getUlHelper(AgendaItemBo item) {
		StringBuilder sb = new StringBuilder();
		sb.append("<li>" + ruleId + "</li>");
		if (whenTrue != null) {
			sb.append("<ul><li>when true</li><ul>");
			sb.append(getUlHelper(whenTrue));
			sb.append("</ul></ul>");
		}
		if (whenFalse != null) {
			sb.append("<ul><li>when false</li><ul>");
			sb.append(getUlHelper(whenFalse));
			sb.append("</ul></ul>");
		}
		if (always != null) {
			sb.append(getUlHelper(always));
		}
		return sb.toString();
	}
	
//	def List<AgendaItemBo> alwaysList
//	def List<AgendaItemBo> whenTrueList
//	def List<AgendaItemBo> whenFalseList
	
	public List<AgendaItemBo> getAlwaysList() {
		List<AgendaItemBo> results = new ArrayList<AgendaItemBo>();
		
		AgendaItemBo currentNode = this;
		while (currentNode.always != null) {
			results.add(currentNode.always);
			currentNode = currentNode.always;
		}
		
		return results;
	}
	
//	public List<AgendaItemBo> getWhenTrueList() {
//		List<AgendaItemBo> results = new ArrayList<AgendaItemBo>();
//		
//		AgendaItemBo currentNode = this;
//		if (currentNode.whenTrue != null) {
//			results.add(currentNode.whenTrue);
//			currentNode = currentNode.whenTrue;
//		}
//		while (currentNode.always != null) {
//			results.add(currentNode.always);
//			currentNode = currentNode.always;
//		}
//		
//		return results;
//	}
//	
//	public List<AgendaItemBo> getWhenFalseList() {
//		List<AgendaItemBo> results = new ArrayList<AgendaItemBo>();
//		
//		AgendaItemBo currentNode = this;
//		if (currentNode.whenFalse != null) {
//			results.add(currentNode.whenFalse);
//			currentNode = currentNode.whenFalse;
//		}
//		while (currentNode.always != null) {
//			results.add(currentNode.always);
//			currentNode = currentNode.always;
//		}
//		
//		return results;
//	}
	
	// Would make life in KNS easier to map to related AgendaItemBos, and RuleBos
	
} 