package app.persistence;

import app.entities.Material;
import app.entities.Order;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class OrderMapperTest {

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();

    @BeforeAll
    public static void setupTables() {
        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Drop existing test tables
                stmt.execute("DROP TABLE IF EXISTS test.materials CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.material_variants CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.orders CASCADE");
                stmt.execute("DROP TABLE IF EXISTS test.order_bills CASCADE");

                // Drop existing sequences
                stmt.execute("DROP SEQUENCE IF EXISTS test.materials_material_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.material_variants_material_variant_id_seq CASCADE;");
                stmt.execute("DROP SEQUENCE IF EXISTS test.orders_order_id_seq CASCADE;");

                // Create tables from the public schema
                stmt.execute("CREATE TABLE test.materials AS (SELECT * from public.materials) WITH NO DATA");
                stmt.execute("CREATE TABLE test.material_variants AS (SELECT * from public.material_variants) WITH NO DATA");
                stmt.execute("CREATE TABLE test.orders AS (SELECT * from public.orders) WITH NO DATA");
                stmt.execute("CREATE TABLE test.order_bills AS (SELECT * from public.order_bills) WITH NO DATA");

                // Create table sequences
                stmt.execute("CREATE SEQUENCE test.materials_material_id_seq");
                stmt.execute("ALTER TABLE test.materials ALTER COLUMN material_id SET DEFAULT nextval('test.materials_material_id_seq')");

                stmt.execute("CREATE SEQUENCE test.material_variants_material_variant_id_seq");
                stmt.execute("ALTER TABLE test.material_variants ALTER COLUMN material_variant_id SET DEFAULT nextval('test.material_variants_material_variant_id_seq')");

                stmt.execute("CREATE SEQUENCE test.orders_order_id_seq");
                stmt.execute("ALTER TABLE test.orders ALTER COLUMN order_id SET DEFAULT nextval('test.orders_order_id_seq')");
            } catch (SQLException e) {
                Assertions.fail("SQL Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            Assertions.fail("Failed to connect to the database.");
        }
    }

    private final List<Material> expectedMaterials = new ArrayList<>();
    private final List<Order> expectedOrders = new ArrayList<>();

    @BeforeEach
    public void setupData() {
        expectedMaterials.clear();
        expectedOrders.clear();

        // Create materials
        for (int i = 0; i < 6; i++) {
            expectedMaterials.add(
                    new Material(
                            1,
                            i + 1,
                            "45x195 mm. spærtræ ubh.",
                            "Stk",
                            32,
                            300 + i * 60,
                            Material.MaterialType.WOOD
                    )
            );
        }

        expectedMaterials.add(
                new Material(
                        2,
                        7,
                        "97x97 mm. trykimp. Stolpe",
                        "Stk",
                        36,
                        300,
                        Material.MaterialType.WOOD
                )
        );

        // Create orders
        expectedOrders.add(new Order(
                1,
                1,
                "Carport med flat tag: bredde=6,0m & længde=7,8m",
                Order.OrderStatus.WAITING_FOR_REVIEW,
                0,
                List.of(new OrderBillItem(
                                expectedMaterials.get(6),
                                "Stolper nedgraves 90 cm. i jord",
                                6
                        ),
                        new OrderBillItem(
                                expectedMaterials.get(3),
                                "Remme i sider, sadles ned i stolper",
                                2
                        ),
                        new OrderBillItem(
                                expectedMaterials.get(1),
                                "Remme i sider, sadles ned i stolper",
                                2
                        ),
                        new OrderBillItem(
                                expectedMaterials.get(5),
                                "Spær, monteres på rem",
                                16
                        )),
                LocalDateTime.of(2024, 5, 10, 11, 55)
        ));
        expectedOrders.add(new Order(
                2,
                2,
                "Carport med flat tag: bredde=2,4m & længde=2,4m",
                Order.OrderStatus.PAID,
                32800,
                List.of(new OrderBillItem(
                                expectedMaterials.get(6),
                                "Stolper nedgraves 90 cm. i jord",
                                4
                        ),
                        new OrderBillItem(
                                expectedMaterials.get(0),
                                "Remme i sider, sadles ned i stolper",
                                2
                        ),
                        new OrderBillItem(
                                expectedMaterials.get(0),
                                "Spær, monteres på rem",
                                6
                        )),
                LocalDateTime.of(2024, 5, 8, 14, 27)
        ));

        try (Connection connection = connectionPool.getConnection()) {
            try (Statement stmt = connection.createStatement()) {
                // Drop existing test tables
                stmt.execute("DELETE FROM test.materials CASCADE");
                stmt.execute("DELETE FROM test.material_variants CASCADE");
                stmt.execute("DELETE FROM test.orders CASCADE");

                // Insert materials
                stmt.execute("SELECT setval('materials_material_id_seq', 1)");

                String materialSql = "INSERT INTO test.materials (material_id, material_description, material_unit, material_price, material_type)  VALUES" + Stream.of(expectedMaterials.get(0), expectedMaterials.get(6))
                        .map(material -> String.format(
                                        "(%d, '%s', '%s', %d, '%s')",
                                        material.getMaterialId(),
                                        material.getDescription(),
                                        material.getUnit(),
                                        material.getPrice(),
                                        material.getMaterialType()
                                )
                        ).collect(Collectors.joining(", "));

                stmt.execute(materialSql);
                stmt.execute("SELECT setval('materials_material_id_seq', COALESCE((SELECT MAX(material_id)+1 FROM materials), 1), false)");

                // Insert material variants
                stmt.execute("SELECT setval('material_variants_material_variant_id_seq', 1)");

                String materialVariantSql = "INSERT INTO test.material_variants (material_variant_id, material_id, length)  VALUES" + expectedMaterials.stream()
                        .map(material -> String.format(
                                        "(%d, %d, %d)",
                                        material.getMaterialVariantId(),
                                        material.getMaterialId(),
                                        material.getLength()
                                )
                        ).collect(Collectors.joining(", "));

                stmt.execute(materialVariantSql);

                // Insert orders
                stmt.execute("SELECT setval('orders_order_id_seq', 1)");
                stmt.execute("SELECT setval('material_variants_material_variant_id_seq', COALESCE((SELECT MAX(material_variant_id)+1 FROM material_variants), 1), false)");

                String orderSql = "INSERT INTO test.orders (order_id, account_id, order_title, order_status, order_total_price, order_timestamp) VALUES" + expectedOrders.stream()
                        .map(order -> String.format(
                                        "(%d, %d, '%s', '%s', %d, '%s')",
                                        order.getOrderId(),
                                        order.getAccountId(),
                                        order.getTitle(),
                                        order.getStatus(),
                                        order.getTotalPrice(),
                                        order.getTimestamp()
                                )
                        ).collect(Collectors.joining(", "));

                stmt.execute(orderSql);
                stmt.execute("SELECT setval('material_variants_material_variant_id_seq', COALESCE((SELECT MAX(order_id)+1 FROM orders), 1), false)");

                // Insert order bills
                for (Order order : expectedOrders) {
                    String orderBillSql = "INSERT INTO test.order_bills (order_id, material_variant_id, item_description, item_quantity) VALUES" + order.getOrderBill().stream()
                            .map(orderBill -> String.format(
                                    "(%d, %d, '%s', %d)",
                                    order.getOrderId(),
                                    orderBill.getMaterial().getMaterialVariantId(),
                                    orderBill.getDescription(),
                                    orderBill.getQuantity()
                            )).collect(Collectors.joining(", "));

                    stmt.execute(orderBillSql);
                }
            } catch (SQLException e) {
                Assertions.fail("SQL Error" + e.getMessage());
            }
        } catch (SQLException e) {
            Assertions.fail("Failed to connect to the database.");
        }
    }

    @Test
    void getAllOrdersByStatusTest() {
        try {
            List<Order> expectedStatusOrders = List.of(expectedOrders.get(0));
            List<Order> actualStatusOrders = OrderMapper.getAllOrdersByStatus(Order.OrderStatus.WAITING_FOR_REVIEW, connectionPool);

            Assertions.assertEquals(expectedStatusOrders, actualStatusOrders);
        } catch (DatabaseException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    void setOrderStatusTest() {
        try {
            Order order = expectedOrders.get(0);
            order.setStatus(Order.OrderStatus.PAID);

            OrderMapper.setOrderStatus(order.getOrderId(), order.getStatus(), connectionPool);

            List<Order> expectedStatusOrders = List.of(expectedOrders.get(1), expectedOrders.get(0));
            List<Order> actualStatusOrders = OrderMapper.getAllOrdersByStatus(Order.OrderStatus.PAID, connectionPool);

            Assertions.assertEquals(true, expectedStatusOrders.containsAll(actualStatusOrders));
        } catch (DatabaseException e) {
            Assertions.fail(e.getMessage());
        }
    }
}