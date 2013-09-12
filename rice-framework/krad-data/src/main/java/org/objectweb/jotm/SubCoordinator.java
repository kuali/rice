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
package org.objectweb.jotm;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.rmi.PortableRemoteObject;
import javax.transaction.InvalidTransactionException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRolledbackException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.objectweb.howl.log.xa.XACommittingTx;

/**
 * Log associated to this transaction coordinator
 */
class SLog {

	private Vector loggedResources = new Vector();
	private Vector loggedByteResources = new Vector();

	private Vector loggedXids = new Vector();

	private Vector loggedJavaxXids = new Vector();

	int decision;
	static final int DECISION_TO_COMMIT = 1;
	static final int DECISION_TO_ROLLBACK = 2;

	public void addResource(XAResource res, Xid xid, javax.transaction.xa.Xid javaxxid) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("res= " + res);
			TraceTm.jta.debug("xid= " + xid);
			TraceTm.jta.debug("javaxxid= " + javaxxid);
		}
		loggedResources.addElement(res);
		loggedByteResources.addElement(res.toString().getBytes());
		loggedXids.addElement(xid);
		loggedJavaxXids.addElement(javaxxid);
	}

	/**
	 * @return a List of logged Resources
	 */
	public List getLoggedResources() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("logged resources=" + loggedResources);
		}
		return loggedResources;
	}

	public List getByteLoggedResources() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("logged resources=" + loggedResources);
		}
		return loggedByteResources;
	}

	public List getLoggedXids() {
		return loggedXids;
	}

	public List getLoggedJavaxXids() {
		return loggedJavaxXids;
	}

	public void flushLog(int decide) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("decide=" + decide);
		}
		decision = decide;

		// XXX serialize log on disk
	}

	public void forgetLog() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("forget log");
		}
		// XXX remove file on disk
	}
}

class JotmTransactionRolledbackException extends TransactionRolledbackException {
	private static final long serialVersionUID = 1L;

	public JotmTransactionRolledbackException() {
		super();
	}

	public JotmTransactionRolledbackException(String message) {
		super(message);
	}

	public JotmTransactionRolledbackException(String message, Throwable cause) {
		super(message);
		detail = cause;
	}

	public JotmTransactionRolledbackException(Throwable cause) {
		super();
		detail = cause;
	}
}

/**
 * This object is the local coordinator. It may be registered as sub-coordinator in case of distributed transaction, so
 * it must be callable remotely and implement Resource
 */
public class SubCoordinator extends PortableRemoteObject implements Resource {

	// ------------------------------------------------------------------
	// Object state
	// ------------------------------------------------------------------

	/**
	 * @serial
	 */
	private TransactionImpl tx = null;

	/**
	 * List of Synchronization objects TODO should synchronize this list.
	 * 
	 * @serial
	 */
	private Vector synchroList = new Vector();

	/**
	 * List of XAResource objects
	 * 
	 * @serial
	 */
	private Vector resourceList = new Vector();
	private Vector javaxxidList = new Vector();

	/**
	 * Keep a reference on TransactionManager
	 * 
	 * @serial
	 */
	private TransactionManager tm;

	/**
	 * @serial
	 */
	private Xid xid = null;

	/**
	 * @serial
	 */
	private SLog log = null;

	/**
	 * javax.transaction.Status
	 * <dl>
	 * <dt>ACTIVE</dt>
	 * <dd>transaction started, commit phase not started</dd>
	 * <dt>PREPARING</dt>
	 * <dd>prepare is being sent to resources</dd>
	 * <dt>PREPARED</dt>
	 * <dd>prepare is done. Must commit now</dd>
	 * <dt>COMMITTING</dt>
	 * <dd>commit is being sent to resources</dd>
	 * <dt>COMMITTED</dt>
	 * <dd>commit was successful</dd>
	 * <dt>ROLLING_BACK</dt>
	 * <dd>not used</dd>
	 * <dt>MARKED_ROLLBACK</dt>
	 * <dd>setRollbackOnly has been called</dd>
	 * <dt>ROLLEDBACK</dt>
	 * <dd>transaction has been rolled back (or prepare returned "vote_rollback")</dd>
	 * <dt>UNKNOWN</dt>
	 * <dd>commit raised heuristics</dd>
	 * <dt>NO_TRANSACTION</dt>
	 * <dd>cannot get the status value from JTM</dd>
	 * </dl>
	 * 
	 * @serial
	 */
	private int status = Status.STATUS_ACTIVE;

	private Exception rollbackCause;
	/**
	 * @serial
	 */
	private boolean beforeCompletionDone = false;

	static final String JOTM_RD_ONLY = "JOTM_RD_ONLY";

	static final byte[] RT1 = "RR1".getBytes();
	static final byte[] RT2 = "RR2".getBytes();
	static final byte[] JOTMDONE = "RR3JOTMDONE".getBytes();

	// ------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------

