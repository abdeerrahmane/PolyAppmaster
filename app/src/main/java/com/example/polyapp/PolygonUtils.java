package com.example.polyapp;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class PolygonUtils {

    public static boolean isPointInsidePolygon(List<LatLng> polygon, LatLng point) {
        int n = polygon.size();
        if (n < 3) return false;

        boolean result = false;
        int j = n - 1;

        for (int i = 0; i < n; i++) {
            LatLng vertex1 = polygon.get(i);
            LatLng vertex2 = polygon.get(j);

            if ((vertex1.latitude > point.latitude) != (vertex2.latitude > point.latitude) &&
                    (point.longitude < (vertex2.longitude - vertex1.longitude) * (point.latitude - vertex1.latitude) / (vertex2.latitude - vertex1.latitude) + vertex1.longitude)) {
                result = !result;
            }
            j = i;
        }

        return result;
    }
}
