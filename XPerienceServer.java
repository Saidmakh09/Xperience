package xperience;

import java.io.IOException;import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single threaded server implementing the XPerience protocol
 * @version 1.0
 */
public class XPerienceServer {

    public static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);



    private static EventStore eventStore = new EventStoreMemory();
    private static PasswordList passwordList;

    public static void checkParameters(String[] ports){
        if (ports.length != 2) {
            logger.severe("Incorrect parameter(s). Expected: <Port>");
            System.exit(1);

        }
    }


    public static void checkIntPort(String[] port ){
        try {
            Integer.parseInt(port[0]);

        } catch (NumberFormatException e) {
            logger.severe("Incorrect input(s). Expected: Int");
            System.exit(1);
        }

        }


    public static void main(String[] args) {
        checkParameters(args);
        checkIntPort(args);

        int servPort = Integer.parseInt(args[0]);
        String passwordFilePath = args[1];

        try {
            passwordList = new PasswordList(passwordFilePath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load password file", ex);
            System.exit(1);
        }

        ServerSocket servSock = null;
        try {
            servSock = new ServerSocket(servPort);
            logger.info("XPerience server started on port " + servPort);

            while (true) {
                Socket clntSock = servSock.accept();
                new ClientHandler(clntSock, passwordList, eventStore).handle();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server setup failed", ex);
        } finally {
            if (servSock != null) {
                try {
                    servSock.close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "Error closing server socket", ex);
                }
            }
        }
    }
}