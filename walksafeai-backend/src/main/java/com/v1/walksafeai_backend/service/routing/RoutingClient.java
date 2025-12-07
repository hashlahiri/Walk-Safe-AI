package com.v1.walksafeai_backend.service.routing;

import java.util.List;

public interface RoutingClient {
    RoutingModels.RouteResult getShortest(double[] originLngLat, double[] destLngLat);
    List<RoutingModels.RouteResult> getAlternatives(double[] originLngLat, double[] destLngLat);
}

