package test;

import src.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class ParkingSlotTest {
    private ParkingSlot compact;
    private ParkingSlot regular;
    private ParkingSlot large;
    private ParkingSlot handicapped;
    private static final LocalDateTime T1 = LocalDateTime.of(2025, 10, 17, 8, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 10, 17, 10, 0);

    @BeforeEach
    void setUp() {
        compact = new ParkingSlot("C1", ParkingSlotType.COMPACT);
        regular = new ParkingSlot("R1", ParkingSlotType.REGULAR);
        large = new ParkingSlot("L1", ParkingSlotType.LARGE);
        handicapped = new ParkingSlot("H1", ParkingSlotType.HANDICAPPED);
    }

    @Nested
    class GetterTest {
        @Test
        void getIdTest() {
            assertEquals(large.getSlotId(), "L1", "Slot id should be L1");
        }

        @Test
        void getSlotTypeTest() {
            assertSame(large.getSlotType(), ParkingSlotType.LARGE, "Slot type mismatched");
        }
    }

    @Nested
    class ConstructorTest {
        @Test
        void slotShouldStartActive() {
            assertTrue(compact.isActive(), "Fresh slot must be active");
        }

        @Test
        void slotShouldCarryCorrectType() {
            assertEquals(ParkingSlotType.COMPACT, compact.getSlotType(), "Slot type mismatch");
        }

        @Test
        void slotShouldHaveEmptyBookingList() {
            assertTrue(compact.getBookings().isEmpty(), "New slot must contain zero bookings");
        }

        @Test
        void slotBalanceShouldStartAtZero() {
            assertEquals(0.0, compact.getBalance(), "Slot wallet must start empty");
        }
    }


    @Nested
    class ActionsTest {
        @Test
        void deactivateShouldMakeSlotInactive() {
            compact.deactivate();
            assertFalse(compact.isActive(), "Deactivated slot must report inactive");
        }

        @Test
        void activateShouldRestoreActiveFlag() {
            compact.deactivate();
            compact.activate();
            assertTrue(compact.isActive(), "Re-activated slot must report active");
        }
    }



    @Nested
    class CompatibilityTest {
        @Test
        void motorcycleShouldAcceptCompactRegularLarge() {
            assertTrue(compact.isCompatible(VehicleType.MOTORCYCLE, T1, T2),
                    "Motorcycle must be compatible with COMPACT");
            assertTrue(regular.isCompatible(VehicleType.MOTORCYCLE, T1, T2),
                    "Motorcycle must be compatible with REGULAR");
            assertTrue(large.isCompatible(VehicleType.MOTORCYCLE, T1, T2),
                    "Motorcycle must be compatible with LARGE");
            assertFalse(handicapped.isCompatible(VehicleType.MOTORCYCLE, T1, T2),
                    "Motorcycle must NOT be compatible with HANDICAPPED");
        }

        @Test
        void carShouldAcceptRegularLargeOnly() {
            assertFalse(compact.isCompatible(VehicleType.CAR, T1, T2),
                    "CAR must NOT be compatible with COMPACT");
            assertTrue(regular.isCompatible(VehicleType.CAR, T1, T2),
                    "CAR must be compatible with REGULAR");
            assertTrue(large.isCompatible(VehicleType.CAR, T1, T2),
                    "CAR must be compatible with LARGE");
        }

        @Test
        void busShouldAcceptLargeOnly() {
            assertTrue(large.isCompatible(VehicleType.BUS, T1, T2),
                    "BUS must be compatible with LARGE");
            assertFalse(regular.isCompatible(VehicleType.BUS, T1, T2),
                    "BUS must NOT be compatible with REGULAR");
        }

        @Test
        void bicycleShouldAcceptAllTypes() {
            assertTrue(compact.isCompatible(VehicleType.BICYCLE, T1, T2),
                    "BICYCLE must be compatible with COMPACT");
            assertTrue(regular.isCompatible(VehicleType.BICYCLE, T1, T2),
                    "BICYCLE must be compatible with REGULAR");
            assertTrue(large.isCompatible(VehicleType.BICYCLE, T1, T2),
                    "BICYCLE must be compatible with LARGE");
            assertTrue(handicapped.isCompatible(VehicleType.BICYCLE, T1, T2),
                    "BICYCLE must be compatible with HANDICAPPED");
        }

        @Test
        void microCarShouldAcceptCompactRegularOnly() {
            assertTrue(compact.isCompatible(VehicleType.MICROCAR, T1, T2),
                    "MICROCAR must be compatible with COMPACT");
            assertTrue(regular.isCompatible(VehicleType.MICROCAR, T1, T2),
                    "MICROCAR must be compatible with REGULAR");
            assertFalse(large.isCompatible(VehicleType.MICROCAR, T1, T2),
                    "MICROCAR must NOT be compatible with LARGE");
        }

        @Test
        void inactiveSlotShouldRejectEveryType() {
            compact.deactivate();
            assertFalse(compact.isCompatible(VehicleType.MOTORCYCLE, T1, T2),
                    "Inactive slot must be incompatible regardless of type");
        }
    }


    @Nested
    class AvailabilityTest {
        @Test
        void slotShouldBeAvailableWhenNoBookingsExist() {
            assertTrue(compact.isAvailable(T1, T2), "Empty slot must be available");
        }

        @Test
        void slotShouldDetectOverlap() {
            Booking b = new Booking(1, new Vehicle(1, VehicleType.CAR, 100),
                    compact, T1, T2, 20);
            compact.getBookings().add(b);

            assertFalse(compact.isAvailable(T1.minusHours(1), T1.plusHours(1)),
                    "Partial front overlap must block");
            assertFalse(compact.isAvailable(T1.plusHours(1), T2.plusHours(1)),
                    "Partial back overlap must block");
            assertFalse(compact.isAvailable(T1, T2),
                    "Exact overlap must block");
            assertTrue(compact.isAvailable(T2, T2.plusHours(2)),
                    "Non-overlapping future window must be free");
            assertTrue(compact.isAvailable(T1.minusHours(2), T1),
                    "Non-overlapping past window must be free");
        }
    }
}
