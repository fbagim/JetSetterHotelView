package com.jetsetter.hoteldashboard.model;

/**
 * Created by gim on 8/9/17.
 */
public enum RoomTypeEnum {

    SINGLE_ROOM(1, "SINGLE_ROOM"), DOUBLE_ROOM(1, "DOUBLE_ROOM"),TRIPLE_ROOM(1, "TRIPLE_ROOM");

    public final int roomId;
    public final String description;

    private RoomTypeEnum(int roomId, String description) {
        this.roomId = roomId;
        this.description = description;
    }
}
