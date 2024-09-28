import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        // rssToken = "{D3F132C0-B1C4-4F28-A4CC-246934D58A4A}",
        projectId = 258
)
class SaveTest {
    Bank bank;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    public void setup() {
        this.bank = new Bank();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    @SpiraTestCase(testCaseId = 16166)
    public void testSaveNormalCase(@TempDir Path tempDir) throws Exception {
        bank.AL.add(new Account("John Doe", 12345678, "1234", 1500));
        bank.AL.add(new Account("Jane Smith", 87654321, "5678", 2000));

        File tempFile = tempDir.resolve("BankRecord.txt").toFile();
        System.setProperty("user.dir", tempDir.toString());

        bank.save();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile))) {
            Account account1 = (Account) in.readObject();
            Account account2 = (Account) in.readObject();

            assertEquals("John Doe", account1.getName());
            assertEquals("Jane Smith", account2.getName());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 16167)
    public void testSaveEmptyBank(@TempDir Path tempDir) throws Exception {
        File tempFile = tempDir.resolve("BankRecord.txt").toFile();
        System.setProperty("user.dir", tempDir.toString());

        bank.save();

        assertTrue(tempFile.exists());
        assertEquals(0, tempFile.length());
    }

    @Test
    @SpiraTestCase(testCaseId = 16168)
    public void testSaveFileAlreadyExists(@TempDir Path tempDir) throws Exception {
        bank.AL.add(new Account("John Doe", 12345678, "1234", 1500));

        File tempFile = tempDir.resolve("BankRecord.txt").toFile();
        tempFile.createNewFile();
        System.setProperty("user.dir", tempDir.toString());

        bank.save();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tempFile))) {
            Account account = (Account) in.readObject();
            assertEquals("John Doe", account.getName());
        }
    }

    @Test
    @SpiraTestCase(testCaseId = 16169)
    public void testSaveIOException(@TempDir Path tempDir) throws Exception {
        bank.AL.add(new Account("John Doe", 12345678, "1234", 1500));

        File tempFile = tempDir.resolve("BankRecord.txt").toFile();
        tempFile.createNewFile();
        tempFile.setWritable(false);
        System.setProperty("user.dir", tempDir.toString());

        bank.save();

        String output = outContent.toString();
        assertTrue(output.contains("Error Saving Data to File"));
    }
}