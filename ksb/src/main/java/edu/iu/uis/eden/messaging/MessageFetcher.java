package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.springframework.dao.OptimisticLockingFailureException;

import edu.iu.uis.eden.messaging.threadpool.KSBThreadPool;


/**
 * Fetches messages from the db.  Marks as 'R'.  Gives messages to ThreadPool for execution
 * 
 * @author rkirkend
 *
 */
public class MessageFetcher implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(MessageFetcher.class);
	private static List<Class<?>> OPTIMISTIC_LOCK_CLASSES = new ArrayList<Class<?>>();
	static {
		OPTIMISTIC_LOCK_CLASSES.add(OptimisticLockException.class);
		OPTIMISTIC_LOCK_CLASSES.add(OptimisticLockingFailureException.class);
	}

	public void run() {
		KSBThreadPool threadPool = KSBServiceLocator.getThreadPool();
		long taskCount = threadPool.getActiveCount();
		LOG.debug("Queue active task count = " + taskCount);
		if (taskCount < 50) {
			LOG.debug("Checking for messages");
			
			for (final PersistedMessage p : getMessages(50)) {
				threadPool.execute(new MessageServiceInvoker(p));
			}
		}
	}
	
	
    private List<PersistedMessage> getMessages(int maxMessages) {
        List<PersistedMessage> validRouteQueues = new ArrayList<PersistedMessage>();
        List routeQueues = getRouteQueueService().getNextDocuments(maxMessages);
        if (routeQueues == null)
            routeQueues = new ArrayList();
        for (Iterator iterator = routeQueues.iterator(); iterator.hasNext();) {
            PersistedMessage routeQueue = (PersistedMessage) iterator.next();
            if (routeQueue == null) {
                LOG.warn("We somehow received a null RouteQueue when initiating routing.");
                continue;
            }
            try {
                routeQueue.setQueueStatus(RiceConstants.ROUTE_QUEUE_ROUTING);
                getRouteQueueService().save(routeQueue);
                validRouteQueues.add(routeQueue);
            } catch (RuntimeException e) {
                if (checkForOptimisticLockFailure(e)) {
                    // this means that another route manager has already grabbed
                    // this entry, ignore this entry
                    LOG.info("An optimistic locking exception was thrown when trying to save route queue with id=" + routeQueue.getRouteQueueId());
                } else
                    throw e;
            }
        }
        return validRouteQueues;
    }
    
    private static boolean checkForOptimisticLockFailure(Exception exception) {
        for (final Class<?> clazz : OPTIMISTIC_LOCK_CLASSES) {
            if (clazz.isInstance(exception) ||
                    clazz.isInstance(exception.getCause())) return true;
        }
        return false;
    }
    
    private MessageQueueService getRouteQueueService() {
        return KSBServiceLocator.getRouteQueueService();
    }
}
