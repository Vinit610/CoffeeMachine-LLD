package tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import inventory.InventoryManager;
import model.Beverage;
import model.CoffeeMachineDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**Represents a physical Coffee Machine, which can serve PARALLELY, using multi threading.
 * Singleton Class to simulate a CoffeeMachine
 * Supports adding beverage requests, with a maximum pending queue size = MAX_QUEUED_REQUEST*/

public class CoffeeMachine {
    private static final Logger logger = LoggerFactory.getLogger(CoffeeMachine.class);

    private static CoffeeMachine coffeeMachine;
    public CoffeeMachineDetails coffeeMachineDetails;
    public InventoryManager inventoryManager;
    private static final int MAX_QUEUED_REQUEST = 100;
    private ThreadPoolExecutor executor;

    /**
     * creates the singleton Object for CoffeeMachine
     * @return
     * @throws IOException
     */
    public static CoffeeMachine getInstance(final String jsonInput) throws IOException {
        if (coffeeMachine == null) {
            coffeeMachine = new CoffeeMachine(jsonInput);
        }
        return coffeeMachine;
    }

    private CoffeeMachine(String jsonInput) throws IOException {
        this.coffeeMachineDetails = new ObjectMapper().readValue(jsonInput, CoffeeMachineDetails.class);
        int outlet = coffeeMachineDetails.getMachine().getOutlets().getCount();
        executor = new ThreadPoolExecutor(outlet, outlet, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(MAX_QUEUED_REQUEST));
        executor.setRejectedExecutionHandler(new RejectedTaskHandler());
    }

    /**
    * CoffeeMachine assigns the task to BeverageMaker
     */
    public void process() {
        this.inventoryManager = InventoryManager.getInstance();

        Map<String, Integer> ingredients = coffeeMachineDetails.getMachine().getIngredientQuantityMap();

        for (String key : ingredients.keySet()) {
            inventoryManager.addInventory(key, ingredients.get(key));
        }

        HashMap<String, HashMap<String, Integer>> beverages = coffeeMachineDetails.getMachine().getBeverages();
        for (String key : beverages.keySet()) {
            Beverage beverage = new Beverage(key, beverages.get(key));
            coffeeMachine.addBeverageRequest(beverage);
        }
    }

    public void addBeverageRequest(Beverage beverage) {
        BeverageMakerTask task = new BeverageMakerTask(beverage);
        executor.execute(task);
    }

    public void stopMachine() {
        executor.shutdown();
    }

    public void reset() {
        logger.info("Resetting");
        this.stopMachine();
        this.inventoryManager.resetInventory();
    }
}
