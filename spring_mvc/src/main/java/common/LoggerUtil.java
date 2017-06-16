package common;

import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2017/6/16.
 */
public class LoggerUtil {
    protected static Logger logger = Logger.getLogger(LoggerUtil.class);

    public static void info(String logData) {
        logger.info(logData);
    }
}
