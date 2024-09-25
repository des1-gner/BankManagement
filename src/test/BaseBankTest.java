package src.test;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import java.io.*;

public abstract class BaseBankTest extends SpiraConfig {
    protected Bank bank;
    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    protected void simulateInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }

    protected void assertAccountDetails(Account account, String name, int accountNumber, String pin, double amount) {
        assertEquals(name, account.getName());
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(pin, account.getPIN());
        assertEquals(amount, account.getAmount());
    }

    protected void assertOutputContains(String expected) {
        assertTrue(outContent.toString().contains(expected));
    }
}