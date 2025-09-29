package com.hosanna.hotelmanagement.repository;

import com.hosanna.hotelmanagement.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    // Custom queries if needed later
}
