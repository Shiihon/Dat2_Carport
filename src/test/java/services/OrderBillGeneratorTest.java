package services;

import app.util.OrderBillGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OrderBillGeneratorTest {

    @Test
    public void calculatePostsQuantityTest460() {
        int expectedPostsQuantity = 4;
        int actualPostsQuantity = OrderBillGenerator.calculatePostsQuantity(460);

        Assertions.assertEquals(expectedPostsQuantity, actualPostsQuantity);
    }

    @Test
    public void calculatePostsQuantityTestAbove460() {
        int expectedPostsQuantity = 6;
        int actualPostsQuantity = OrderBillGenerator.calculatePostsQuantity(480);

        Assertions.assertEquals(expectedPostsQuantity, actualPostsQuantity);
    }

    @Test
    public void calculatePostsQuantityTestMaxLength() {
        int expectedPostsQuantity = 6;
        int actualPostsQuantity = OrderBillGenerator.calculatePostsQuantity(780);

        Assertions.assertEquals(expectedPostsQuantity, actualPostsQuantity);
    }

    @Test
    public void calculateBeamLengthsTest600() {
        List<Double> expectedBeamLengths = List.of(600D);
        List<Double> actualBeamLengths = OrderBillGenerator.calculateBeamLengths(600);

        Assertions.assertEquals(expectedBeamLengths, actualBeamLengths);
    }

    @Test
    public void calculateBeamLengthsTestAbove600() {
        List<Double> expectedBeamLengths = List.of(425D, 355D);
        List<Double> actualBeamLengths = OrderBillGenerator.calculateBeamLengths(780);

        Assertions.assertEquals(expectedBeamLengths, actualBeamLengths);
    }

    @Test
    public void calculateRaftersQuantityTest() {
        int expectedRaftersQuantity = 16;
        int actualRaftersQuantity = OrderBillGenerator.calculateRaftersQuantity(780);

        Assertions.assertEquals(expectedRaftersQuantity, actualRaftersQuantity);
    }
}
