# Parking Slot Booking System - Test Suite

A comprehensive JUnit test suite for the Parking Slot Booking System, designed to validate system behavior, identify defects, and document assumptions.

## 📋 Overview

This repository contains a complete unit testing implementation for a parking slot booking system with intentional defects. The test suite is built using JUnit 5 and follows software testing best practices to:

- Capture actual system behavior through tests
- Document business rules and edge cases
- Identify potential defects without modifying source code
- Provide a foundation for quality assurance

## 🏗️ System Under Test

The parking system manages:
- **Vehicles** with different types (Car, Motorcycle, Bus, Bicycle, Microcar, Truck)
- **Parking Slots** with various types (Compact, Regular, Large, Handicapped)
- **Bookings** with lifecycle management (Active → Completed/Cancelled)
- **Wallets** for financial transactions
- **ParkingSystem** as the central coordinator

### Key Business Rules

- **Pricing**: `price = hours × 10.0 × vehicleTypeRate × slotTypeMultiplier`
- **Booking Flow**: Full payment upfront → 80% to slot on completion OR 90% refund on cancellation
- **Compatibility**: Specific vehicle-slot type restrictions
- **Availability**: Time-window overlap detection

## 📦 Test Suite Structure
```
test/
├── BookingTest.java         # 60+ tests for Booking entity
├── ParkingSlotTest.java     # 25+ tests for slot management
├── ParkingSystemTest.java   # 15+ tests for system operations
├── VehicleTest.java         # 4 tests for vehicle initialization
└── WalletTest.java          # 40+ tests for financial operations
```

### Test Coverage

| Class | Test Count | Coverage Areas |
|-------|------------|----------------|
| **Booking** | 60+ | Constructor validation, status transitions, getter consistency, toString formatting |
| **ParkingSlot** | 25+ | Initialization, activation/deactivation, compatibility matrix, availability detection |
| **ParkingSystem** | 15+ | Singleton pattern, booking validation, financial settlements, time calculations |
| **Vehicle** | 4 | Basic initialization and toString |
| **Wallet** | 40+ | Fund management, transfers, balance validation, error handling |

## 🧪 Running the Tests

### Prerequisites
- Java 11 or higher
- JUnit 5.x
- Maven or Gradle (optional)

### Using IDE
1. Clone the repository
2. Import as a Java project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
3. Ensure JUnit 5 is in the classpath
4. Run individual test classes or the entire suite

### Using Maven
```bash
mvn clean test
```

### Using Gradle
```bash
gradle test
```

## 🔍 Test Methodology

### Test Organization
Tests are organized using JUnit 5's `@Nested` classes for logical grouping:
```java
@Nested
class ConstructorTests { ... }

@Nested
class StatusTransitionTests { ... }

@Nested
class FinancialOperationsTests { ... }
```

### Test Naming Convention
Tests follow descriptive naming: `test<Action><Condition><ExpectedResult>`

Examples:
- `testConstructorWithNegativeAmount()`
- `testCompleteBookingChangesStatusToCompleted()`
- `testTransferMoreThanBalance()`

## 📊 Key Test Scenarios

### Boundary Testing
- ✅ Zero and negative amounts
- ✅ Maximum value handling (Double.MAX_VALUE)
- ✅ Null parameter handling
- ✅ Edge time windows (same start/end)

### Business Logic Validation
- ✅ Vehicle-slot compatibility matrix
- ✅ Time window overlap detection
- ✅ Fractional hour truncation (90 minutes → 1 hour)
- ✅ Financial settlement percentages

### State Transition Testing
- ✅ Active → Completed → Cancelled
- ✅ Multiple status changes
- ✅ Idempotent operations

### Error Handling
- ✅ Insufficient funds
- ✅ Invalid time ranges
- ✅ Incompatible vehicle-slot pairs
- ✅ Null wallet transfers

## 🐛 Identified Behaviors & Assumptions

### Documented Through Tests

1. **Negative Amount Handling**
   - Constructor accepts negative amounts but may store as 0.0
   - Test: `testConstructorWithNegativeAmount()`

2. **Status Transitions**
   - Completed bookings can be cancelled (and vice versa)
   - Tests: `testCompleteAfterCancel()`, `testCancelAfterComplete()`

3. **Time Validation**
   - Constructor allows end time before start time
   - Booking validation happens at system level
   - Tests: `testConstructorWithEndTimeBeforeStartTime()`

4. **Fractional Hour Billing**
   - System truncates fractional hours (not rounds)
   - Test: `bookingShouldTruncateFractionalHours()`

5. **Self-Transfer Behavior**
   - Wallet allows transfer to self (no-op)
   - Test: `testTransferToSelf()`

## 🎯 Testing Philosophy

**As per assignment guidelines:**
- ❌ Source code is **NOT modified**
- ✅ Tests capture **actual behavior** (not ideal behavior)
- ✅ Assumptions are **documented** through test assertions
- ✅ Potential fixes are **suggested** via test names and comments

## 📈 Test Statistics

- **Total Test Methods**: 140+
- **Test Assertions**: 300+
- **Nested Test Classes**: 25+
- **Exception Testing**: 20+ scenarios

## 🤝 Contributing

This test suite was developed as part of a Software Testing & QA assignment. The focus is on:
- Comprehensive coverage
- Clear documentation
- Behavioral validation
- Edge case identification

## 📄 License

This test suite is provided for educational purposes as part of a software testing assignment.

## 🔗 References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- Testing best practices for parking management systems
- Domain-driven design testing patterns

---

**Note**: This is a testing-focused repository. The system under test intentionally contains defects for educational purposes. Tests document behavior as-is, not as-should-be.