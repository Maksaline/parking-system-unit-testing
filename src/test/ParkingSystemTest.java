package test;

import src.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;


public class ParkingSystemTest {
    private Vehicle car;
    private ParkingSlot regular;
    private static ParkingSystem system;
    private static final LocalDateTime T1 = LocalDateTime.of(2025, 10, 17, 8, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 10, 17, 10, 0);

    @BeforeEach
    void setUp() {
        system = ParkingSystem.getInstance();
        system.setVehicles(new java.util.ArrayList<>());
        system.setParkingSlots(new java.util.ArrayList<>());
        system.setBookings(new java.util.ArrayList<>());
        system.setSYSTEM_WALLET(new Wallet());

        car = new Vehicle(1, VehicleType.CAR, 1000);
        regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        system.addVehicle(car);
        system.addParkingSlot(regular);
    }

    @Nested
    class InstanceTest {
        @Test
        void systemShouldReturnSameInstance() {
            ParkingSystem second = ParkingSystem.getInstance();
            assertSame(system, second, "Singleton must return identical instance");
        }
    }



    @Nested
    class AvailabilityTest {
        @Test
        void shouldFindCompatibleSlot() {
            List<ParkingSlot> list = system.getAvailableParkingSlots(car, T1, T2);
            assertEquals(1, list.size(), "Exactly one compatible slot expected");
            assertEquals(regular, list.get(0), "Returned slot must be the registered one");
        }

        @Test
        void shouldReturnEmptyListWhenNoSlotCompatible() {
            Vehicle bus = new Vehicle(2, VehicleType.BUS, 1000);
            system.addVehicle(bus);
            List<ParkingSlot> list = system.getAvailableParkingSlots(bus, T1, T2);
            assertTrue(list.isEmpty(), "No LARGE slot registered, list must be empty");
        }
    }


    @Nested
    class BookingTest {
        @Test
        void bookingShouldRejectEndBeforeStart() {
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> system.book(car, regular, T2, T1),
                    "End before start must throw");
            assertTrue(ex.getMessage().contains("End time must be after start time"),
                    "Exception message must mention time order");
        }

        @Test
        void bookingShouldRejectIncompatibleSlot() {
            ParkingSlot compact = new ParkingSlot("C1", ParkingSlotType.COMPACT);
            system.addParkingSlot(compact);
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> system.book(car, compact, T1, T2),
                    "CAR vs COMPACT must be rejected");
            assertTrue(ex.getMessage().contains("not compatible"),
                    "Exception message must mention compatibility");
        }

        @Test
        void bookingShouldChargeVehicleImmediately() {
            double before = car.getBalance();
            system.book(car, regular, T1, T2);
            assertEquals(before - 20, car.getBalance(),
                    "Vehicle must be charged full amount at booking");
            assertEquals(20, system.getBalance(),
                    "System wallet must receive full amount");
        }

        @Test
        void completeBookingShouldTransfer80PercentToSlot() {
            Booking b = system.book(car, regular, T1, T2); // 20 currency
            system.completeBooking(b);
            assertEquals(20 * 0.8, regular.getBalance(),
                    "Slot wallet must receive 80% of fare");
            assertEquals(20 * 0.2, system.getBalance(),
                    "System must retain 20%");
        }

        @Test
        void cancelBookingShouldRefund90PercentToVehicle() {
            double before = car.getBalance();
            Booking b = system.book(car, regular, T1, T2); // 20 currency
            system.cancelBooking(b);
            assertEquals(before - 20 + 20 * 0.9, car.getBalance(),
                    "Vehicle must receive 90% refund");
            assertEquals(20 * 0.1, system.getBalance(),
                    "System must keep 10% cancellation fee");
        }

        @Test
        void bookingShouldTruncateFractionalHours() {
            LocalDateTime t90 = T1.plusMinutes(90); // 1.5h -> 1h billed
            Booking b = system.book(car, regular, T1, t90);
            assertEquals(10, b.getAmount(),
                    "90 minutes must be truncated to 1 hour");
        }
    }
}