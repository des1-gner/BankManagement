import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258
)
class LoadTest {

    private Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    @TempDir
    Path tempDir;

    @BeforeEach
    public void setup() {
        bank = new Bank();
        System.setOut(new PrintStream(outContent));
    }

    private void createTestFile(String fileName, Account... accounts) throws IOException {
        File file = tempDir.resolve(fileName).toFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (Account account : accounts) {
                oos.writeObject(account);
            }
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 16198)
    public void testNormalLoad() throws IOException {
        createTestFile("BankRecord.txt",
                new Account("Oisin Aeonn", 39523200, "2809", 3000),
                new Account("Hoshi Aeonn", 39523201, "1234", 2000));

        bank.load();

        assertEquals(2, bank.AL.size());
        assertEquals("Oisin Aeonn", bank.AL.get(0).getName());
        assertEquals("Hoshi Aeonn", bank.AL.get(1).getName());
    }

    @Test
    @SpiraTestCase(testCaseId = 16199)
    public void testLoadCorruptedFile() throws IOException {
        File file = tempDir.resolve("BankRecord.txt").toFile();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("This is not valid serialized data");
        }

        bank.load();

        assertEquals(0, bank.AL.size());
        assertTrue(outContent.toString().contains("Error loading data from file"));
    }

    @Test
    @SpiraTestCase(testCaseId = 16200)
    public void testLoadNonExistentFile() {
        // Ensure the file doesn't exist
        File file = tempDir.resolve("BankRecord.txt").toFile();
        file.delete();

        bank.load();

        assertEquals(0, bank.AL.size());
        assertTrue(outContent.toString().contains("Error loading data from file"));
    }

    @Test
    @SpiraTestCase(testCaseId = 16201)
    public void testLoadLargeNumberOfAccounts() throws IOException {
        File file = tempDir.resolve("BankRecord.txt").toFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < 1000; i++) {
                oos.writeObject(new Account("Test" + i, 10000000 + i, "1234", 1000));
            }
        }

        bank.load();

        assertEquals(1000, bank.AL.size());
        assertEquals("Test0", bank.AL.get(0).getName());
        assertEquals("Test999", bank.AL.get(999).getName());
    }

    @Test
    @SpiraTestCase(testCaseId = 16202)
    public void testLoadWithMixedValidAndInvalidData() throws IOException {
        File file = tempDir.resolve("BankRecord.txt").toFile();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new Account("Valid1", 10000001, "1234", 1000));
            oos.writeObject("This is not an Account object");
            oos.writeObject(new Account("Valid2", 10000002, "5678", 2000));
        }

        bank.load();

        assertEquals(2, bank.AL.size());
        assertEquals("Valid1", bank.AL.get(0).getName());
        assertEquals("Valid2", bank.AL.get(1).getName());
    }
}