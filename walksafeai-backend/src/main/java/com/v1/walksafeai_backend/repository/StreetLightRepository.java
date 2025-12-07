package com.v1.walksafeai_backend.repository;


import com.v1.walksafeai_backend.model.StreetLight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StreetLightRepository extends MongoRepository<StreetLight, String> {

    @Query("{ location: { $near: { $geometry: ?0, $maxDistance: ?1 } } }")
    List<StreetLight> findNear(Object geoJsonPoint, double maxDistanceMeters);
}
