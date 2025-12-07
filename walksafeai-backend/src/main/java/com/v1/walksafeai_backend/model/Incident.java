package com.v1.walksafeai_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.Instant;

@Document("incidents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Incident {
    @Id
    private String id;

    private String category;   // harassment, theft, assault, etc.
    private Integer severity;  // 1-5 optional
    private Instant occurredAt;

    @GeoSpatialIndexed(type = org.springframework.data.mongodb.core.index.GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    private String source;
}
