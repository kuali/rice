package edu.sampleu.travel.infrastructure;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import edu.iu.uis.eden.core.BaseLifecycle;

/**
 * Core lifecycle for Travel application.  Initializes Spring and OJB.
 * @author rkirkend
 * @author natjohns
 */
public class TravelLifecycle extends BaseLifecycle {
    private static final Logger LOG = Logger.getLogger(TravelLifecycle.class);
    
    public static final String DATASOURCE = "travelDataSource";

    private ApplicationContext context;
    
    public TravelLifecycle(ApplicationContext context) {
        this.context = context;
    }

    public void start() throws Exception {
        TravelServiceLocator.getInstance().start();
        super.start();
    }
        
    public void stop() throws Exception {
        LOG.info("Stopping TravelLifecycle");
        TravelServiceLocator.getInstance().stop();
        super.stop();
    }
}