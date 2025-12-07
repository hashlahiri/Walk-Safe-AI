package com.v1.walksafeai_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;

@Document("streetLights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StreetLight {
    @Id
    private String id;

    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String source;
    private Instant lastSeenAt;
}
