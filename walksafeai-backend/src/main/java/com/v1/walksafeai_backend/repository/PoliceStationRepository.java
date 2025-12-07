package com.v1.walksafeai_backend.repository;

import com.v1.walksafeai_backend.model.PoliceStation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliceStationRepository extends MongoRepository<PoliceStation, String> {

    @Query("{ location: { $near: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<PoliceStation> findNear(Object geoJsonPoint, double maxDistanceMeters);
}
