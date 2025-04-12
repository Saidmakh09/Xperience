package xperience;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ClientHandler {
    private static final Logger logger = Logger.getLogger(XPerienceServer.LOGGERNAME);
    private static final int EXPECTED_FIELDS = 5;

    private final Socket clntSock;
    private final PasswordList passwordList;
    private final EventStore eventStore; // Changed from List<Event> to EventStore

    // Regex patterns for date and time validation
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    public ClientHandler(Socket clntSock, PasswordList passwordList, EventStore eventStore) {
        this.clntSock = clntSock;
        this.passwordList = passwordList;
        this.eventStore = eventStore;
    }

    public void handle() {
        logger.info(() -> "Handling client at " + clntSock.getRemoteSocketAddress());
        try (Scanner in = new Scanner(clntSock.getInputStream(), StandardCharsets.US_ASCII);
             PrintWriter out = new PrintWriter(clntSock.getOutputStream(), true, StandardCharsets.US_ASCII);
             clntSock) {

            String clientInput = in.nextLine();
            logger.info("Received: " + clientInput);
            processRequest(clientInput, out);

        } catch (Exception ex) {
            logger.log(Level.WARNING, "Client communication failed", ex);
        }
    }

    private void processRequest(String inputString, PrintWriter out) {
        String[] fields = inputString.split("#", -1);
        if (fields.length < EXPECTED_FIELDS) {
            logger.warning("Received message with insufficient fields: " + fields.length);
            out.println("Reject#");
            return;
        }

        String name = fields[0];
        String date = fields[1];
        String time = fields[2];
        String description = fields[3];
        String password = fields[4];

        // Password authentication check
        if (!passwordList.use(password)) {
            logger.warning("Authentication failed: Invalid password");
            out.println("Reject#");
            return;
        }

        // Field validations
        if (!isValidName(name)) {
            logger.warning("Validation failed: Invalid event name length");
            out.println("Reject#");
            return;
        }
        if (!isValidDate(date)) {
            logger.warning("Validation failed: Date does not match YYYY-MM-DD pattern");
            out.println("Reject#");
            return;
        }
        if (!isValidTime(time)) {
            logger.warning("Validation failed: Time does not match HH:MM (24-hour) pattern");
            out.println("Reject#");
            return;
        }
        if (!isValidDescription(description)) {
            logger.warning("Validation failed: Invalid description length");
            out.println("Reject#");
            return;
        }
        if (eventStore.exists(name)) {
            logger.warning("Event name already in use: " + name);
            out.println("Reject#");
            return;
        }

        Event newEvent = new Event(name, date, time, description);
        if (!eventStore.add(newEvent)) {
            logger.warning("Failed to add event to the store: " + name);
            out.println("Reject#");
            return;
        }

        logger.info("Accepted new event: " + newEvent);
        out.println("Accept#" + eventStore.size() + "#");
    }

    // Validation helpers
    private boolean isValidName(String name) {
        return name != null && name.length() >= 1 && name.length() <= 300;
    }

    private boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date).matches();
    }

    private boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time).matches();
    }

    private boolean isValidDescription(String description) {
        return description != null && description.length() >= 1 && description.length() <= 65535;
    }
}
