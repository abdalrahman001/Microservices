package org.example.inventory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService service;

    @GetMapping("/dishes")
    public List<DishModel> getAllDishes() {
        return service.getAllDishes();
    }

    @GetMapping("/dishes/seller/{sellerId}")
    public List<DishModel> getDishesBySeller(@PathVariable String sellerId) {
        return service.getDishesBySeller(sellerId);
    }

    @GetMapping("/dishes/{id}")
    public DishModel getDishById(@PathVariable int id) {
        return service.getDishById(id);
    }

    @PostMapping("/dishes")
    public void addDish(@RequestBody DishRequest request) {
        service.addDish(
                request.getName(),
                request.getSellerId(),
                request.getDescription(),
                request.getPrice(),
                request.getStock()
        );
    }

    @PutMapping("/dishes/{id}")
    public void updateDish(@PathVariable int id, @RequestBody DishRequest dishRequest) {
        service.updateDish(
            id,
            dishRequest.getSellerId(),
            dishRequest.getName(),
            dishRequest.getDescription(),
            BigDecimal.valueOf(dishRequest.getPrice()),
            dishRequest.getStock()
        );
    }


    // @GetMapping("/stock/check/{dishId}/{quantity}")
    // public boolean checkStock(@PathVariable int dishId, @PathVariable int quantity) {
    //     return service.checkStock(dishId, quantity);
    // }

    @GetMapping("/stock/check/{dishId}/{quantity}")
    public ResponseEntity<Map<String, Object>> checkStock(@PathVariable int dishId, @PathVariable int quantity) {
        // Call the new service method that provides more details
        Map<String, Object> dishDetails = service.checkStockAndGetDetails(dishId, quantity);

        // Build the response explicitly
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("available", dishDetails.get("available"));
        responseBody.put("price", dishDetails.get("price"));
        // You might or might not want to send 'stock' back to the OrderService,
        // but it's good to have it in dishDetails for internal logic.

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/stock/reduce/{dishId}/{quantity}")
    public void reduceStock(@PathVariable int dishId, @PathVariable int quantity) {
        service.reduceStock(dishId, quantity);
    }


    @PostMapping("/stock/increase/{dishId}/{quantity}")
    public void increaseStock(@PathVariable int dishId, @PathVariable int quantity) {
        System.out.println("INCREASE STOCK CALLED: dishId=" + dishId + ", quantity=" + quantity);
        service.increaseStock(dishId, quantity);
    }


    @GetMapping("/stock/increase/{dishId}/{quantity}")
    public String testIncrease(@PathVariable int dishId, @PathVariable int quantity) {
        return "Increase stock called for dishId=" + dishId + " quantity=" + quantity;
    }


    @GetMapping("/dishes/available")
    public List<DishModel> listAvailableDishes() {
        return service.listAvailableDishes();
    }

    @GetMapping("/previously-sold/{customerId}")
    public List<SoldDishModel> getPreviouslySoldDishes(@PathVariable String customerId) {
        return service.listPreviouslySoldDishes(customerId);
    }

}
