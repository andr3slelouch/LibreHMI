package andrade.luis.hmiethernetip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class StringCalculatorTest {
    private StringCalculator myCalculator = new StringCalculator();

    @Test
    public void addTwoPositiveNumbers(){
        Assertions.assertEquals(5,myCalculator.add(2,3));
    }
    @Test
    public void addTwoNegativeNumbers()
    {
        Assertions.assertEquals(-5,myCalculator.add(-2,-3));
    }

    @Test
    void add() {
    }
}
