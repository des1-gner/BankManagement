import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258,
        rssToken = "{332767F9-C19C-42B0-BE3C-3EC80E04AC56}",
        releaseId = 1467//,
        // testSetId = 2510
)
class PrintTest {

    Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        this.bank = new Bank();
        System.setOut(new PrintStream(outContent));

        // Create 5 accounts with the specified names and account numbers
        bank.AL.add(new Account("Oisin Aeonn", 39523200, "2809", 3000));
        bank.AL.add(new Account("Hoshi Aeonn", 39523201, "1234", 2000));
        bank.AL.add(new Account("Elani Shannon-Puszka", 41082220, "5678", 1500));
        bank.AL.add(new Account("Cei Aeonn", 39523202, "9876", 5000));
        bank.AL.add(new Account("Eija Aeonn", 39523203, "4321", 100));
    }

    @Test
    @SpiraTestCase(testCaseId = 16239)
    public void testPrintAllComponents() {
        bank.print();
        String output = outContent.toString();

        assertAll("Check all components of printed output",
                () -> assertTrue(output.contains("Name: Oisin Aeonn"), "Should contain Oisin's name"),
                () -> assertTrue(output.contains("Account Number: 39523200"), "Should contain Oisin's account number"),
                () -> assertTrue(output.contains("Balance: 4000.0"), "Should contain Oisin's balance"),
                () -> assertTrue(output.contains("Name: Hoshi Aeonn"), "Should contain Hoshi's name"),
                () -> assertTrue(output.contains("Name: Elani Shannon-Puszka"), "Should contain Elani's name"),
                () -> assertTrue(output.contains("Account Number: 41082220"), "Should contain Elani's account number"),
                () -> assertTrue(output.contains("Name: Cei Aeonn"), "Should contain Cei's name"),
                () -> assertTrue(output.contains("Name: Eija Aeonn"), "Should contain Eija's name")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16240)
    public void testPrintOrder() {
        bank.print();
        String output = outContent.toString().trim(); // Trim to remove leading/trailing whitespaces
        String[] accounts = output.split("\n\n");

        assertTrue(accounts[0].trim().startsWith("Name: Oisin Aeonn"), "First account should be Oisin Aeonn");
        assertTrue(accounts[accounts.length - 1].trim().startsWith("Name: Eija Aeonn"), "Last account should be Eija Aeonn");
    }

    @Test
    @SpiraTestCase(testCaseId = 16241)
    public void testPrintFormat() {
        bank.print();
        String output = outContent.toString();
        String[] accounts = output.split("\n\n");

        for (String account : accounts) {
            String[] lines = account.trim().split("\n");
            assertEquals(3, lines.length, "Each account should have 3 lines of information");
            assertTrue(lines[0].startsWith("Name:"), "First line should start with 'Name:'");
            assertTrue(lines[1].startsWith("Account Number:"), "Second line should start with 'Account Number:'");
            assertTrue(lines[2].startsWith("Balance:"), "Third line should start with 'Balance:'");
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 16242)
    public void testPrintEmptyBank() {
        bank.AL.clear();
        bank.print();
        String output = outContent.toString();

        assertTrue(output.trim().isEmpty(), "Output should be empty for a bank with no accounts");
    }

}