package xperience;

import donabase.DonaBaseConnection;
import donabase.DonaBaseException;
import java.util.List;

public class EventStoreDB implements EventStore {
    private final DonaBaseConnection dbConn;

    public EventStoreDB(DonaBaseConnection dbConn) {
        this.dbConn = dbConn;
    }

    @Override
    public boolean add(Event e) {
        String insertStmt = "INSERT INTO Event (name, date, time, description) VALUES ('"
                + e.getName() + "', '"
                + e.getDate() + "', '"
                + e.getTime() + "', '"
                + e.getDescription() + "')";
        try {
            // Assume that insert returns true if the row was added successfully.
            return dbConn.insert(insertStmt);
        } catch (DonaBaseException ex) {
            throw new RuntimeException("Database error while inserting event", ex);
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
    public boolean exists(String name) {
        try {
            List<List<String>> rows = dbConn.query("SELECT name FROM Event WHERE name = '" + name + "'");
            return !rows.isEmpty();
        } catch (DonaBaseException ex) {
            throw new RuntimeException("Database error while checking event existence", ex);
        }
    }
}
