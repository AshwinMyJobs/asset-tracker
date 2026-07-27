package com.enterprise.asset_service.controller;

import com.enterprise.asset_service.model.Asset;
import com.enterprise.asset_service.model.AuditLog;
import com.enterprise.asset_service.repository.jpa.AssetRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetController {

    private final AssetRepository assetRepository;
    private final MongoTemplate mongoTemplate; // Using explicit template engine

    public AssetController(AssetRepository assetRepository, MongoTemplate mongoTemplate) {
        this.assetRepository = assetRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        // Step A: Save transactional asset data to Postgres SQL
        Asset savedAsset = assetRepository.save(asset);

        // Step B: Save audit logging document to MongoDB directly
        try {
            AuditLog log = new AuditLog();
            log.setAssetId(savedAsset.getId());
            log.setAction("ASSET_CREATED");
            log.setDetails("Asset '" + savedAsset.getName() + "' registered by owner team: " + savedAsset.getOwner());
            log.setTimestamp(LocalDateTime.now());

            // Explicitly force write down to MongoDB container port
            mongoTemplate.save(log);
            System.out.println("📝 SUCCESS: Compliance Audit document written to MongoDB database collection.");
        } catch (Exception e) {
            System.err.println("⚠️ MONGO ERROR DETECTED: " + e.getMessage());
            e.printStackTrace(); // This will print the true root cause stack trace if a drop happens!
        }

        return savedAsset;
    }
}
