package echowand.util;

import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ymakino
 */
public class LoggerConfig {
    
    /*
     * This HashMap keeps Logger instances to avoid being released accidentally.
     * Because OpenJDK uses weak references for keeping Logger instances,
     * they might be freed anytime while there are no explicit references
     * to them.
     */
    private static HashMap<String, Logger> loggers = new HashMap<String, Logger>();
    
    private synchronized static Logger getLogger(String name) {
        Logger logger = loggers.get(name);
        if (logger == null) {
            logger = Logger.getLogger(name);
            loggers.put(name, logger);
        }
        return logger;
    }
    
    private static ConsoleHandler handler = null;
    
    private static ConsoleHandler getHandler() {
        if (handler == null) {
            handler = new ConsoleHandler();
            handler.setLevel(Level.ALL);
        }
        
        return handler;
    }
    
    public synchronized static void changeLogLevelAll(String name) {
        changeLogLevel(Level.ALL, name);
    }
    
    
    public synchronized static void changeLogLevel(Level level, String name) {
        getLogger(name).setLevel(level);
        getLogger(name).addHandler(getHandler());
    }
}
