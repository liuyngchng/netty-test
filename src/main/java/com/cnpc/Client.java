package com.cnpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by richard on 3/7/18.
 */
public class Client {
    private static final Logger LOGGER = LogManager.getLogger();

    public void go() {
        try {
            Socket s = new Socket("127.0.0.1",8081);
            final String msg = "hello, i am a client";
            s.getOutputStream().write(msg.getBytes());
            LOGGER.info("snd to server msg {}", msg);
            Receiver receiver = new Receiver(s, false);
            receiver.run();
            s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Configurator.initialize("Log4j2", "./config/log4j2.xml");
        Client client = new Client();
        client.go();
    }
}