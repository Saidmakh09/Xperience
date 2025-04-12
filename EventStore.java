package xperience;

public interface EventStore {
    /**
     * Adds an event to the store.
     * @param e the event to add
     * @return true if the event was added successfully, false otherwise.
     */
    boolean add(Event e);

    /**
     * Returns the number of events currently stored.
     */
    int size();

    /**
     * Checks whether an event with the given name already exists.
     * @param name the event name to look for.
     * @return true if an event with the given name exists.
     */
    boolean exists(String name);
}
