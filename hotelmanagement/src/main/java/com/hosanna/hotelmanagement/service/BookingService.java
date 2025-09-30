package com.hosanna.hotelmanagement.service;

import com.hosanna.hotelmanagement.dto.BookingRequest;
import com.hosanna.hotelmanagement.model.Booking;
import com.hosanna.hotelmanagement.model.Room;
import com.hosanna.hotelmanagement.model.User;
import com.hosanna.hotelmanagement.repository.BookingRepository;
import com.hosanna.hotelmanagement.repository.RoomRepository;
import com.hosanna.hotelmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Create booking - FIXED to properly fetch user and room
    public Booking createBooking(BookingRequest request) throws Exception {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Exception("User not found with id: " + request.getUserId()));

        // Validate room exists
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new Exception("Room not found with id: " + request.getRoomId()));

        // Check if room is available
        if (!room.getAvailable()) {
            throw new Exception("Room is not available");
        }

        // Validate dates
        if (request.getCheckInDate().isAfter(request.getCheckOutDate())) {
            throw new Exception("Check-in date must be before check-out date");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "BOOKED");

        // Optionally mark room as unavailable
        // room.setAvailable(false);
        // roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Get booking by ID
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Update booking - FIXED
    public Booking updateBooking(Long id, BookingRequest request) throws Exception {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new Exception("Booking not found with id " + id));

        // Update user if provided
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new Exception("User not found with id: " + request.getUserId()));
            booking.setUser(user);
        }

        // Update room if provided
        if (request.getRoomId() != null) {
            Room room = roomRepository.findById(request.getRoomId())
                    .orElseThrow(() -> new Exception("Room not found with id: " + request.getRoomId()));
            booking.setRoom(room);
        }

        // Update dates if provided
        if (request.getCheckInDate() != null) {
            booking.setCheckInDate(request.getCheckInDate());
        }
        if (request.getCheckOutDate() != null) {
            booking.setCheckOutDate(request.getCheckOutDate());
        }

        // Validate dates
        if (booking.getCheckInDate().isAfter(booking.getCheckOutDate())) {
            throw new Exception("Check-in date must be before check-out date");
        }

        // Update status if provided
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }

        return bookingRepository.save(booking);
    }

    // Delete booking
    public void deleteBooking(Long id) throws Exception {
        if (!bookingRepository.existsById(id)) {
            throw new Exception("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    // Get bookings by user ID
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    // Get bookings by room ID
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }
}