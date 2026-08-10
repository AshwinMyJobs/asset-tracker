package com.enterprise.asset_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "compliance_logs") // Maps to a MongoDB document collection grid
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id // MongoDB auto-generates a unique String ID (ObjectId format) if left null
    private String id;

    private Long assetId;
    private String action;      // e.g., "ASSET_CREATED"
    private String details;     // Description text payload

    // 👈 This instructs Spring Data to build a TTL index that purges records older than 30 days
    @Indexed(expireAfter = "30d")
    private LocalDateTime timestamp;
}
