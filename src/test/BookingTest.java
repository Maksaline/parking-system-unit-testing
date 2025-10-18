package test;

import src.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class BookingTest {

    private Booking booking;
    private Vehicle vehicle;
    private ParkingSlot parkingSlot;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle(1, VehicleType.CAR, 1000.0);
        parkingSlot = new ParkingSlot("A1", ParkingSlotType.REGULAR);
        startTime = LocalDateTime.of(2025, 10, 17, 10, 0);
        endTime = LocalDateTime.of(2025, 10, 17, 12, 0);
        booking = new Booking(1, vehicle, parkingSlot, startTime, endTime, 100.0);
    }

    @Nested
    class ConstructorTests {

        @Test
        void testConstructorInitializesAllFields() {
            Booking b = new Booking(5, vehicle, parkingSlot, startTime, endTime, 250.0);

            assertEquals(5, b.getBookingId(), "Booking ID should be set correctly");
            assertEquals(vehicle, b.getVehicle(), "Vehicle should be set correctly");
            assertEquals(parkingSlot, b.getParkingSlot(), "Parking slot should be set correctly");
            assertEquals(startTime, b.getStartTime(), "Start time should be set correctly");
            assertEquals(endTime, b.getEndTime(), "End time should be set correctly");
            assertEquals(250.0, b.getAmount(), "Amount should be set correctly");
            assertEquals(BookingStatus.ACTIVE, b.getBookingStatus(), "Initial status should be ACTIVE");
        }

        @Test
        void testConstructorWithZeroAmount() {
            Booking b = new Booking(1, vehicle, parkingSlot, startTime, endTime, 0.0);
            assertEquals(0.0, b.getAmount(), "Amount can be zero");
            assertEquals(BookingStatus.ACTIVE, b.getBookingStatus(), "Status should still be ACTIVE");
        }

        @Test
        void testConstructorWithNegativeAmount() {
            Booking b = new Booking(1, vehicle, parkingSlot, startTime, endTime, -50.0);
            assertEquals(0.0, b.getAmount(), "Negative amount should not be allowed");
        }

        @Test
        void testConstructorWithNegativeBookingId() {
            Booking b = new Booking(-1, vehicle, parkingSlot, startTime, endTime, 100.0);
            assertEquals(-1, b.getBookingId(), "Negative booking ID should be allowed");
        }

        @Test
        void testConstructorWithSameStartAndEndTime() {
            LocalDateTime sameTime = LocalDateTime.of(2025, 10, 17, 10, 0);
            Booking b = new Booking(1, vehicle, parkingSlot, sameTime, sameTime, 0.0);
            assertEquals(sameTime, b.getStartTime(), "Start time should be set");
            assertEquals(sameTime, b.getEndTime(), "End time should be set");
        }

        @Test
        void testConstructorWithEndTimeBeforeStartTime() {
            LocalDateTime later = LocalDateTime.of(2025, 10, 17, 12, 0);
            LocalDateTime earlier = LocalDateTime.of(2025, 10, 17, 10, 0);
            Booking b = new Booking(1, vehicle, parkingSlot, later, earlier, 100.0);
            assertEquals(later, b.getStartTime(), "Start time should be set even if after end time");
            assertEquals(earlier, b.getEndTime(), "End time should be set even if before start time");
        }

        @Test
        void testConstructorWithNullVehicle() {
            Booking b = new Booking(1, null, parkingSlot, startTime, endTime, 100.0);
            assertNull(b.getVehicle(), "Vehicle can be null");
        }

        @Test
        void testConstructorWithNullParkingSlot() {
            Booking b = new Booking(1, vehicle, null, startTime, endTime, 100.0);
            assertNull(b.getParkingSlot(), "Parking slot can be null");
        }

        @Test
        void testConstructorWithNullStartTime() {
            Booking b = new Booking(1, vehicle, parkingSlot, null, endTime, 100.0);
            assertNull(b.getStartTime(), "Start time can be null");
        }

        @Test
        void testConstructorWithNullEndTime() {
            Booking b = new Booking(1, vehicle, parkingSlot, startTime, null, 100.0);
            assertNull(b.getEndTime(), "End time can be null");
        }

        @Test
        void testConstructorWithVeryLargeAmount() {
            Booking b = new Booking(1, vehicle, parkingSlot, startTime, endTime, Double.MAX_VALUE);
            assertEquals(Double.MAX_VALUE, b.getAmount(), "Very large amount should be allowed");
        }
    }

    @Nested
    class GetterTests {

        @Test
        void testGetBookingId() {
            assertEquals(1, booking.getBookingId(), "Should return correct booking ID");
        }

        @Test
        void testGetVehicle() {
            assertEquals(vehicle, booking.getVehicle(), "Should return correct vehicle");
        }

        @Test
        void testGetParkingSlot() {
            assertEquals(parkingSlot, booking.getParkingSlot(), "Should return correct parking slot");
        }

        @Test
        void testGetStartTime() {
            assertEquals(startTime, booking.getStartTime(), "Should return correct start time");
        }

        @Test
        void testGetEndTime() {
            assertEquals(endTime, booking.getEndTime(), "Should return correct end time");
        }

        @Test
        void testGetAmount() {
            assertEquals(100.0, booking.getAmount(), "Should return correct amount");
        }

        @Test
        void testGetBookingStatus() {
            assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus(), "Initial status should be ACTIVE");
        }

        @Test
        void testGettersAreReadOnly() {
            int id = booking.getBookingId();
            Vehicle v = booking.getVehicle();

            assertEquals(id, booking.getBookingId(), "Multiple calls should return same value");
            assertEquals(v, booking.getVehicle(), "Multiple calls should return same reference");
        }
    }

    @Nested
    class CompleteBookingTests {

        @Test
        void testCompleteBookingChangesStatusToCompleted() {
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Status should be COMPLETED");
        }

        @Test
        void testCompleteBookingOnActiveBooking() {
            assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus(), "Should start as ACTIVE");
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should change to COMPLETED");
        }

        @Test
        void testCompleteBookingMultipleTimes() {
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should be COMPLETED");

            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should remain COMPLETED");
        }

        @Test
        void testCompleteBookingDoesNotChangeOtherFields() {
            int originalId = booking.getBookingId();
            double originalAmount = booking.getAmount();
            Vehicle originalVehicle = booking.getVehicle();

            booking.completeBooking();

            assertEquals(originalId, booking.getBookingId(), "Booking ID should not change");
            assertEquals(originalAmount, booking.getAmount(), "Amount should not change");
            assertEquals(originalVehicle, booking.getVehicle(), "Vehicle should not change");
        }

        @Test
        void testCompleteAfterCancel() {
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should be CANCELLED");

            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should change to COMPLETED");
        }
    }

    @Nested
    class CancelBookingTests {

        @Test
        void testCancelBookingChangesStatusToCancelled() {
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Status should be CANCELLED");
        }

        @Test
        void testCancelBookingOnActiveBooking() {
            assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus(), "Should start as ACTIVE");
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should change to CANCELLED");
        }

        @Test
        void testCancelBookingMultipleTimes() {
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should be CANCELLED");

            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should remain CANCELLED");
        }

        @Test
        void testCancelBookingDoesNotChangeOtherFields() {
            int originalId = booking.getBookingId();
            double originalAmount = booking.getAmount();
            ParkingSlot originalSlot = booking.getParkingSlot();

            booking.cancelBooking();

            assertEquals(originalId, booking.getBookingId(), "Booking ID should not change");
            assertEquals(originalAmount, booking.getAmount(), "Amount should not change");
            assertEquals(originalSlot, booking.getParkingSlot(), "Parking slot should not change");
        }

        @Test
        void testCancelAfterComplete() {
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should be COMPLETED");

            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should change to CANCELLED");
        }
    }

    @Nested
    class ToStringTests {

        @Test
        void testToStringContainsBookingId() {
            String result = booking.toString();
            assertTrue(result.contains("bookingId=1"), "toString should contain booking ID");
        }

        @Test
        void testToStringContainsAmount() {
            String result = booking.toString();
            assertTrue(result.contains("amount=100.0"), "toString should contain amount");
        }

        @Test
        void testToStringContainsStatus() {
            String result = booking.toString();
            assertTrue(result.contains("bookingStatus=ACTIVE"), "toString should contain status");
        }

        @Test
        void testToStringAfterComplete() {
            booking.completeBooking();
            String result = booking.toString();
            assertTrue(result.contains("bookingStatus=COMPLETED"), "toString should show COMPLETED status");
        }

        @Test
        void testToStringAfterCancel() {
            booking.cancelBooking();
            String result = booking.toString();
            assertTrue(result.contains("bookingStatus=CANCELLED"), "toString should show CANCELLED status");
        }

        @Test
        void testToStringWithNullVehicle() {
            Booking b = new Booking(1, null, parkingSlot, startTime, endTime, 100.0);
            String result = b.toString();
            assertTrue(result.contains("vehicle=null"), "toString should handle null vehicle");
        }
    }

    @Nested
    class StatusTransitionTests {

        @Test
        void testStatusTransitionActiveToCompleted() {
            assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus(), "Should start ACTIVE");
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should become COMPLETED");
        }

        @Test
        void testStatusTransitionActiveToCancelled() {
            assertEquals(BookingStatus.ACTIVE, booking.getBookingStatus(), "Should start ACTIVE");
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should become CANCELLED");
        }

        @Test
        void testStatusTransitionCompletedToCancelled() {
            booking.completeBooking();
            booking.cancelBooking();
            assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus(), "Should transition to CANCELLED");
        }

        @Test
        void testStatusTransitionCancelledToCompleted() {
            booking.cancelBooking();
            booking.completeBooking();
            assertEquals(BookingStatus.COMPLETED, booking.getBookingStatus(), "Should transition to COMPLETED");
        }
    }
}