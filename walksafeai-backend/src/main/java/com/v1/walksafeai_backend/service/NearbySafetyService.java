package com.v1.walksafeai_backend.service;

import com.v1.walksafeai_backend.model.PoliceStation;
import com.v1.walksafeai_backend.model.SafetyFacility;
import com.v1.walksafeai_backend.repository.PoliceStationRepository;
import com.v1.walksafeai_backend.repository.SafetyFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NearbySafetyService {

    private final PoliceStationRepository policeRepo;
    private final SafetyFacilityRepository facilityRepo;

    public List<PoliceStation> nearbyPolice(double lng, double lat, double radiusMeters) {
        GeoJsonPoint p = new GeoJsonPoint(lng, lat);
        return policeRepo.findNear(p, radiusMeters);
    }

    public List<SafetyFacility> nearbyFacilities(double lng, double lat, double radiusMeters) {
        GeoJsonPoint p = new GeoJsonPoint(lng, lat);
        return facilityRepo.findNear(p, radiusMeters);
    }
}

