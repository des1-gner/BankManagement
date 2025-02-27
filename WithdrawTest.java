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
        // testSetId = 2513
)
class WithdrawTest {

    Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        this.bank = new Bank();
        System.setOut(new PrintStream(outContent));
        // Create an account for testing, considering the initial 1000 balance
        bank.AL.add(new Account("Oisin Aeonn", 39523200, "2809", 3000)); // Total balance: 4000
    }

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    @SpiraTestCase(testCaseId = 16231)
    public void testWithdrawMenuPrompts() {
        simulateInput("39523200\n2809\n1000\n");
        bank.withdraw();

        String output = outContent.toString();
        assertAll("Check all withdraw prompts",
                () -> assertTrue(output.contains("Enter User's 8 digit account number:"), "Account number prompt should be present"),
                () -> assertTrue(output.contains("Enter User's pin code:"), "PIN prompt should be present"),
                () -> assertTrue(output.contains("Amount to be Withdrawn:"), "Withdrawal amount prompt should be present")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16232)
    public void testNormalWithdraw() {
        simulateInput("39523200\n2809\n1000\n");
        bank.withdraw();

        assertEquals(3000, bank.AL.get(0).getAmount(), "Account balance should be reduced by withdrawal amount");
    }

    @Test
    @SpiraTestCase(testCaseId = 16233)
    public void testWithdrawInvalidAccount() {
        simulateInput("12345678\n2809\n1000\n");
        bank.withdraw();

        assertTrue(outContent.toString().contains("Account not Found"), "Should display error for invalid account");
    }

    @Test
    @SpiraTestCase(testCaseId = 16234)
    public void testWithdrawInvalidPIN() {
        simulateInput("39523200\n1111\n1000\n");
        bank.withdraw();

        assertTrue(outContent.toString().contains("PIN"), "Should display a unique error for invalid PIN");
    }

    @Test
    @SpiraTestCase(testCaseId = 16235)
    public void testWithdrawInsufficientFunds() {
        simulateInput("39523200\n2809\n5000\n");
        bank.withdraw();

        assertTrue(outContent.toString().contains("This person does not have this much balance in their account"),
                "Should display error for insufficient funds");
    }

    @Test
    @SpiraTestCase(testCaseId = 16236)
    public void testWithdrawNegativeAmount() {
        simulateInput("39523200\n2809\n-1000\n");
        bank.withdraw();

        assertEquals(4000, bank.AL.get(0).getAmount(), "Account balance should not change for negative withdrawal amount");
    }

    @Test
    @SpiraTestCase(testCaseId = 16237)
    public void testWithdrawZeroAmount() {
        simulateInput("39523200\n2809\n0\n");
        bank.withdraw();

        assertEquals(4000, bank.AL.get(0).getAmount(), "Account balance should not change for zero withdrawal amount");
    }

    @Test
    @SpiraTestCase(testCaseId = 16238)
    public void testWithdrawEntireBalance() {
        simulateInput("39523200\n2809\n4000\n");
        bank.withdraw();

        assertEquals(0, bank.AL.get(0).getAmount(), "Account balance should be zero after withdrawing entire balance");
    }
}