package org.kuali.rice.kew.postprocessor;

import java.rmi.RemoteException;

import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentLockingEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;

public class DefaultPostProcessorRemote implements PostProcessorRemote {

	@Override
	public boolean doRouteStatusChange(
			DocumentRouteStatusChangeDTO statusChangeEvent)
			throws RemoteException {
		return true;
	}

	@Override
	public boolean doRouteLevelChange(
			DocumentRouteLevelChangeDTO levelChangeEvent)
			throws RemoteException {
		return true;
	}

	@Override
	public boolean doDeleteRouteHeader(DeleteEventDTO event)
			throws RemoteException {
		return false;
	}

	@Override
	public boolean doActionTaken(ActionTakenEventDTO event)
			throws RemoteException {
		return true;
	}

	@Override
	public boolean beforeProcess(BeforeProcessEventDTO event) throws Exception {
		return true;
	}

	@Override
	public boolean afterProcess(AfterProcessEventDTO event) throws Exception {
		return true;
	}

	@Override
	public Long[] getDocumentIdsToLock(DocumentLockingEventDTO event)
			throws Exception {
		return null;
	}

}
