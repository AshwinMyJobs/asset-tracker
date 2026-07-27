package com.enterprise.asset_service.repository.mongo;

import com.enterprise.asset_service.model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
// MongoRepository<DocumentClass, IDType> provides standard out-of-the-box NoSQL operations
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
