package org.example.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
    public class InventoryService {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<DishModel> mapper = new RowMapper<>() {
        @Override
        public DishModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new DishModel(
                    rs.getInt("id"),
                    rs.getString("seller_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("stock"),
                    rs.getTimestamp("created_at")
            );
        }
    };

    public List<DishModel> getAllDishes() {
        return jdbc.query("SELECT * FROM dishes", mapper);
    }

    public List<DishModel> getDishesBySeller(String sellerId) {
        return jdbc.query("SELECT * FROM dishes WHERE seller_id = ?", mapper, sellerId);
    }

    public DishModel getDishById(int id) {
        return jdbc.queryForObject("SELECT * FROM dishes WHERE id = ?", mapper, id);
    }

    public void addDish(  String name, String seller_id,String description, double price, int stock) {
        jdbc.update(
                "INSERT INTO dishes (  name, seller_id, description, price, stock) VALUES ( ?, ?, ?, ?, ?)",
                name, seller_id, description, price, stock
        );
    }

    public void updateDish(int id, String seller_id, String name, String description, BigDecimal price, int stock) {
        jdbc.update(
            "UPDATE dishes SET seller_id = ?, name = ?, description = ?, price = ?, stock = ? WHERE id = ?",
            seller_id, name, description, price, stock, id
        );
    }


    // public boolean checkStock(int dishId, int quantity) {
    //     Integer stock = jdbc.queryForObject("SELECT stock FROM dishes WHERE id = ?", Integer.class, dishId);
    //     return stock != null && stock >= quantity;
    // }

    public Map<String, Object> checkStockAndGetDetails(int dishId, int requestedQuantity) {
        String sql = "SELECT stock, price FROM dishes WHERE id = ?";
        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> dishData = jdbc.queryForMap(sql, dishId);

            Integer currentStock = (Integer) dishData.get("stock");
            Double price = (Double) dishData.get("price");
            System.err.println("--------------> stock " + currentStock );
            System.err.println("--------------> price" + price );


            // Determine 'available' based on stock vs. requested quantity
            // This is the core logic for your "stock >= quantity" check
            boolean isAvailable = (currentStock != null && currentStock >= requestedQuantity);

            result.put("available", isAvailable);
            result.put("price", price);
            result.put("currentStock", currentStock); // Provide actual stock for context
        } catch (EmptyResultDataAccessException e) {
            // Dish not found in the database
            result.put("available", false); // Not available because it doesn't exist
            result.put("price", 0.0);
            result.put("currentStock", 0);
        } catch (Exception e) {
            // Catch any other potential database or type casting errors
            System.err.println("Error fetching dish details for ID " + dishId + ": " + e.getMessage());
            result.put("available", false);
            result.put("price", 0.0);
            result.put("currentStock", 0);
            // Optionally, re-throw or log this more severely if it's an unrecoverable internal error
        }
        return result;
    }
    // You might still want a simple checkStock if other parts of your app only need true/false
    public boolean checkStock(int dishId, int quantity) {
        Map<String, Object> details = checkStockAndGetDetails(dishId, quantity);
        return (boolean) details.getOrDefault("available", false);
    }

    public void reduceStock(int dishId, int quantity) {
        jdbc.update("UPDATE dishes SET stock = stock - ? WHERE id = ?", quantity, dishId);
    }
    public void increaseStock(int dishId, int quantity) {
        jdbc.update("UPDATE dishes SET stock = stock + ? WHERE id = ?", quantity, dishId);
    }
    

    public List<DishModel> listAvailableDishes() {
        String sql = "SELECT * FROM dishes WHERE stock > 0";
        return jdbc.query(sql, mapper);
    }

    private static final String SHIPPING_COMPANY_NAME = "FEDEX"; 

    private final RowMapper<SoldDishModel> soldDishMapper = (rs, rowNum) -> new SoldDishModel(
            rs.getInt("dish_id"),
            rs.getString("dish_name"),
            rs.getInt("quantity"),
            rs.getDouble("sold_price"),
            rs.getTimestamp("order_date"),
            rs.getString("customer_id"),
            rs.getString("shipping_company")
    );

    public List<SoldDishModel> listPreviouslySoldDishes(String customerId) {
        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, oi.quantity, oi.price AS sold_price,
                   o.created_at AS order_date,
                   ? AS customer_id,
                   ? AS shipping_company
            FROM order_items oi
            JOIN dishes d ON oi.dish_id = d.id
            JOIN orders o ON oi.order_id = o.id
            WHERE o.customer_id = ?
            ORDER BY o.created_at DESC
            """;

        return jdbc.query(sql,
                soldDishMapper,
                customerId,
                SHIPPING_COMPANY_NAME,
                customerId);
    }

}
