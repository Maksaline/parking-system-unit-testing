package test;

import src.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;


public class WalletTest {
    private Wallet wallet;
    private Wallet targetWallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet();
        targetWallet = new Wallet();
    }

    @Nested
    class ConstructorTests {

        @Test
        void testDefaultConstructor() {
            Wallet w = new Wallet();
            assertEquals(0.0, w.getBalance(), "Balance should be zero");
        }

        @Test
        void testParameterizedConstructor() {
            Wallet w = new Wallet(100.0);
            assertEquals(100.0, w.getBalance(), "Balance not matching");
        }

        @Test
        void testConstructorWithNegativeBalance() {
            Wallet w = new Wallet(-50.0);
            assertTrue(w.getBalance() >= 0, "Can not create a wallet with negative value");
        }

        @Test
        void testConstructorWithLargeBalance() {
            Wallet w = new Wallet(Double.MAX_VALUE);
            assertEquals(Double.MAX_VALUE, w.getBalance(), "Value should be 1.7976931348623157E308");
        }
    }

    @Nested
    class AddFundsTests {

        @Test
        void testAddFundsToEmptyWallet() {
            wallet.addFunds(50.0);
            wallet.addFunds(30.0);
            assertEquals(80.0, wallet.getBalance(), "Balance not added");
        }

        @Test
        void testAddFundsToExistingBalance() {
            wallet = new Wallet(100.0);
            wallet.addFunds(50.0);
            assertEquals(150.0, wallet.getBalance(), "Balance not added");
        }

        @Test
        void testAddVerySmallAmount() {
            wallet.addFunds(0.01);
            assertEquals(0.01, wallet.getBalance(), "Balance not adding little amount");
        }

        @Test
        void testAddVeryLargeAmount() {
            wallet.addFunds(1_000_000.0);
            assertEquals(1_000_000.0, wallet.getBalance(), "Balance not adding large amount");
        }

        @Test
        void testMultipleAddFunds() {
            wallet.addFunds(10.0);
            wallet.addFunds(20.0);
            wallet.addFunds(30.0);
            assertEquals(60.0, wallet.getBalance(), "Balance not added");
        }

        @Test
        void testAddZeroAmount() {
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.addFunds(0.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testAddNegativeAmount() {
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.addFunds(-50.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testBalanceUnchangedAfterFailedAdd() {
            wallet = new Wallet(100.0);
            try {
                wallet.addFunds(-50.0);
            } catch (RuntimeException e) {
            }
            assertEquals(100.0, wallet.getBalance(), "Balanced changed after failed attempt");
        }
    }


    @Nested
    class DeductFundsTests {

        @Test
        void testDeductLessThanBalance() {
            wallet = new Wallet(100.0);
            wallet.deductFunds(30.0);
            assertEquals(70.0, wallet.getBalance(), "Balance not deducted");
        }

        @Test
        void testDeductExactBalance() {
            wallet = new Wallet(100.0);
            wallet.deductFunds(100.0);
            assertEquals(0.0, wallet.getBalance(), "Balance not deducted");
        }

        @Test
        void testDeductVerySmallAmount() {
            wallet = new Wallet(100.0);
            wallet.deductFunds(0.01);
            assertEquals(99.99, wallet.getBalance(), "Balance not deducting small amount");
        }

        @Test
        void testMultipleDeductFunds() {
            wallet = new Wallet(100.0);
            wallet.deductFunds(10.0);
            wallet.deductFunds(20.0);
            wallet.deductFunds(30.0);
            assertEquals(40.0, wallet.getBalance(), "Balance not deducting multiple times");
        }

        @Test
        void testDeductMoreThanBalance() {
            wallet = new Wallet(50.0);
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.deductFunds(100.0)
            );
            assertEquals("Insufficient funds in wallet.", exception.getMessage());
        }

        @Test
        void testDeductFromEmptyWallet() {
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.deductFunds(10.0)
            );
            assertEquals("Insufficient funds in wallet.", exception.getMessage());
        }

        @Test
        void testDeductZeroAmount() {
            wallet = new Wallet(100.0);
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.deductFunds(0.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testDeductNegativeAmount() {
            wallet = new Wallet(100.0);
            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.deductFunds(-50.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testBalanceUnchangedAfterInsufficientFunds() {
            wallet = new Wallet(50.0);
            try {
                wallet.deductFunds(100.0);
            } catch (RuntimeException e) {
            }
            assertEquals(50.0, wallet.getBalance(), "Balance got deducted with larger amount");
        }

        @Test
        void testBalanceUnchangedAfterInvalidAmount() {
            wallet = new Wallet(100.0);
            try {
                wallet.deductFunds(-50.0);
            } catch (RuntimeException e) {

            }
            assertEquals(100.0, wallet.getBalance(), "Balance got deducted with invalid amount");
        }
    }


    @Nested
    class TransferFundsTests {

        @Test
        void testTransferPositiveAmount() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(50.0);

            wallet.transferFunds(targetWallet, 30.0);

            assertEquals(70.0, wallet.getBalance(), "Sender balance not deducted");
            assertEquals(80.0, targetWallet.getBalance(), "Receiver balance not increasing");
        }

        @Test
        void testTransferEntireBalance() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(0.0);

            wallet.transferFunds(targetWallet, 100.0);

            assertEquals(0.0, wallet.getBalance(), "Sender balance not deducted");
            assertEquals(100.0, targetWallet.getBalance(), "Receiver balance not increasing");
        }

        @Test
        void testTransferVerySmallAmount() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(0.0);

            wallet.transferFunds(targetWallet, 0.01);

            assertEquals(99.99, wallet.getBalance(), "Sender balance not deducted");
            assertEquals(0.01, targetWallet.getBalance(), "Receiver balance not increasing");
        }

        @Test
        void testMultipleTransfers() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(50.0);

            wallet.transferFunds(targetWallet, 20.0);
            wallet.transferFunds(targetWallet, 10.0);
            wallet.transferFunds(targetWallet, 30.0);

            assertEquals(40.0, wallet.getBalance(), "Sender balance not deducted");
            assertEquals(110.0, targetWallet.getBalance(), "Receiver balance not increasing");
        }

        @Test
        void testTransferToSelf() {
            wallet = new Wallet(100.0);

            wallet.transferFunds(wallet, 50.0);

            assertEquals(100.0, wallet.getBalance(), "Balanced got transferred to own wallet");
        }

        @Test
        void testTransferMoreThanBalance() {
            wallet = new Wallet(50.0);
            targetWallet = new Wallet(0.0);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.transferFunds(targetWallet, 100.0)
            );
            assertEquals("Insufficient funds in wallet.", exception.getMessage());
        }

        @Test
        void testTransferFromEmptyWallet() {
            wallet = new Wallet(0.0);
            targetWallet = new Wallet(50.0);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.transferFunds(targetWallet, 10.0)
            );
            assertEquals("Insufficient funds in wallet.", exception.getMessage());
        }

        @Test
        void testTransferZeroAmount() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(0.0);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.transferFunds(targetWallet, 0.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testTransferNegativeAmount() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(0.0);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> wallet.transferFunds(targetWallet, -50.0)
            );
            assertEquals("Invalid amount. Amount must be positive.", exception.getMessage());
        }

        @Test
        void testWalletsUnchangedAfterInsufficientFunds() {
            wallet = new Wallet(50.0);
            targetWallet = new Wallet(100.0);

            try {
                wallet.transferFunds(targetWallet, 100.0);
            } catch (RuntimeException e) {
            }

            assertEquals(50.0, wallet.getBalance(), "Invalid transaction happened");
            assertEquals(100.0, targetWallet.getBalance(), "Invalid transaction happened");
        }

        @Test
        void testWalletsUnchangedAfterInvalidAmount() {
            wallet = new Wallet(100.0);
            targetWallet = new Wallet(50.0);

            try {
                wallet.transferFunds(targetWallet, -50.0);
            } catch (RuntimeException e) {
            }

            assertEquals(100.0, wallet.getBalance(), "Invalid transaction happened");
            assertEquals(50.0, targetWallet.getBalance(), "Invalid transaction happened");
        }

        @Test
        void testTransferToNullWallet() {
            wallet = new Wallet(100.0);

            assertThrows(
                    NullPointerException.class,
                    () -> wallet.transferFunds(null, 50.0)
            );
        }
    }

    @Nested
    class GetBalanceTests {

        @Test
        void testGetBalanceNewWallet() {
            assertEquals(0.0, wallet.getBalance(), "Wrong balance displayed");
        }

        @Test
        void testGetBalanceAfterAddFunds() {
            wallet.addFunds(100.0);
            assertEquals(100.0, wallet.getBalance(), "Wrong balance displayed");
        }

        @Test
        void testGetBalanceAfterDeductFunds() {
            wallet = new Wallet(100.0);
            wallet.deductFunds(30.0);
            assertEquals(70.0, wallet.getBalance(), "Wrong balance displayed");
        }
    }
}