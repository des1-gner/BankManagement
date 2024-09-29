import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258,
        rssToken = "{332767F9-C19C-42B0-BE3C-3EC80E04AC56}",
        releaseId = 1467//,
        // testSetId = 2512
)
class TransferTest {

    Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        this.bank = new Bank();
        System.setOut(new PrintStream(outContent));
        // Create two accounts for testing
        bank.AL.add(new Account("Oisin Aeonn", 39523200, "2809", 3000));
        bank.AL.add(new Account("John Doe", 39523201, "1234", 2000));
    }

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    @SpiraTestCase(testCaseId = 16221)
    public void testTransferMenuPrompts() {
        simulateInput("39523200\n2809\n39523201\n1000\n");
        bank.transfer();

        String output = outContent.toString();
        assertAll("Check all transfer prompts",
                () -> assertTrue(output.contains("Enter sender's 8 digit account number:"), "Sender account prompt should be present"),
                () -> assertTrue(output.contains("Enter Sender's pin code:"), "Sender PIN prompt should be present"),
                () -> assertTrue(output.contains("Enter receiver's 8 digit account number:"), "Receiver account prompt should be present"),
                () -> assertTrue(output.contains("Amount to be transferred:"), "Transfer amount prompt should be present")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16222)
    public void testNormalTransfer() {
        simulateInput("39523200\n2809\n39523201\n1000\n");
        bank.transfer();

        assertAll("Check normal transfer",
                () -> assertEquals(3000, bank.AL.get(0).getAmount(), "Sender's balance should be reduced"),
                () -> assertEquals(4000, bank.AL.get(1).getAmount(), "Receiver's balance should be increased")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16223)
    public void testTransferInvalidSenderAccount() {
        simulateInput("12345678\n2809\n39523201\n1000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Account not Found"), "Should display error for invalid sender account");
    }

    @Test
    @SpiraTestCase(testCaseId = 16224)
    public void testTransferInvalidReceiverAccount() {
        simulateInput("39523200\n2809\n12345678\n1000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Receiver's account not Found"), "Should display error for invalid receiver account");
    }

    @Test
    @SpiraTestCase(testCaseId = 16225)
    public void testTransferInvalidPIN() {
        simulateInput("39523200\n1111\n39523201\n1000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("PIN"), "Should display a unique error for invalid PIN");
    }

    @Test
    @SpiraTestCase(testCaseId = 16226)
    public void testTransferInsufficientFunds() {
        simulateInput("39523200\n2809\n39523201\n5000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Sender does not have this much balance in his account"), "Should display error for insufficient funds");
    }

    @Test
    @SpiraTestCase(testCaseId = 16227)
    public void testTransferNegativeAmount() {
        simulateInput("39523200\n2809\n39523201\n-1000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Invalid amount"), "Should display an error for a negative amount");
    }

    @Test
    @SpiraTestCase(testCaseId = 16228)
    public void testTransferToSameAccount() {
        simulateInput("39523200\n2809\n39523200\n1000\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Cannot transfer to the same account"), "Should display error for transfer to same account");
    }

    @Test
    @SpiraTestCase(testCaseId = 16229)
    public void testTransferZeroAmount() {
        simulateInput("39523200\n2809\n39523201\n0\n");
        bank.transfer();

        assertTrue(outContent.toString().contains("Invalid amount"), "Should display error for zero amount");
    }
}