package xperience;

import donabase.DonaBaseConnection;
import donabase.DonaBaseException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XPerience Server that uses a MySQL database via the DonaBase library to persist events.
 * It accepts multiple clients concurrently using virtual threads.
 * <p>
 * Expected command-line parameters:
 *   args[0] - server port for XPerience (e.g., 5000)
 *   args[1] - path to password file
 *   args[2] - MySQL server address (e.g., "localhost")
 *   args[3] - MySQL server port (e.g., 3306)
 *   args[4] - MySQL database name
 *   args[5] - MySQL username
 *   args[6] - MySQL password
 */
public class XPerienceServerDB {

    public static final String LOGGERNAME = "xperience.db";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);

    /**
     * A persistent event list that implements the few List methods required by ClientHandler.
     * Under the hood, it delegates operations to the MySQL database via a DonaBaseConnection.
     */
    public static class PersistentEventList extends AbstractList<Event> {
        private final DonaBaseConnection dbConn;

        public PersistentEventList(DonaBaseConnection dbConn) {
            this.dbConn = dbConn;
        }

        @Override
        public Event get(int index) {
            try {
                List<List<String>> rows = dbConn.query("SELECT name, date, time, description FROM Event");
                if (index < 0 || index >= rows.size()) {
                    throw new IndexOutOfBoundsException();
                }
                List<String> row = rows.get(index);
                return new Event(row.get(0), row.get(1), row.get(2), row.get(3));
            } catch (DonaBaseException ex) {
                throw new RuntimeException("Database error while fetching events", ex);
            }
        }

        @Override
        public int size() {
            try {
                List<List<String>> rows = dbConn.query("SELECT name, date, time, description FROM Event");
                return rows.size();
            } catch (DonaBaseException ex) {
                throw new RuntimeException("Database error while counting events", ex);
            }
        }

        @Override
        public boolean add(Event e) {
            String insertStmt = "INSERT INTO Event (name, date, time, description) VALUES ('"
                    + e.getName() + "', '"
                    + e.getDate() + "', '"
                    + e.getTime() + "', '"
                    + e.getDescription() + "')";
            try {
                return dbConn.insert(insertStmt);
            } catch (DonaBaseException ex) {
                throw new RuntimeException("Database error while inserting event", ex);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 7) {
            logger.severe("Usage: <XPeriencePort> <passwordFile> <DBserver> <DBport> <DBname> <DBusername> <DBpassword>");
            System.exit(1);
        }

        int servPort = 0;
        int dbPort = 0;
        try {
            servPort = Integer.parseInt(args[0]);
            dbPort = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            logger.severe("XPerience server port and DB port must be integers.");
            System.exit(1);
        }
        String passwordFilePath = args[1];
        String dbServer = args[2];
        String dbName = args[4];
        String dbUsername = args[5];
        String dbPassword = args[6];

        // Initialize PasswordList from the provided file.
        PasswordList passwordList = null;
        try {
            passwordList = new PasswordList(passwordFilePath);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Failed to load password file", ex);
            System.exit(1);
        }

        // Create a connection to the MySQL database using DonaBase.
        DonaBaseConnection dbConn = null;
        try {
            dbConn = new DonaBaseConnection(dbServer, dbPort, dbName, dbUsername, dbPassword);
        } catch (DonaBaseException ex) {
            logger.log(Level.SEVERE, "Failed to connect to the database", ex);
            System.exit(1);
        }

        // Create the persistent event list backed by the database.
        EventStore persistentEvents = new EventStoreDB(dbConn);
        // Start the server socket and use an executor with virtual threads for concurrent clients.
        try (ServerSocket servSock = new ServerSocket(servPort);
             ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            logger.info("XPerienceServerDB started on port " + servPort);

            while (true) {
                Socket clntSock = servSock.accept();
                PasswordList finalPasswordList = passwordList;
                executor.submit(() -> {
                    // The ClientHandler uses the provided PasswordList and persistent event list.
                    new ClientHandler(clntSock, finalPasswordList, persistentEvents).handle();
                });
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Server setup failed", ex);
        } finally {
            // Optionally close the database connection on shutdown.
            if (dbConn != null) {
                dbConn.close();
            }
        }
    }
}
