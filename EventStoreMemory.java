package xperience;

import java.util.ArrayList;
import java.util.List;

public class EventStoreMemory implements EventStore {
    private final List<Event> events = new ArrayList<>();

    @Override
    public synchronized boolean add(Event e) {
        // Check if event already exists
        if (exists(e.getName())) {
            return false;
        }
        return events.add(e);
    }

    @Override
    public synchronized int size() {
        return events.size();
    }

    @Override
    public synchronized boolean exists(String name) {
        return events.stream().anyMatch(ev -> ev.getName().equals(name));
    }
}
