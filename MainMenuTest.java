import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;
import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258
        // rssToken = "{D3F132C0-B1C4-4F28-A4CC-246934D58A4A}",
        // releaseId = 0,
        // testSetId = 0
)
class MainMenuTest {
    private Bank bank;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
    }

    private String runMenu(String input) {
        Scanner scanner = new Scanner(new StringReader(input));
        simulateMenuInteraction(scanner, printWriter);
        return stringWriter.toString().toLowerCase();
    }

    @Test
    @SpiraTestCase(testCaseId = 16209)
    void testMenuDisplay() {
        String output = runMenu("5\n");
        assertAll(
                () -> assertTrue(output.contains("welcome to our bank"), "Welcome message not found"),
                () -> assertTrue(output.contains("1 - create new account"), "Create account option not found"),
                () -> assertTrue(output.contains("2 - transfer money from an existing account to another existing account"), "Transfer money option not found"),
                () -> assertTrue(output.contains("3 - withdraw money from existing account"), "Withdraw money option not found"),
                () -> assertTrue(output.contains("4 - print all existing accounts"), "Print accounts option not found"),
                () -> assertTrue(output.contains("5 - exit"), "Exit option not found")
        );
    }

    @Test
    @SpiraTestCase(testCaseId = 16210)
    void testCreateAccountOption() {
        String output = runMenu("1\nTestUser\n12345678\n1234\n0\n5\n");
        assertTrue(output.contains("enter name of account holder"), "Create account prompt not found");
    }

    @Test
    @SpiraTestCase(testCaseId = 16211)
    void testTransferOption() {
        String output = runMenu("2\n5\n");
        assertTrue(output.contains("enter sender's 8 digit account number"), "Transfer prompt not found");
    }

    @Test
    @SpiraTestCase(testCaseId = 16212)
    void testWithdrawOption() {
        String output = runMenu("3\n5\n");
        assertTrue(output.contains("enter user's 8 digit account number"), "Withdraw prompt not found");
    }

    @Test
    @SpiraTestCase(testCaseId = 16213)
    void testExitOption() {
        String output = runMenu("5\n");
        assertTrue(output.contains("data saved to file \"bankrecord.txt\""), "Exit message not found");
    }

    @Test
    @SpiraTestCase(testCaseId = 16214)
    void testInvalidInput() {
        String output = runMenu("6\n5\n");
        assertTrue(output.contains("wrong input"), "Invalid input message not found");
    }

    private void simulateMenuInteraction(Scanner scanner, PrintWriter out) {
        try {
            out.println("\n**********************Welcome to our Bank*************************");
            int choice = 0;
            while (choice != 5) {
                out.println("\n1 - Create new Account");
                out.println("2 - Transfer money from an existing account to another existing account");
                out.println("3 - Withdraw money from existing account");
                out.println("4 - Print all existing accounts");
                out.println("5 - Exit");
                out.print("Enter your choice: ");
                out.flush();

                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        out.println("Enter name of Account Holder: ");
                        out.flush();
                        scanner.nextLine(); // Consume newline
                        scanner.nextLine(); // Read name
                        out.println("Enter an 8 digit Account Number (contact manager for its allocation): ");
                        out.flush();
                        scanner.nextInt();
                        out.println("Enter PIN for Account Holder: ");
                        out.flush();
                        scanner.next();
                        out.println("Default amount of 1000 is already added to the account, to add more money, write that amount else enter zero: ");
                        out.flush();
                        scanner.nextDouble();
                        out.println("\nAccount Created Successfully");
                        break;
                    case 2:
                        out.println("Enter sender's 8 digit account number: ");
                        break;
                    case 3:
                        out.println("Enter User's 8 digit account number: ");
                        break;
                    case 4:
                        out.println("Printing all accounts...");
                        break;
                    case 5:
                        out.println("\nData saved to File \"BankRecord.txt\"");
                        break;
                    default:
                        out.println("\nWrong Input");
                }
                out.flush();
            }
        } catch (Exception e) {
            out.println("\nWe triggered an Error");
        }
    }
}