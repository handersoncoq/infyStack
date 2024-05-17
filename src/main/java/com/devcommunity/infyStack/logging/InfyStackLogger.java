package com.devcommunity.infyStack.logging;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@NoArgsConstructor
@Data
public class InfyStackLogger {
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";


    private static Logger logger = LogManager.getLogger();


    public static void info(String message) {
        logger.info(ANSI_GREEN + message + ANSI_RESET);
    }
    public static void warn(String message) {
        logger.warn(ANSI_YELLOW + message + ANSI_RESET);
    }
    public static void error(String message) {
        logger.error(ANSI_RED + message + ANSI_RESET);
    }
    public static void debug(String message) {
        if(logger.isDebugEnabled())
            logger.debug(ANSI_PURPLE + message + ANSI_RESET);
    }

}
