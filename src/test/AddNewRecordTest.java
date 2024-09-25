package test;

import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;

public class AddNewRecordTest extends test.BaseBankTest {
    @Test
    @SpiraTestCase(testCaseId = 1)
    void testAddNewRecordNormalCase() {
        simulateInput("John Doe\n12345678\n1234\n500\n");
        bank.addNewRecord();
        assertEquals(1, bank.AL.size());
        assertAccountDetails(bank.AL.get(0), "John Doe", 12345678, "1234", 1500);
    }

    @Test
    @SpiraTestCase(testCaseId = 2)
    void testAddNewRecordZeroAdditionalAmount() {
        simulateInput("Jane Doe\n87654321\n4321\n0\n");
        bank.addNewRecord();
        assertEquals(1, bank.AL.size());
        assertAccountDetails(bank.AL.get(0), "Jane Doe", 87654321, "4321", 1000);
    }
}
