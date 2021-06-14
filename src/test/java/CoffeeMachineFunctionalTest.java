import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tasks.CoffeeMachine;

import java.io.File;
import java.lang.reflect.Field;

public class CoffeeMachineFunctionalTest {

    CoffeeMachine coffeeMachine;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        coffeeMachine.reset();
        resetSingleton(CoffeeMachine.class, "coffeeMachine");
    }

    /**
     * This class is used to reset the coffeeMachine class for each Test
     * @param clazz
     * @param fieldName
     */
    public static void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Test
    public void test3OutletsValidInput() throws Exception {
        final String filePath = "sampleInput.json";
        File file = new File(CoffeeMachine.class.getClassLoader().getResource(filePath).getFile());
        String jsonInput = FileUtils.readFileToString(file, "UTF-8");
        coffeeMachine = CoffeeMachine.getInstance(jsonInput);
        coffeeMachine.process();
        Assert.assertEquals(4, coffeeMachine.coffeeMachineDetails.getMachine().getBeverages().size());
    }


}