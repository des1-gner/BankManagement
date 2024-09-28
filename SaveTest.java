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
        projectId = 258
        // rssToken = "{D3F132C0-B1C4-4F28-A4CC-246934D58A4A}",
        // releaseId = 0,
        // testSetId = 0
)
class SaveTest {

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

    @Test
    @SpiraTestCase(testCaseId = 16203)
    public void testSaveNormalCase() {
        bank.AL.add(new Account("Oisin Aeonn", 39523200, "2809", 3000));
        bank.AL.add(new Account("Hoshi Aeonn", 39523201, "1234", 2000));

        bank.save();

        Bank loadedBank = new Bank();
        loadedBank.load();

        assertEquals(2, loadedBank.AL.size(), "Two accounts should be saved and loaded");
        assertEquals("Oisin Aeonn", loadedBank.AL.get(0).getName(), "First account should be Oisin Aeonn");
        assertEquals("Hoshi Aeonn", loadedBank.AL.get(1).getName(), "Second account should be Hoshi Aeonn");
    }

    @Test
    @SpiraTestCase(testCaseId = 16204)
    public void testSaveEmptyBank() {
        bank.save();

        Bank loadedBank = new Bank();
        loadedBank.load();

        assertEquals(0, loadedBank.AL.size(), "No accounts should be saved or loaded");
    }

    @Test
    @SpiraTestCase(testCaseId = 16205)
    public void testSaveLargeNumberOfAccounts() {
        for (int i = 0; i < 1000; i++) {
            bank.AL.add(new Account("Test" + i, 10000000 + i, "1234", 1000));
        }

        bank.save();

        Bank loadedBank = new Bank();
        loadedBank.load();

        assertEquals(1000, loadedBank.AL.size(), "All 1000 accounts should be saved and loaded");
        assertEquals("Test0", loadedBank.AL.get(0).getName(), "First account should be Test0");
        assertEquals("Test999", loadedBank.AL.get(999).getName(), "Last account should be Test999");
    }

    @Test
    @SpiraTestCase(testCaseId = 16206)
    public void testSaveWithSpecialCharacters() {
        bank.AL.add(new Account("Oisin O'Aeonn", 39523200, "2809", 3000));
        bank.AL.add(new Account("Elani Shannon-Puszka", 41082220, "5678", 1500));

        bank.save();

        Bank loadedBank = new Bank();
        loadedBank.load();

        assertEquals(2, loadedBank.AL.size(), "Two accounts should be saved and loaded");
        assertEquals("Oisin O'Aeonn", loadedBank.AL.get(0).getName(), "First account name should include apostrophe");
        assertEquals("Elani Shannon-Puszka", loadedBank.AL.get(1).getName(), "Second account name should include hyphen");
    }

    @Test
    @SpiraTestCase(testCaseId = 16207)
    public void testSaveAndOverwrite() {
        bank.AL.add(new Account("Oisin Aeonn", 39523200, "2809", 3000));
        bank.save();

        bank = new Bank(); // Create a new bank
        bank.AL.add(new Account("Hoshi Aeonn", 39523201, "1234", 2000));
        bank.save(); // This should overwrite the previous file

        Bank loadedBank = new Bank();
        loadedBank.load();

        assertEquals(1, loadedBank.AL.size(), "Only one account should be saved and loaded after overwrite");
        assertEquals("Hoshi Aeonn", loadedBank.AL.get(0).getName(), "The loaded account should be Hoshi Aeonn");
    }
}