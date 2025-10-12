package com.hosanna.hotelmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enable scheduling for automatic booking completion
 * This allows the @Scheduled annotation in BookingService to work
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // No additional configuration needed
    // This class just enables Spring's scheduling capabilities
}