import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpiraTestConfiguration(
        url = "https://rmit.spiraservice.net/",
        login = "s3952320",
        projectId = 258,
        rssToken = "{332767F9-C19C-42B0-BE3C-3EC80E04AC56}",
        releaseId = 1453,
        testSetId = 2509
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
        System.setProperty("user.dir", tempDir.toString());
    }

    private void createTestFile(Account... accounts) {
        Bank testBank = new Bank();
        for (Account account : accounts) {
            testBank.AL.add(account);
        }
        testBank.save();
    }

    @Test
    @SpiraTestCase(testCaseId = 16243)
    public void testNormalLoad() {
        createTestFile(
                new Account("Oisin Aeonn", 39523200, "2809", 3000),
                new Account("Hoshi Aeonn", 39523201, "1234", 2000)
        );

        bank.load();

        assertEquals(2, bank.AL.size(), "Two accounts should be loaded");
        assertEquals("Oisin Aeonn", bank.AL.get(0).getName(), "First account should be Oisin Aeonn");
        assertEquals("Hoshi Aeonn", bank.AL.get(1).getName(), "Second account should be Hoshi Aeonn");
    }

    @Test
    @SpiraTestCase(testCaseId = 16244)
    public void testLoadCorruptedFile() throws IOException {
        File file = new File("BankRecord.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("This is not valid serialized data".getBytes());
        }

        bank.load();

        assertEquals(0, bank.AL.size(), "No accounts should be loaded from corrupted file");
    }

    @Test
    @SpiraTestCase(testCaseId = 16245)
    public void testLoadNonExistentFile() {
        File file = new File("BankRecord.txt");
        file.delete();

        bank.load();

        assertEquals(0, bank.AL.size(), "No accounts should be loaded when file doesn't exist");
    }

    @Test
    @SpiraTestCase(testCaseId = 16246)
    public void testLoadLargeNumberOfAccounts() {
        Bank testBank = new Bank();
        for (int i = 0; i < 1000; i++) {
            testBank.AL.add(new Account("Test" + i, 10000000 + i, "1234", 1000));
        }
        testBank.save();

        bank.load();

        assertEquals(1000, bank.AL.size(), "All 1000 accounts should be loaded");
        assertEquals("Test0", bank.AL.get(0).getName(), "First account should be Test0");
        assertEquals("Test999", bank.AL.get(999).getName(), "Last account should be Test999");
    }
}