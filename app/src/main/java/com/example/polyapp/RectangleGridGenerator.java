package com.example.polyapp;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class RectangleGridGenerator {

    // Function to generate a rectangular grid (polyline) inside a polygon
    public static List<LatLng> generateRectangleGridInsidePolygon(List<LatLng> polygonPoints, double stepSize) {
        List<LatLng> gridPoints = new ArrayList<>();

                if (polygonPoints.size() < 3) return gridPoints;

                // Step 1: Get bounding box of the polygon
                double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
                double minLng = Double.MAX_VALUE, maxLng = Double.MIN_VALUE;

                for (LatLng point : polygonPoints) {
                    if (point.latitude < minLat) minLat = point.latitude;
                    if (point.latitude > maxLat) maxLat = point.latitude;
                    if (point.longitude < minLng) minLng = point.longitude;
                    if (point.longitude > maxLng) maxLng = point.longitude;
                }

                // Ensure the step size is positive to avoid infinite loops
                if (stepSize <= 0) stepSize = 0.001; // Default step size if invalid value is provided

                // Step 2: Generate grid lines (both horizontal and vertical) within the bounding box

                // Generate Vertical lines (Longitude constant, Latitude varies)
                for (double lng = minLng; lng <= maxLng; lng += stepSize) {
                    for (double lat = minLat; lat <= maxLat; lat += stepSize) {
                        LatLng gridPoint = new LatLng(lat, lng);
                        // Check if this grid point is inside the polygon using Ray-Casting algorithm
                        if (PolygonUtils.isPointInsidePolygon(polygonPoints, gridPoint)) {
                            gridPoints.add(gridPoint);  // Add valid points inside the polygon
                        }
                    }
                }


        return gridPoints; // Return the valid points forming a grid pattern inside the polygon


    }
}
