import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258,
        rssToken = "{332767F9-C19C-42B0-BE3C-3EC80E04AC56}",
        releaseId = 1453,
        testSetId = 2508
)
class AddNewRecordTest {

    Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        this.bank = new Bank();
        System.setOut(new PrintStream(outContent));
    }

    private void simulateInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    @SpiraTestCase(testCaseId = 16204)
    public void testAddNewRecordPrompts() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n2024\n");
        bank.addNewRecord();

        String output = outContent.toString();
        assertAll("Check all prompts",
                () -> assertTrue(output.contains("Enter name of Account Holder:"), "Name prompt should be present"),
                () -> assertTrue(output.contains("Enter an 8 digit Account Number (contact manager for its allocation):"), "Account number prompt should be present"),
                () -> assertTrue(output.contains("Enter PIN for Account Holder:"), "PIN prompt should be present"),
                () -> assertTrue(output.contains("Default amount of 1000 is already added to the account, to add more money, write that amount else enter zero:"), "Amount prompt should be present")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16153)
    public void testAddNewRecordNormalCase() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n2024\n");
        bank.addNewRecord();

        assertAll("New account details",
                () -> assertEquals(1, bank.AL.size()),
                () -> {
                    Account account = bank.AL.get(0);
                    assertAll("Account properties",
                            () -> assertEquals("Oisin Aeonn", account.getName()),
                            () -> assertEquals(39523200, account.getAccountNumber()),
                            () -> assertEquals("2809", account.getPIN()),
                            () -> assertEquals(3024, account.getAmount())
                    );
                }
        );
    }

    // Name tests
    @Test
    @SpiraTestCase(testCaseId = 16205)
    public void testAddNewRecordNameWithSpecialCharacters() {
        simulateInput("Oisin $%# Aeonn\n39523200\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals("Oisin $%# Aeonn", bank.AL.get(0).getName(),
                "Name with special characters should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16206)
    public void testAddNewRecordEmptyName() {
        simulateInput("\n39523200\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals("", bank.AL.get(0).getName(),
                "Empty name should not be accepted");
    }

    // Account number tests
    @Test
    @SpiraTestCase(testCaseId = 16207)
    public void testAddNewRecordInvalidAccountNumber() {
        simulateInput("Oisin Aeonn\n3952320\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals(3952320, bank.AL.get(0).getAccountNumber(),
                "Invalid account number should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16208)
    public void testAddNewRecordDuplicateRecord() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n2024\n");
        bank.addNewRecord();
        simulateInput("Oisin Aeonn\n39523200\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals(2, bank.AL.size(),
                "Duplicate account number should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16209)
    public void testAddNewRecordNonNumericAccountNumber() {
        simulateInput("Oisin Aeonn\ns3952320\n2809\n2024\n");

        assertThrows(InputMismatchException.class, () -> bank.addNewRecord(),
                "Should throw InputMismatchException for non-numeric account number");
    }

    @Test
    @SpiraTestCase(testCaseId = 16210)
    public void testAddNewRecordNegativeAccountNumber() {
        simulateInput("Oisin Aeonn\n-39523200\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals(-39523200, bank.AL.get(0).getAccountNumber(),
                "Negative account number should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16211)
    public void testAddNewRecordZeroAccountNumber() {
        simulateInput("Oisin Aeonn\n00000000\n2809\n2024\n");
        bank.addNewRecord();

        assertNotEquals(0, bank.AL.get(0).getAccountNumber(),
                "Zero account number should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16212)
    public void testAddNewRecordEmptyAccountNumber() {
        simulateInput("Oisin Aeonn\n\n2809\n2024\n");

        assertThrows(NoSuchElementException.class, () -> bank.addNewRecord(),
                "Should throw NoSuchElementException for empty account number");
    }

    @Test
    @SpiraTestCase(testCaseId = 16213)
    public void testAddNewRecordDecimalAccountNumber() {
        simulateInput("Oisin Aeonn\n3952320.5\n2809\n2024\n");

        assertThrows(InputMismatchException.class, () -> bank.addNewRecord(),
                "Should throw InputMismatchException for decimal account number");
    }

    // PIN tests
    @Test
    @SpiraTestCase(testCaseId = 16214)
    public void testAddNewRecordPINWithLetters() {
        simulateInput("Oisin Aeonn\n39523200\n28a9\n2024\n");
        bank.addNewRecord();

        assertEquals("28a9", bank.AL.get(0).getPIN(),
                "PIN with letters should be accepted");
    }

    // Amount tests
    @Test
    @SpiraTestCase(testCaseId = 16215)
    public void testAddNewRecordZeroAdditionalAmount() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n0\n");
        bank.addNewRecord();

        assertEquals(1000, bank.AL.get(0).getAmount());
    }

    @Test
    @SpiraTestCase(testCaseId = 16216)
    public void testAddNewRecordNegativeAmountNoError() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n-2024\n");
        bank.addNewRecord();

        assertNotEquals(-1024, bank.AL.get(0).getAmount(),
                "Negative amount should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16217)
    public void testAddNewRecordEmptyAmount() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n\n");

        assertThrows(NoSuchElementException.class, () -> bank.addNewRecord(),
                "Should throw NoSuchElementException for empty amount");
    }

    @Test
    @SpiraTestCase(testCaseId = 16218)
    public void testAddNewRecordWithHugeAmount() {
        simulateInput("Oisin Aeonn\n39523200\n2809\n1.8e308\n");
        bank.addNewRecord();

        assertFalse(Double.isInfinite(bank.AL.get(0).getAmount()),
                "Huge amount resulting in infinity should not be accepted");
    }

    @Test
    @SpiraTestCase(testCaseId = 16219)
    public void testAddNewRecordAmountWithCharacters() {
        simulateInput("Oisin Aeonn\n39523211\n2809\n2024a\n");

        assertThrows(InputMismatchException.class, () -> bank.addNewRecord(),
                "Should throw InputMismatchException for amount with characters");
    }

    @Test
    @SpiraTestCase(testCaseId = 16220)
    public void testAddNewRecordAmountWithDecimalCharacters() {
        simulateInput("Oisin Aeonn\n39523212\n2809\n2024.50\n");
        bank.addNewRecord();

        assertEquals(3024.5, bank.AL.get(0).getAmount());
    }
}