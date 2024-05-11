package app.services;

import app.entities.Material;
import app.entities.OrderBillItem;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.MaterialMapper;

import java.util.ArrayList;
import java.util.List;

public class OrderBillGenerator {

    private static final double postDistanceFromFront = 100; // The distance from the front of the carport to the first post
    private static final double postDistanceFromBack = 30; // The distance from the back of the carport to the last post
    private static final double maxPostDistance = 330; // The max distance there can be between two posts

    private static final double maxRafterDistance = 55; // The max distance there can be between two rafters
    private static final double rafterWidth = 4.5; // The width (slim side) of the rafter

    public static List<OrderBillItem> generateOrderBill(int width, int length, ConnectionPool connectionPool) throws DatabaseException {
        List<OrderBillItem> orderBillItems = new ArrayList<>();

        orderBillItems.add(calculatePosts(length, connectionPool));
        orderBillItems.addAll(calculateBeams(length, connectionPool));
        orderBillItems.add(calculateRafters(width, length, connectionPool));

        return orderBillItems;
    }

    private static OrderBillItem calculatePosts(int length, ConnectionPool connectionPool) throws DatabaseException {
        Material material = MaterialMapper.getMaterial("97x97 mm. trykimp. Stolpe", 300, connectionPool);
        int quantity = calculatePostsQuantity(length);

        return new OrderBillItem(material, "Stolper nedgraves 90 cm. i jord", quantity);
    }

    public static int calculatePostsQuantity(int length) {
        double totalPostDistance = length - postDistanceFromFront - postDistanceFromBack;

        return (int) Math.max(Math.ceil(totalPostDistance / maxPostDistance) + 1, 2) * 2;
    }

    private static List<OrderBillItem> calculateBeams(int length, ConnectionPool connectionPool) throws DatabaseException {
        List<Material> materials = MaterialMapper.getAllMaterials("45x195 mm. spærtræ ubh.", connectionPool);
        List<OrderBillItem> orderBillItems = new ArrayList<>();

        for (double beamLength : calculateBeamLengths(length)) {
            orderBillItems.add(new OrderBillItem(getBestMaterialMatch(materials, beamLength), "Remme i sider, sadles ned i stolper", 2));
        }

        return orderBillItems;
    }

    public static List<Double> calculateBeamLengths(int length) {
        double totalPostDistance = length - postDistanceFromFront - postDistanceFromBack;
        int numPosts = (int) Math.max(Math.ceil(totalPostDistance / maxPostDistance) + 1, 2);
        double distanceBetweenPosts = totalPostDistance / (numPosts - 1);
        double x = postDistanceFromFront;

        List<Double> lengths = new ArrayList<>();

        for (int i = 1; i < numPosts; i++) {
            if (x + distanceBetweenPosts <= 660) {
                x += distanceBetweenPosts;
            } else {
                lengths.add(x);
                x = distanceBetweenPosts;
            }
        }

        lengths.add(x + postDistanceFromBack);

        return lengths;
    }

    private static OrderBillItem calculateRafters(int width, int length, ConnectionPool connectionPool) throws DatabaseException {
        List<Material> materials = MaterialMapper.getAllMaterials("45x195 mm. spærtræ ubh.", connectionPool);
        Material bestFitMaterial = getBestMaterialMatch(materials, width);
        int quantity = calculateRaftersQuantity(length);

        return new OrderBillItem(bestFitMaterial, "Spær, monteres på rem", quantity);
    }

    public static int calculateRaftersQuantity(int length) {
        double totalRafterDistance = length - rafterWidth;

        return (int) Math.ceil(totalRafterDistance / maxRafterDistance) + 1;
    }

    public static Material getBestMaterialMatch(List<Material> materials, double length) {
        double smallestDifference = Double.MAX_VALUE;
        Material bestFitMaterial = null;

        for (Material material : materials) {
            // Ensure the length of the material is not smaller than the desired length
            if (material.getLength() >= length) {
                // Calculate the difference between the material length and the desired length
                double diff = Math.abs(length - material.getLength());

                // If the difference is smaller than the current known smallest (smallestDifference) then it is determined to be a better match
                if (diff < smallestDifference) {
                    bestFitMaterial = material;
                    smallestDifference = diff;
                }
            }
        }

        return bestFitMaterial;
    }
}
