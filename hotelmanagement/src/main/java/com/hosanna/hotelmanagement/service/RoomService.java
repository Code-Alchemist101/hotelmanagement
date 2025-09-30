package com.hosanna.hotelmanagement.service;

import com.hosanna.hotelmanagement.model.Room;
import com.hosanna.hotelmanagement.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Create new room
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    // Get all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // Get room by ID
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    // Update room - FIXED to handle partial updates (null-safe)
    public Room updateRoom(Long id, Room updatedRoom) {
        return roomRepository.findById(id).map(room -> {
            // Only update fields that are provided (not null)
            if (updatedRoom.getRoomNumber() != null) {
                room.setRoomNumber(updatedRoom.getRoomNumber());
            }
            if (updatedRoom.getType() != null) {
                room.setType(updatedRoom.getType());
            }
            if (updatedRoom.getPrice() != null) {
                room.setPrice(updatedRoom.getPrice());
            }
            if (updatedRoom.getAvailable() != null) {
                room.setAvailable(updatedRoom.getAvailable());
            }
            return roomRepository.save(room);
        }).orElseThrow(() -> new RuntimeException("Room not found with id " + id));
    }

    // Delete room
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id " + id);
        }
        roomRepository.deleteById(id);
    }
}