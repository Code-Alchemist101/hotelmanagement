package com.hosanna.hotelmanagement.repository;

import com.hosanna.hotelmanagement.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
