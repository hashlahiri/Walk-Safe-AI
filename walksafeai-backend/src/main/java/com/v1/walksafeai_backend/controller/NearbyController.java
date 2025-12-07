package com.v1.walksafeai_backend.controller;

import com.v1.walksafeai_backend.model.PoliceStation;
import com.v1.walksafeai_backend.model.SafetyFacility;
import com.v1.walksafeai_backend.service.NearbySafetyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nearby")
@RequiredArgsConstructor
public class NearbyController {

    @Autowired
    private NearbySafetyService nearbyService;

    @GetMapping("/police")
    public List<PoliceStation> police(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "1200") double radiusMeters) {
        return nearbyService.nearbyPolice(lng, lat, radiusMeters);
    }

    @GetMapping("/facilities")
    public List<SafetyFacility> facilities(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam(defaultValue = "1200") double radiusMeters) {
        return nearbyService.nearbyFacilities(lng, lat, radiusMeters);
    }
}

