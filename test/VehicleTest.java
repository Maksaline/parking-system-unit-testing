package test;

import src.Vehicle;
import src.VehicleType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleTest {

    @Test
    void vehicleShouldCarryGivenIdAndType() {
        Vehicle v = new Vehicle(99, VehicleType.TRUCK, 500);
        assertEquals(99, v.getVehicleId(), "Vehicle ID mismatch");
        assertEquals(VehicleType.TRUCK, v.getVehicleType(), "Vehicle type mismatch");
    }

    @Test
    void vehicleShouldStartWithGivenBalance() {
        Vehicle v = new Vehicle(1, VehicleType.CAR, 777);
        assertEquals(777, v.getBalance(), "Initial balance must be stored in wallet");
    }

    @Test
    void negativeInitialBalanceTest() {
        Vehicle v = new Vehicle(1, VehicleType.CAR, -500);
        assertTrue(v.getBalance() >= 0, "Initial Balance can't be negative");
    }

    @Test
    void vehicleToStringShouldContainKeyFields() {
        Vehicle v = new Vehicle(42, VehicleType.BICYCLE, 123);
        String s = v.toString();
        assertTrue(s.contains("42"), "toString must contain vehicleId");
        assertTrue(s.contains("BICYCLE"), "toString must contain vehicleType");
        assertTrue(s.contains("123"), "toString must contain walletBalance");
    }
}
