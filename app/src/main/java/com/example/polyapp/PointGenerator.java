package com.example.polyapp;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PointGenerator {

    public static List<LatLng> generatePointsInsidePolygon(List<LatLng> polygonPoints, int numberOfPoints) {
        List<LatLng> points = new ArrayList<>();
        if (polygonPoints.size() < 3) return points;

        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE, maxLng = Double.MIN_VALUE;

        // Calculate bounding box
        for (LatLng point : polygonPoints) {
            if (point.latitude < minLat) minLat = point.latitude;
            if (point.latitude > maxLat) maxLat = point.latitude;
            if (point.longitude < minLng) minLng = point.longitude;
            if (point.longitude > maxLng) maxLng = point.longitude;
        }

        Random random = new Random();
        while (points.size() < numberOfPoints) {
            LatLng randomPoint = new LatLng(
                    minLat + (maxLat - minLat) * random.nextDouble(),
                    minLng + (maxLng - minLng) * random.nextDouble()
            );
            if (PolygonUtils.isPointInsidePolygon(polygonPoints, randomPoint)) {
                points.add(randomPoint);
            }
        }
        return points;
    }

}
