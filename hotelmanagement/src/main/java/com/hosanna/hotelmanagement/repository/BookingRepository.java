package com.hosanna.hotelmanagement.repository;

import com.hosanna.hotelmanagement.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Find all bookings for a specific user
    List<Booking> findByUserId(Long userId);

    // Find all bookings for a specific room
    List<Booking> findByRoomId(Long roomId);
}