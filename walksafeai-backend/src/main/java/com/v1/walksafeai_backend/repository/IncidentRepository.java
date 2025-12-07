package com.v1.walksafeai_backend.repository;

import com.v1.walksafeai_backend.model.Incident;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface IncidentRepository extends MongoRepository<Incident, String> {

    @Query("{ location: { $near: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<Incident> findNear(Object geoJsonPoint, double maxDistanceMeters);
}
