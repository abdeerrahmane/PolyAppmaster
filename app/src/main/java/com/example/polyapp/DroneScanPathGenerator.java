package com.example.polyapp;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class DroneScanPathGenerator {

    // Function to generate a drone scanning path (lawnmower pattern) inside a polygon
    public static List<LatLng> generateDroneScanPathInsidePolygon(List<LatLng> polygonPoints, double stepSize) {
        List<LatLng> pathPoints = new ArrayList<>();

        if (polygonPoints.size() < 3) return pathPoints;

        // Step 1: Get the bounding box of the polygon
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE, maxLng = Double.MIN_VALUE;

        for (LatLng point : polygonPoints) {
            if (point.latitude < minLat) minLat = point.latitude;
            if (point.latitude > maxLat) maxLat = point.latitude;
            if (point.longitude < minLng) minLng = point.longitude;
            if (point.longitude > maxLng) maxLng = point.longitude;
        }

        // Step 2: Generate the path using a zigzag/lawnmower pattern
        boolean moveRight = true;  // Boolean to control the direction of zigzag (left to right or right to left)

        for (double lat = minLat; lat <= maxLat; lat += stepSize) {
            List<LatLng> linePoints = new ArrayList<>();

            // Generate points for a line (horizontal line moving left to right or right to left)
            if (moveRight) {
                for (double lng = minLng; lng <= maxLng; lng += stepSize) {
                    LatLng pathPoint = new LatLng(lat, lng);
                    if (PolygonUtils.isPointInsidePolygon(polygonPoints, pathPoint)) {
                        linePoints.add(pathPoint);
                    }
                }
            } else {
                for (double lng = maxLng; lng >= minLng; lng -= stepSize) {
                    LatLng pathPoint = new LatLng(lat, lng);
                    if (PolygonUtils.isPointInsidePolygon(polygonPoints, pathPoint)) {
                        linePoints.add(pathPoint);
                    }
                }
            }

            // Add the line points to the final path
            if (!linePoints.isEmpty()) {
                pathPoints.addAll(linePoints);
            }

            // Alternate direction for next line (right to left or left to right)
            moveRight = !moveRight;
        }

        return pathPoints;  // Return the complete path points inside the polygon
    }
}
