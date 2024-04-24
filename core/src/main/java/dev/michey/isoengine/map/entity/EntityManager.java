package dev.michey.isoengine.map.entity;

public class EntityManager {

    private long currentEntityId;

    public long generateEntityId() {
        long current = currentEntityId;
        currentEntityId++;
        return current;
    }

}
