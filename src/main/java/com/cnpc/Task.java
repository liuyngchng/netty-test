package com.cnpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Task implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public void run() {
        final int t = 20;
        LOGGER.info("i will sleep {}s", t);
        try {
            Thread.sleep(t * 1000);
            LOGGER.info("i am awaken");
        } catch (Exception ex) {
            LOGGER.error("err");
        }
        final String msg = "another msg";
        LOGGER.info("wrt msg, {}", msg);
        CtxUtl.wrtCtx(msg, Server.ctx);
    }
}
