package com.v1.walksafeai_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Document("publicSafetyFacilities")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SafetyFacility {
    @Id
    private String id;

    private String name;
    private String type; // hospital, fire_station, metro_station, 24x7_pharmacy, etc.

    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String source;
}
