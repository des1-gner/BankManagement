package src.test;

public class TransferTest extends BaseBankTest {
    @BeforeEach
    @Override
    void setUp() {
        super.setUp();
        bank.AL.add(new Account("Sender", 11111111, "1111", 2000));
        bank.AL.add(new Account("Receiver", 22222222, "2222", 1000));
    }

    @Test
    @SpiraTestCase(testCaseId = 3)
    void testTransferSuccessful() {
        simulateInput("11111111\n1111\n22222222\n500\n");
        bank.transfer();
        assertEquals(1500, bank.AL.get(0).getAmount());
        assertEquals(1500, bank.AL.get(1).getAmount());
    }

    @Test
    @SpiraTestCase(testCaseId = 4)
    void testTransferInsufficientFunds() {
        simulateInput("11111111\n1111\n22222222\n3000\n");
        bank.transfer();
        assertEquals(2000, bank.AL.get(0).getAmount());
        assertEquals(1000, bank.AL.get(1).getAmount());
        assertOutputContains("Sender does not have this much balance in his account");
    }
}