	/**
	 * constructor used by TransactionImpl
	 */
	SubCoordinator(TransactionImpl tx, Xid xid) throws RemoteException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("tx=" + tx + ",  xid=" + xid);
		}

		this.tx = tx;
		this.xid = xid;
		this.tm = Current.getTransactionManager();

		beforeCompletionDone = false;
	}

	// ------------------------------------------------------------------
	// Resource interface
	// ------------------------------------------------------------------

	/**
	 * phase 1 of the 2PC.
	 * 
	 * @return int vote commit, rollback, or readonly.
	 */
	@Override
	public int prepare() throws RemoteException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		try {
			tx.doDetach(XAResource.TMSUCCESS);
		} catch (SystemException e) {

			if (TraceTm.jta.isDebugEnabled()) {
				String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
				TraceTm.jta.debug(error);
			}
		}

		switch (status) {
		case Status.STATUS_MARKED_ROLLBACK:
			// Doing rollback now may be an issue, because the following rollback
			// will not work (this object do not exist any longer)
			// doBeforeCompletion(false);
			// doRollback();
			status = Status.STATUS_ROLLING_BACK;
			return Resource.VOTE_ROLLBACK;
		case Status.STATUS_COMMITTED:
			return Resource.VOTE_COMMIT;
		default:
			doBeforeCompletion(true);
			break;
		}

		// Recheck Status after doBeforeCompletion

		if (status == Status.STATUS_MARKED_ROLLBACK) {
			TraceTm.jotm.info("Rollback during beforeCompletion in SubCoordinator.prepare");
			// doRollback();
			status = Status.STATUS_ROLLING_BACK;
			return Resource.VOTE_ROLLBACK;
		}

		int ret = doPrepare();

		if (ret == Resource.VOTE_READONLY) {
			// Transaction completed for this Resource
			doAfterCompletion();
		}

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("vote = " + ret);
		}

		return ret;
	}

	/**
	 * rollback transaction
	 */

	@Override
	public void rollback() throws RemoteException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		try {
			tx.doDetach(XAResource.TMSUCCESS);
		} catch (SystemException e) {
			if (TraceTm.jta.isDebugEnabled()) {
				String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
				TraceTm.jta.debug(error);
			}
		}

		switch (status) {
		case Status.STATUS_ACTIVE:
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_ROLLING_BACK:
			if (TraceTm.jotm.isDebugEnabled()) {
				TraceTm.jotm.debug("transaction rolling back");
			}
			break;
		case Status.STATUS_PREPARED:
			if (TraceTm.jotm.isDebugEnabled()) {
				TraceTm.jotm.debug("should not rollback a prepared transaction");
			}
			break;
		case Status.STATUS_ROLLEDBACK:
			if (TraceTm.jotm.isDebugEnabled()) {
				TraceTm.jotm.debug("already rolledback");
			}
			return;
		default:
			TraceTm.jotm.error("rollback: bad status: " + StatusHelper.getStatusName(status));
			return;
		}
		doBeforeCompletion(false);
		doRollback();
	}

	/**
	 * phase 2 of the 2PC.
	 */
	@Override
	public void commit() throws RemoteException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		switch (status) {
		case Status.STATUS_PREPARED:
			break;
		default:
			TraceTm.jotm.error("commit: bad status: " + StatusHelper.getStatusName(status));
			return;
		}
		doCommit();
	}

	/**
	 * commit 1 phase. Called either from JTM (distributed transaction) or from Transaction.commit (local transaction).
	 */

	@Override
	public void commit_one_phase() throws RemoteException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		switch (status) {
		case Status.STATUS_ROLLEDBACK:
			try {
				tx.doDetach(XAResource.TMSUCCESS);
			} catch (SystemException e) {

				if (TraceTm.jta.isDebugEnabled()) {
					String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
					TraceTm.jta.debug(error);
				}
			}
			// KULRICE-9919 : Updated to include the rollback cause
			throw new JotmTransactionRolledbackException(rollbackCause);
			// END KULRICE-9919
		case Status.STATUS_MARKED_ROLLBACK:
			doBeforeCompletion(false);
			try {
				tx.doDetach(XAResource.TMSUCCESS);
			} catch (SystemException e) {

				if (TraceTm.jta.isDebugEnabled()) {
					String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
					TraceTm.jta.debug(error);
				}
			}
			doRollback();
			// KULRICE-9919 : Updated to include the rollback cause
			throw new JotmTransactionRolledbackException(rollbackCause);
			// END KULRICE-9919
		case Status.STATUS_COMMITTED:
			try {
				tx.doDetach(XAResource.TMSUCCESS);
			} catch (SystemException e) {

				if (TraceTm.jta.isDebugEnabled()) {
					String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
					TraceTm.jta.debug(error);
				}
			}
			return;
		default:
			doBeforeCompletion(true);
			try {
				tx.doDetach(XAResource.TMSUCCESS);
			} catch (SystemException e) {

				if (TraceTm.jta.isDebugEnabled()) {
					String error = "Error when detaching XAResource:" + e + "--" + e.getMessage();
					TraceTm.jta.debug(error);
				}
			}
			break;
		}

		// status many have changed in doBeforeCompletion

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		// Recheck Status after doBeforeCompletion

		if (status == Status.STATUS_MARKED_ROLLBACK) {
			TraceTm.jotm.info("Rollback during beforeCompletion in SubCoordinator.commit_one_phase");
			doRollback();
			// KULRICE-9919 : Updated to include the rollback cause
			throw new JotmTransactionRolledbackException(rollbackCause);
			// END KULRICE-9919
		}

		// only 1 Resource => 1 phase commit

		if (resourceList.size() == 1) {
			doOnePhaseCommit();
			return;
		}

		// 2 phase commit

		int vote = doPrepare();

		switch (vote) {
		case Resource.VOTE_COMMIT:
			doCommit();
			break;
		case Resource.VOTE_READONLY:
			doAfterCompletion();
			break;
		case Resource.VOTE_ROLLBACK:
			doRollback();
			// KULRICE-9919 : Updated to include the rollback cause
			throw new JotmTransactionRolledbackException(rollbackCause);
			// END KULRICE-9919
		}
	}

	/**
	 * forget heuristics about this transaction.
	 */
	@Override
	public void forget() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.forget()");
		}
		doForget();
	}

	// ------------------------------------------------------------------
	// Other public methods (interface exposed to Transaction object)
	// ------------------------------------------------------------------

	/**
	 * add a Synchronization to the list
	 * 
	 * @param synchro
	 *            The javax.transaction.Synchronization object for the transaction associated with the target object
	 * 
	 * @exception RollbackException
	 *                Thrown to indicate that the transaction has been marked for rollback only.
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 * 
	 */
	public void addSynchronization(Synchronization synchro) throws RollbackException, IllegalStateException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("synchro=" + synchro);
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		// check status: should be ACTIVE.
		boolean markedRollback = false;

		switch (status) {
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_ROLLEDBACK:
			markedRollback = true;
			break;
		case Status.STATUS_ACTIVE:
			break;
		default:
			String errorMsg = "addSynchronization: bad status = " + StatusHelper.getStatusName(status);
			TraceTm.jotm.error(errorMsg);
			throw new IllegalStateException(errorMsg);
		}

		// Add synchro to the list of local synchros
		synchroList.addElement(synchro);

		// If transaction marked rollback only, we add synchro but we throw
		// the correct exception because nobody can presume what will be done in the
		// Synchronization object, so we must send it beforeCompletion and afterCompletion
		// even in case of rollback.

		if (markedRollback) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("SubCoordinator.addSynchronization: transaction rollback only");
			}
			throw new RollbackException();
		}
	}

	/**
	 * add a XAResource to the list
	 * 
	 * @param xares
	 *            XAResource to register
	 * 
	 * @return true if this datasource was already known
	 * 
	 * @exception IllegalStateException
	 *                Thrown if the transaction in the target object is in prepared state or the transaction is
	 *                inactive.
	 */
	public synchronized boolean addResource(XAResource xares) throws IllegalStateException {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("xares=" + xares);
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		// check status: should be ACTIVE.
		boolean markedRollback = false;

		switch (status) {
		case Status.STATUS_MARKED_ROLLBACK:
			markedRollback = true;
			break;
		case Status.STATUS_ACTIVE:
			break;
		default:
			String errorMsg = "SubCoordinator.addResource: bad status= " + StatusHelper.getStatusName(status);
			TraceTm.jotm.error(errorMsg);
			throw new IllegalStateException(errorMsg);
		}

		// Check if this DataSource is already known
		// -> we must register only ONE resource per DataSource.

		boolean found = false;

		for (int i = 0; i < resourceList.size(); i++) {
			XAResource res = (XAResource) resourceList.elementAt(i);

			try {
				if (res.isSameRM(xares)) {
					found = true;
					break;
				}
			} catch (XAException e) {
				String error = "Cannot send res.isSameRM:" + e + " (error code = " + e.errorCode + ") --"
						+ e.getMessage();
				TraceTm.jotm.error("Exception on resource.isSameRM: " + error);
			}
		}

		// add this XAResource to the list

		if (found == false) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("new XAResource added to the list");
			}
			resourceList.addElement(xares);
		}

		// If transaction marked rollback only, we enlist resource but we throw
		// the correct exception. It is important to enlist the Resource because
		// if we don't, the database would be updated although transaction has been
		// rolled back.

		if (markedRollback) {
			TraceTm.jta.debug("SubCoordinator.addResource: transaction set rollback only");
		}

		return found;
	}

	/**
	 * add this javaxxid to the List
	 * 
	 * @param javaxxid
	 */
	public synchronized void addJavaxXid(javax.transaction.xa.Xid javaxxid) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("addJavaxXid javaxxid=" + javaxxid);
		}

		// add this javaxxid to the List at the index location of XAResource
		javaxxidList.addElement(javaxxid);
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("new JavaxXid added to the list");
		}
	}

	/**
	 * Get the javaxxid at specified index in the list
	 * 
	 * @param xaresindex
	 * @return javaxxid
	 */
	public javax.transaction.xa.Xid getJavaxXid(int xaresindex) {
		javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(xaresindex);
		return myjavaxxid;
	}

	/**
	 * return the status of this transaction
	 */
	public int getStatus() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}
		return status;
	}

	/**
	 * set the transaction "rollback only"
	 */
	public void setRollbackOnly() {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("status=" + StatusHelper.getStatusName(status));
		}

		switch (status) {
		case Status.STATUS_ACTIVE:
		case Status.STATUS_UNKNOWN:
		case Status.STATUS_PREPARING:
			status = Status.STATUS_MARKED_ROLLBACK;
			break;
		case Status.STATUS_MARKED_ROLLBACK:
		case Status.STATUS_ROLLING_BACK:
			break;
		case Status.STATUS_PREPARED:
		case Status.STATUS_COMMITTED:
		case Status.STATUS_ROLLEDBACK:
		case Status.STATUS_NO_TRANSACTION:
		case Status.STATUS_COMMITTING:
			TraceTm.jotm.error("Cannot set transaction as rollback only");
			TraceTm.jotm.error("Bad status=" + StatusHelper.getStatusName(status));
			break;
		}
	}

	// ------------------------------------------------------------------
	// private methods
	// ------------------------------------------------------------------

	/**
	 * forget Transaction
	 */
	private void doForget() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doForget()");
		}

		boolean exception = false;

		for (int i = 0; i < resourceList.size(); i++) {
			// Tell this Resource to forget the transaction
			// Remove from the list ?
			XAResource xar = (XAResource) resourceList.elementAt(i);

			javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(i);

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
				TraceTm.jta.debug("forgotten with resource= " + xar);
			}

			try {
				xar.forget(myjavaxxid);
			} catch (XAException e) {
				String error = "Cannot send xar.forget:" + e + " (error code = " + e.errorCode + ") --"
						+ e.getMessage();
				TraceTm.jotm.error("Got XAException from xar.forget: " + error);
				exception = true;
			}
		}

		if (exception) {
			throw new RemoteException("XAException on forget");
		}

		unexportObject(this);
	}

	/**
	 * 2PC phase 1 (prepare) Basically, send prepare on each XAResource and collect the results.
	 */
	private synchronized int doPrepare() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doPrepare()");
		}

		int ret = VOTE_READONLY;
		int errors = 0;

		// No resource

		if (resourceList.size() == 0) {
			// increment counter for management
			Current.getCurrent().incrementCommitCounter();
			status = Status.STATUS_COMMITTED;
			return ret;
		}

		// Creates a log for that transaction, where we will add all the
		// resources that replied VOTE_COMMIT to prepare.
		// Do not flush the log on disk before decision to commit.

		log = new SLog();

		// Sends prepare to each resource while no error
		// In case of prepare on sub-coord. we may have only 1 resource.

		status = Status.STATUS_PREPARING;

		for (int i = 0; i < resourceList.size(); i++) {
			XAResource res = (XAResource) resourceList.elementAt(i);
			javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(i);

			Xid xid = new XidImpl(this.xid, i);

			if (errors > 0) {

				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("xid=        " + xid);
					TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
					TraceTm.jta.debug("rolled back with resource= " + res);
				}

				try {
					res.rollback(myjavaxxid);
					// res.rollback(xid);
				} catch (XAException e) {
					String error = "Cannot send res.rollback:" + e + " (error code = " + e.errorCode + ") --"
							+ e.getMessage();
					TraceTm.jotm.error("Got XAException from res.rollback: " + error);
					// Nothing to do ?
				}
			} else {
				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("xid=        " + xid);
					TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
					TraceTm.jta.debug("prepared with resource= " + res);
				}

				try {
					switch (res.prepare(myjavaxxid)) {
					// switch (res.prepare(xid)) {
					case XAResource.XA_OK:
						log.addResource(res, xid, myjavaxxid);
						ret = VOTE_COMMIT;
						if (TraceTm.jta.isDebugEnabled()) {
							TraceTm.jta.debug("Prepare= XA_OK");
						}
						break;
					case XAResource.XA_RDONLY:
						if (TraceTm.jta.isDebugEnabled()) {
							TraceTm.jta.debug("Prepare= XA_RDONLY");
						}
						break;
					}
				} catch (XAException e) {
					String error = "Cannot send res.prepare:" + e + " (error code = " + e.errorCode + ") --"
							+ e.getMessage();
					TraceTm.jotm.error("Got XAException from res.prepare: " + error);
					ret = VOTE_ROLLBACK;
					errors++;
				}
			}
		}

		// Update the status, depending on vote result
		// If all resources returned READ_ONLY, we can forget the transaction

		switch (ret) {
		case VOTE_READONLY:
			// increment counter for management
			Current.getCurrent().incrementCommitCounter();
			status = Status.STATUS_COMMITTED;
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("VOTE_READONLY");
			}
			break;
		case VOTE_COMMIT:
			status = Status.STATUS_PREPARED;
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("VOTE_COMMIT");
			}
			break;
		case VOTE_ROLLBACK:
			status = Status.STATUS_ROLLING_BACK;
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("VOTE_ROLLBACK");
			}
			break;
		}

		// return the global vote
		return ret;
	}

	/**
	 * 2PC - phase 2 (commit) See JTM for heuristics management
	 */
	private synchronized int doCommit() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doCommit()");
		}

		// We build the Recovery Record in doCommit just in case of a system crash
		// Store the Recovery Record using HOWL so it can manage for us.
		//
		// The Recovery Record consists of two record types:
		// 1. XA Transaction recovery record
		// 2. XA Resource recovery record
		//
		// The XA Transaction recovery record format:
		// recovery record type1 (byte[3]) - 'RR1'
		// recovery record stored date-time (long) - 8 bytes
		// format id (int) - 4 bytes
		// length of transaction's gtrid (int) - 4 bytes
		// global transaction id (byte []) - txxidgti.length bytes
		// length of transaction's bqual (int) - 4 bytes
		// bqual (byte []) - txxidbq.length bytes
		// length of transactions store date-time (int) - 4 bytes
		// transactions created date-time (byte []) - Date.length bytes
		// count of XA resources assigned to the transaction (int) - 4 bytes
		//
		// The XA Resource recovery record format:
		// recovery record type2 (byte[3]) = 'RR2'
		// resource index (int)
		// length of XA resource (int) - 4 bytes
		// XA resource (byte []) - xares.length bytes
		// length of XA resource class name (int) - 4 bytes
		// XA resource class name (byte []) - resclassname.length bytes
		// XID.formatid (int) - 4 bytes
		// length of XID.gtrid assigned to XA resource (int) - 4 bytes
		// XID.gtrid assigned to XA resource (byte []) - xidgti.length bytes
		// length of XID.bqual assigned to XA resource (int) - 4 bytes
		// XID.bqual assigned to XA resource (byte []) - xidbq.length bytes
		// XID status-state (int) = 4 bytes
		//
		// The JOTM Done recovery record format:
		// recovery record type3 (byte[3]) = 'RR3'
		// JOTM done value (byte[8]) = 'JOTMDONE'

		// First check that a log is initialized

		if (log == null) {
			TraceTm.jotm.error("doCommit: no log");
			return -1;
		}

		boolean successfulcommit = true;
		int commitnb = 0;
		int heuristicnb = 0;

		List loggedResources = log.getLoggedResources();
		List byteloggedResources = log.getByteLoggedResources();
		List loggedXids = log.getLoggedXids();
		List loggedJavaxXids = log.getLoggedJavaxXids();

		XACommittingTx xaCommitTx = null;
		XACommittingTx xaCommitTxRewrite = null;

		byte[][] recoveryBuffer = new byte[loggedResources.size() + 1][]; // loggedResources + 1 (recoveryRecord1)

		byte[] recoveryRecord1 = null;
		byte[] recoveryRecord2 = null;
		ByteBuffer rr1 = null;
		ByteBuffer rr2 = null;

		byte[][] jotmDoneRecord = new byte[1][11];

		if (Current.getDefaultRecovery()) {
			Xid txxid = tx.getXid();

			int txxidfi = txxid.getFormatId();
			byte[] txxidgti = txxid.getGlobalTransactionId();
			byte[] txxidbq = txxid.getBranchQualifier();
			int txxidlength = 4 + txxidgti.length + txxidbq.length;

			// formatid(int) + gridlen(int) + grid + bqlen(int) + bqual
			int gridlen = txxidgti.length;
			int bqlen = txxidbq.length;

			String txdate = tx.getTxDate().toString();
			int txdatelength = txdate.length();
			long rcdate = System.currentTimeMillis();

			recoveryRecord1 = new byte[3 + 8 + 4 + 4 + gridlen + 4 + bqlen + 4 + txdatelength + 4];

			rr1 = ByteBuffer.wrap(recoveryRecord1);
			rr1.put(RT1);
			rr1.putLong(rcdate);
			rr1.putInt(txxidfi);
			rr1.putInt(txxidgti.length);
			rr1.put(txxidgti);
			rr1.putInt(txxidbq.length);
			rr1.put(txxidbq);
			rr1.putInt(txdatelength);
			rr1.put(txdate.getBytes());
			rr1.putInt(loggedResources.size());

			recoveryBuffer[0] = rr1.array();

			for (int i = 0; i < loggedResources.size(); i++) {
				XAResource res = (XAResource) loggedResources.get(i);
				byte[] resname = (byte[]) byteloggedResources.get(i);

				Xid xid = (Xid) loggedXids.get(i);
				int rmindex = 99; // Store 99, at recovery we will store the correct index

				if (TraceTm.recovery.isDebugEnabled()) {
					TraceTm.recovery.debug("recovery xid= " + xid);
					TraceTm.recovery.debug("recovery resource= " + res);
				}

				int resnamelength = resname.length;
				String resclassname = res.getClass().getName();
				int resclassnamelength = resclassname.length();

				int xidfi = xid.getFormatId();
				byte[] xidgti = xid.getGlobalTransactionId();
				byte[] xidbq = xid.getBranchQualifier();
				int rr2gtilen = xidgti.length;
				int rr2bqlen = xidbq.length;

				// formatid + : + gridlen + : + bqlen + : + xidgti + : + xidbq

				recoveryRecord2 = new byte[3 + 4 + 4 + resnamelength + 4 + resclassnamelength + 4 + 4 + rr2gtilen + 4
						+ rr2bqlen + 4];

				rr2 = ByteBuffer.wrap(recoveryRecord2);
				rr2.put(RT2);
				rr2.putInt(rmindex);
				rr2.putInt(resnamelength);
				rr2.put(resname);
				rr2.putInt(resclassnamelength);
				rr2.put(resclassname.getBytes());
				rr2.putInt(xidfi);
				rr2.putInt(xidgti.length);
				rr2.put(xidgti);
				rr2.putInt(xidbq.length);
				rr2.put(xidbq);
				rr2.putInt(status);

				if (TraceTm.recovery.isDebugEnabled()) {
					TraceTm.recovery.debug("Prepare Init RR2 to Recovery Buffer");
				}
				recoveryBuffer[i + 1] = rr2.array(); // First record (0) is always rr1
			}

			try {
				xaCommitTx = TransactionRecoveryImpl.getTransactionRecovery().howlCommitLog(recoveryBuffer);
			} catch (Exception e) {
				// If we cannot write the Log, we cannot perform recovery, rollback transaction
				status = Status.STATUS_ROLLEDBACK;

				String howlerror = "Cannot howlCommitLog:" + e + " --" + e.getMessage();
				TraceTm.jotm.error("Got LogException from howlCommitLog: " + howlerror);
				xaCommitTx = null;
				doAfterCompletion();
				log.forgetLog();

				// KULRICE-9919 : Updated to include the rollback cause
				throw new JotmTransactionRolledbackException(e);
				// END KULRICE-9919
			}
		}

		// Status Transaction = committing

		status = Status.STATUS_COMMITTING;
		if (TraceTm.recovery.isDebugEnabled()) {
			TraceTm.recovery.debug("Status Committing");
		}

		// Send commit to each resource prepared

		for (int i = 0; i < loggedResources.size(); i++) {
			XAResource res = (XAResource) loggedResources.get(i);

			// javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(i);
			javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) loggedJavaxXids.get(i);
			Xid xid = (Xid) loggedXids.get(i);

			// Commit every resource that have been logged even if any of
			// the commit resources fail. During recovery, the administrator
			// will resolve any incomplete transactions.

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("xid=        " + xid);
				TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
				TraceTm.jta.debug("attempting commit with resource= " + res);
			}

			if (Current.getDefaultRecovery()) {
				int rmindex = 99; // Store 99, at recovery we will store the correct index
				byte[] resname = (byte[]) byteloggedResources.get(i);
				int resnamelength = resname.length;

				String resclassname = res.getClass().getName();
				int resclassnamelength = resclassname.length();
				int xidfi = xid.getFormatId();
				byte[] xidgti = xid.getGlobalTransactionId();
				byte[] xidbq = xid.getBranchQualifier();
				int rr2gtilen = xidgti.length;
				int rr2bqlen = xidbq.length;

				// formatid + : + gridlen + : + bqlen + : + xidgti + : + xidbq

				recoveryRecord2 = new byte[3 + 4 + 4 + resnamelength + 4 + resclassnamelength + 4 + 4 + rr2gtilen + 4
						+ rr2bqlen + 4];

				rr2 = ByteBuffer.wrap(recoveryRecord2);

				rr2.put(RT2);
				rr2.putInt(rmindex);
				rr2.putInt(resnamelength);
				rr2.put(resname);
				rr2.putInt(resclassnamelength);
				rr2.put(resclassname.getBytes());
				rr2.putInt(xidfi);
				rr2.putInt(xidgti.length);
				rr2.put(xidgti);
				rr2.putInt(xidbq.length);
				rr2.put(xidbq);
			}

			// commit resource

			try {
				res.commit(myjavaxxid, false);
				// res.commit(xid, false);

				if (Current.getDefaultRecovery()) {
					rr2.putInt(Status.STATUS_COMMITTED);
					if (TraceTm.recovery.isDebugEnabled()) {
						TraceTm.recovery.debug("Status Committed");
					}
				}

				commitnb++; // an XAresource was committed
			} catch (XAException e) {
				switch (e.errorCode) {
				case XAException.XA_HEURHAZ:
				case XAException.XA_HEURCOM:
				case XAException.XA_HEURRB:
				case XAException.XA_HEURMIX:
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("Heuristic condition= " + e.getMessage());
					}

					if (Current.getDefaultRecovery()) {
						rr2.putInt(Status.STATUS_UNKNOWN);
						if (TraceTm.recovery.isDebugEnabled()) {
							TraceTm.recovery.debug("Status Unknown");
						}
					}
					break;
				case XAException.XAER_RMERR:
				case XAException.XAER_NOTA:
				case XAException.XAER_INVAL:
				case XAException.XAER_PROTO:
				case XAException.XAER_RMFAIL:
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("RM error= " + e.getMessage());
					}

					if (Current.getDefaultRecovery()) {
						rr2.putInt(Status.STATUS_COMMITTING);
						if (TraceTm.recovery.isDebugEnabled()) {
							TraceTm.recovery.debug("Status Committing");
						}
					}
					break;
				default:
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("Default error= " + e.getMessage());
					}

					if (Current.getDefaultRecovery()) {
						rr2.putInt(Status.STATUS_ROLLEDBACK);
						if (TraceTm.recovery.isDebugEnabled()) {
							TraceTm.recovery.debug("Status Rolledback");
						}
					}
				}

				String error = "Cannot send res.commit:" + e + " (error code = " + e.errorCode + ") --"
						+ e.getMessage();
				TraceTm.jotm.error("Got XAException from res.commit: " + error);

				successfulcommit = false;
				if (commitnb > 0) {
					heuristicnb++;
				}
			}

			if (Current.getDefaultRecovery()) {
				if (TraceTm.recovery.isDebugEnabled()) {
					TraceTm.recovery.debug("Prepare New RR2 to Recovery Buffer");
				}
				recoveryBuffer[i + 1] = rr2.array(); // First record (0) is always rr1
			}
		}

		if (successfulcommit) {
			// increment counter for management
			Current.getCurrent().incrementCommitCounter();
			// Everything's fine.
			status = Status.STATUS_COMMITTED;

			if (TraceTm.recovery.isDebugEnabled()) {
				TraceTm.recovery.debug("Status Committed");
			}

			if (Current.getDefaultRecovery()) {
				try {
					if (TraceTm.recovery.isDebugEnabled()) {
						TraceTm.recovery.debug("Done howl log, all okay");
					}

					jotmDoneRecord[0] = JOTMDONE;
					TransactionRecoveryImpl.getTransactionRecovery().howlDoneLog(jotmDoneRecord, xaCommitTx);
				} catch (Exception f) {
					String howlerror = "Cannot howlDoneLog:" + f + "--" + f.getMessage();
					TraceTm.jotm.error("Got LogException from howlDoneLog: " + howlerror);
				}
			}

			doAfterCompletion();
			log.forgetLog();

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("SubCoordinator.doCommit(): EXIT 0");
			}

			return 0;
		}

		/*****
		 * if (heuristicnb == 0) { // commits on all XAResources failed, just rollback // Transaction has been
		 * eventually rolled back status = Status.STATUS_ROLLEDBACK;
		 * 
		 * if (Current.getDefaultRecovery()) { try { jotmDoneRecord [0] = JOTMDONE;
		 * TransactionRecoveryImpl.getTransactionRecovery().howlDoneLog(jotmDoneRecord, xaCommitTx); } catch (Exception
		 * f) { String howlerror = "Cannot howlDoneLog" + f + "--" + f.getMessage();
		 * TraceTm.jotm.error("Got LogException from howlDoneLog: "+ howlerror); } }
		 * 
		 * doAfterCompletion(); log.forgetLog();
		 * 
		 * throw new TransactionRolledbackException(); }
		 *****/

		// Log heuristics if errors

		// if (heuristicnb != 0) { // some XAResource commits succeeded, others failed, heuristicmixed

		if (Current.getDefaultRecovery()) {
			try {
				if (TraceTm.recovery.isDebugEnabled()) {
					TraceTm.recovery.debug("Rewrite HowlCommitLog");
				}
				xaCommitTxRewrite = TransactionRecoveryImpl.getTransactionRecovery().howlCommitLog(recoveryBuffer);
			} catch (Exception e) {
				// If we cannot write the Log, we cannot perform recovery, rollback transaction
				status = Status.STATUS_UNKNOWN;
				if (TraceTm.recovery.isDebugEnabled()) {
					TraceTm.recovery.debug("Status Unknown");
				}

				String howlerror = "Cannot howlCommitLog:" + e + " --" + e.getMessage();
				TraceTm.jotm.error("Got LogException from howlCommitLog: " + howlerror);
				xaCommitTx = null;

				doAfterCompletion();
				log.forgetLog();

				throw new JotmTransactionRolledbackException(e);
			}

			// Transaction state is unknown, now job for administrator
			// status = Status.STATUS_UNKNOWN;

			try {
				jotmDoneRecord[0] = JOTMDONE;
				TransactionRecoveryImpl.getTransactionRecovery().howlDoneLog(jotmDoneRecord, xaCommitTx);
			} catch (Exception f) {
				String howlerror = "Cannot howlDoneLog" + f + "--" + f.getMessage();
				TraceTm.jotm.error("Got LogException from howlDoneLog: " + howlerror);
			}
		}
		// }

		status = Status.STATUS_UNKNOWN;

		if (TraceTm.recovery.isDebugEnabled()) {
			TraceTm.recovery.debug("Status Unknown");
		}

		doAfterCompletion();

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doCommit(): Exit -1");
		}

		return -1;
	}

	/**
	 * 1PC (commit one phase)
	 */
	private synchronized void doOnePhaseCommit() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doOnePhaseCommit()");
		}

		// Only 1 resource: commit with onePhase=true

		status = Status.STATUS_COMMITTING;

		XAResource res = (XAResource) resourceList.elementAt(0);
		javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(0);

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
			TraceTm.jta.debug("one phase commit with resource= " + res);
		}

		try {
			res.commit(myjavaxxid, true);

			// increment counter for management
			Current.getCurrent().incrementCommitCounter();
			status = Status.STATUS_COMMITTED;
		} catch (XAException e) {
			status = Status.STATUS_UNKNOWN;
			String error = "Cannot send res.commit:" + e + " (error code = " + e.errorCode + ") --" + e.getMessage();
			TraceTm.jotm.error("Got XAException from res.commit: " + error);

			if (e.errorCode == XAException.XA_RBROLLBACK) {
				// KULRICE-9919 : Updated to include the rollback cause
				throw new JotmTransactionRolledbackException("XAException:" + error, e);
				// END KULRICE-9919
			}
			throw new RemoteException("XAException:" + error);
		} finally {
			doAfterCompletion();
		}
	}

	/**
	 * Rollback every resource involved
	 */

	private synchronized void doRollback() throws RemoteException {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("SubCoordinator.doRollback()");
		}

		status = Status.STATUS_ROLLEDBACK;
		boolean heurroll = false;
		String heuristic = null;

		// roll back each resource
		for (int i = 0; i < resourceList.size(); i++) {

			XAResource res = (XAResource) resourceList.elementAt(i);
			javax.transaction.xa.Xid myjavaxxid = (javax.transaction.xa.Xid) javaxxidList.elementAt(i);

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("myjavaxxid= " + myjavaxxid);
				TraceTm.jta.debug("rolled back with resource= " + res);
			}

			// Rollback every resource that have been logged even if any of
			// the rollback resources fail. During recovery, the administrator
			// will resolve any incomplete transactions.

			try {
				res.rollback(myjavaxxid);
			} catch (XAException e) {
				switch (e.errorCode) {
				case XAException.XA_HEURHAZ:
				case XAException.XA_HEURCOM:
				case XAException.XA_HEURRB:
				case XAException.XA_HEURMIX:
					heuristic = "Heuristic condition= " + e.errorCode + "--" + e.getMessage();
					TraceTm.jta.warn(heuristic);
					heurroll = true;
					break;
				case XAException.XAER_RMERR:
				case XAException.XAER_NOTA:
				case XAException.XAER_INVAL:
				case XAException.XAER_PROTO:
				case XAException.XAER_RMFAIL:
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("RM error= " + e.errorCode + "--" + e.getMessage());
					}
					break;
				default:
					if (TraceTm.jta.isDebugEnabled()) {
						TraceTm.jta.debug("Default error= " + e.errorCode + "--" + e.getMessage());
					}
				}
			}
		}

		// raise Heuristic exception if XAResource returned heuristic
		// no need to throw an exception for other XAResource errors,
		// the resource (xid) has been rolled back anyway

		if (heurroll) {
			// pass the last heuristic error
			throw new HeuristicRollback(heuristic);
		}

		// increment counter for management

		Current.getCurrent().incrementRollbackCounter();

		doAfterCompletion();
	}

	/**
	 * before completion
	 * 
	 * @param boolean true if completion ok, false if rollback
	 */
	private void doBeforeCompletion(boolean committing) {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("doBeforeCompletion committing= " + committing);
		}
		if (beforeCompletionDone) {
			return;
		}

		// Unset the timer for this transaction, if any
		tx.unsetTimer();

		// For each synchro, send beforeCompletion (not if rollback)

		if (committing && synchroList.size() > 0) {
			// We must be in the correct transaction context
			// See JTA spec. page 13 (3.3.2)
			// because at beforeCompletion, the bean will write its cache

			// Check the trivial case where we already have the correct tx context

			Transaction mytx = null;
			boolean suspended = false;
			boolean resumed = false;

			try {
				mytx = tm.getTransaction();
			} catch (SystemException e) {
				if (TraceTm.jta.isDebugEnabled()) {
					String error = "Cannot get transaction:" + e + "--" + e.getMessage();
					TraceTm.jta.debug(error);
				}
			}

			// Suspend if another tx context

			if (mytx != null && mytx.equals(tx) == false) {
				try {
					tm.suspend();
					suspended = true;
				} catch (SystemException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot suspend transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}

			// Resume the good tx context

			if (mytx == null || suspended) {
				try {
					tm.resume(tx);
					resumed = true;
				} catch (SystemException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				} catch (InvalidTransactionException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				} catch (IllegalStateException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}

			// Call the synchronizations
			// beforeCompletion may set the TX rollbackonly if something goes wrong

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("sychronization list size= " + synchroList.size());
			}

			for (int i = 0; i < synchroList.size(); i++) {
				Synchronization sync = (Synchronization) synchroList.elementAt(i);

				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("Calling Synchro " + sync);
				}

				try {
					// This can register a new synchro, modifying synchroList!
					// TODO add a synchronized on list, and call beforeCompletion
					// outside the block.
					sync.beforeCompletion();
				} catch (RuntimeException e) {
					status = Status.STATUS_MARKED_ROLLBACK;
					rollbackCause = e;
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot sync.beforeCompletion:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}

			// reset context as it was before

			if (resumed) {
				try {
					tm.suspend();
				} catch (SystemException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot suspend transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}

			if (suspended) {
				try {
					tm.resume(mytx);
					resumed = true;
				} catch (SystemException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				} catch (InvalidTransactionException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				} catch (IllegalStateException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "Cannot resume transaction:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}
		}

		beforeCompletionDone = true;
	}

	/**
	 * after completion
	 */
	private void doAfterCompletion() {
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("doAfterCompletion()");
		}

		// For each synchro, send afterCompletion
		// / CompletedTransactionListener has been replaced by Synchronization

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("sychronization list size= " + synchroList.size());
		}

		for (int i = 0; i < synchroList.size(); i++) {
			Synchronization sync = (Synchronization) synchroList.elementAt(i);

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("Synchronization sync= " + sync);
				TraceTm.jta.debug("sync.afterCompletion status= " + StatusHelper.getStatusName(status));
			}

			sync.afterCompletion(status);
		}

		// Forget this transaction.
		// LATER:
		// - Should not forget it in case of heuristics (for recovery)
		// - May be this could be deferred in case of retry from a client: use a timer.
		Current.getCurrent().forgetTx(tx.getXid());
	}

	/**
	 * return index in resourceList of this XAResource
	 * 
	 * @param xares
	 *            the XAResource
	 * @return index
	 */
	public int getXaresIndex(XAResource xares) {

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("getXaresIndex xares= " + xares);
			TraceTm.jta.debug("resourceList.size= " + resourceList.size());
		}

		int xaresIndex = -1;

		// first, search for an XAResource with the same object reference
		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("search xares with same obj ref");
		}

		for (int i = 0; i < resourceList.size(); i++) {
			XAResource res = (XAResource) resourceList.elementAt(i);

			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("res= " + res);
			}

			if (res.equals(xares)) {
				xaresIndex = i;
				break;
			}
		}

		// if not found, search for a xares with the same RM
		if (xaresIndex == -1) {
			if (TraceTm.jta.isDebugEnabled()) {
				TraceTm.jta.debug("not found -> search for xares with same RM");
			}

			for (int i = 0; i < resourceList.size(); i++) {
				XAResource res = (XAResource) resourceList.elementAt(i);

				if (TraceTm.jta.isDebugEnabled()) {
					TraceTm.jta.debug("res= " + res);
				}

				try {
					if (res.isSameRM(xares)) {
						xaresIndex = i;
						break;
					}
				} catch (XAException e) {
					if (TraceTm.jta.isDebugEnabled()) {
						String error = "res.isSameRm exception:" + e + "--" + e.getMessage();
						TraceTm.jta.debug(error);
					}
				}
			}
		}

		if (TraceTm.jta.isDebugEnabled()) {
			TraceTm.jta.debug("xaresIndex= " + xaresIndex);
		}

		return xaresIndex;
	}

}
