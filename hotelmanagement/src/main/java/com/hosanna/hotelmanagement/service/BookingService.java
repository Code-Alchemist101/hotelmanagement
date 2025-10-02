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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    /**
     * Check if a room is available for the given date range
     * Returns true if room is available, false if there's a conflict
     */
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeBookingId) {
        List<Booking> existingBookings = bookingRepository.findByRoomId(roomId);

        for (Booking booking : existingBookings) {
            // Skip the current booking when updating
            if (excludeBookingId != null && booking.getId().equals(excludeBookingId)) {
                continue;
            }

            // Skip cancelled bookings
            if ("CANCELLED".equals(booking.getStatus())) {
                continue;
            }

            // Check for date overlap
            LocalDate existingCheckIn = booking.getCheckInDate();
            LocalDate existingCheckOut = booking.getCheckOutDate();

            // Overlap occurs if:
            // 1. New check-in is before existing check-out AND
            // 2. New check-out is after existing check-in
            boolean hasOverlap = checkIn.isBefore(existingCheckOut) && checkOut.isAfter(existingCheckIn);

            if (hasOverlap) {
                return false; // Conflict detected
            }
        }

        return true; // No conflicts
    }

    /**
     * Create booking with conflict detection
     */
    @Transactional
    public Booking createBooking(BookingRequest request) throws Exception {
        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Exception("User not found with id: " + request.getUserId()));

        // Validate room exists
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new Exception("Room not found with id: " + request.getRoomId()));

        // Validate dates
        if (request.getCheckInDate().isAfter(request.getCheckOutDate()) ||
                request.getCheckInDate().isEqual(request.getCheckOutDate())) {
            throw new Exception("Check-in date must be before check-out date");
        }

        // Validate check-in is not in the past
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new Exception("Check-in date cannot be in the past");
        }

        // Check for booking conflicts
        if (!isRoomAvailable(request.getRoomId(), request.getCheckInDate(), request.getCheckOutDate(), null)) {
            throw new Exception("Room is not available for the selected dates. Please choose different dates.");
        }

        // Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "BOOKED");

        return bookingRepository.save(booking);
    }

    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Get booking by ID
     */
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    /**
     * Update booking with conflict detection
     */
    @Transactional
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
        LocalDate newCheckIn = request.getCheckInDate() != null ? request.getCheckInDate() : booking.getCheckInDate();
        LocalDate newCheckOut = request.getCheckOutDate() != null ? request.getCheckOutDate() : booking.getCheckOutDate();

        // Validate dates
        if (newCheckIn.isAfter(newCheckOut) || newCheckIn.isEqual(newCheckOut)) {
            throw new Exception("Check-in date must be before check-out date");
        }

        // Check for conflicts when updating dates or room
        Long roomIdToCheck = request.getRoomId() != null ? request.getRoomId() : booking.getRoom().getId();
        if (!isRoomAvailable(roomIdToCheck, newCheckIn, newCheckOut, id)) {
            throw new Exception("Room is not available for the selected dates. Please choose different dates.");
        }

        booking.setCheckInDate(newCheckIn);
        booking.setCheckOutDate(newCheckOut);

        // Update status if provided
        if (request.getStatus() != null) {
            booking.setStatus(request.getStatus());
        }

        return bookingRepository.save(booking);
    }

    /**
     * Delete booking
     */
    @Transactional
    public void deleteBooking(Long id) throws Exception {
        if (!bookingRepository.existsById(id)) {
            throw new Exception("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    /**
     * Get bookings by user ID
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * Get bookings by room ID
     */
    public List<Booking> getBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    /**
     * Get upcoming bookings (check-in date is today or in the future)
     */
    public List<Booking> getUpcomingBookings() {
        return bookingRepository.findAll().stream()
                .filter(b -> !b.getCheckInDate().isBefore(LocalDate.now()))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .toList();
    }

    /**
     * Get active bookings (currently checked in)
     */
    public List<Booking> getActiveBookings() {
        LocalDate today = LocalDate.now();
        return bookingRepository.findAll().stream()
                .filter(b -> !b.getCheckInDate().isAfter(today) && !b.getCheckOutDate().isBefore(today))
                .filter(b -> "BOOKED".equals(b.getStatus()))
                .toList();
    }
}