package xperience;

/**
 * Represents an event in the XPerience system
 */
public class Event {
    private String name;
    private String date;
    private String time;
    private String description;

    /**
     * Creates a new Event with the given details
     *
     * @param name The name of the event
     * @param date The date of the event
     * @param time The time of the event
     * @param description The description of the event
     */
    public Event(String name, String date, String time, String description) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
    }

    /**
     * @return The name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * @return The date of the event
     */
    public String getDate() {
        return date;
    }

    /**
     * @return The time of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * @return The description of the event
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Event{name='" + name + "', date='" + date + "', time='" + time + "', description='" + description + "'}";
    }
}