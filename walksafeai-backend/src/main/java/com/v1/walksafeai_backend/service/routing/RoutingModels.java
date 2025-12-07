package com.v1.walksafeai_backend.service.routing;

import lombok.*;

import java.util.List;

public class RoutingModels {

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RouteResult {
        private String providerRouteId;
        private double distanceMeters;
        private double durationSeconds;
        // Line as list of [lng, lat]
        private List<double[]> geometry;
    }
}