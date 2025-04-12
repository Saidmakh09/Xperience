package xperience;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to manage password list
 *
 * @version 1.0
 */
public class PasswordList {
    private static final String LOGGERNAME = "xperience";
    private static final Logger logger = Logger.getLogger(LOGGERNAME);

    private final Set<String> passwords = new HashSet<>();

    /**
     * Create password list from file
     *
     * @param passwordFilePath Path to password file
     * @throws IOException if I/O problem
     */
    public PasswordList(String passwordFilePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    passwords.add(line);
                }
            }
        }
        logger.info("Loaded " + passwords.size() + " passwords from file");
    }

    /**
     * If password in list, remove password from list and return true;
     * otherwise (not in list), return false
     *
     * @param password password to use
     * @return true if password in list; false otherwise
     */
    public boolean use(String password) {
        if (passwords.contains(password)) {
            passwords.remove(password);
            logger.fine("Password used successfully: " + password);
            return true;
        }
        logger.warning("Invalid password attempt: " + password);
        return false;
    }
}
