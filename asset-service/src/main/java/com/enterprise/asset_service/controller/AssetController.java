package com.enterprise.asset_service.controller;

import com.enterprise.asset_service.model.Asset;
import com.enterprise.asset_service.model.AuditLog;
import com.enterprise.asset_service.service.AssetService; // 👈 1. Injected import
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetController {

    private final AssetService assetService; // 👈 2. Updated to service component abstraction
    private final MongoTemplate mongoTemplate;

    // 👈 3. Updated constructor mapping
    public AssetController(AssetService assetService, MongoTemplate mongoTemplate) {
        this.assetService = assetService;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ASSET_USER', 'ASSET_ADMIN')")
    public List<Asset> getAllAssets() {
        System.out.println("🔍 RAW TOKEN AUTH DETAILS: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        // 👈 4. Rerouted execution context boundary through service to activate aspect interception
        return assetService.getAllAssets();
    }

    @PostMapping
    @PreAuthorize("hasRole('ASSET_ADMIN')")
    public Asset createAsset(@RequestBody Asset asset) {
        // Step A: Save transactional asset data to Postgres primary via Service proxy wrapper
        Asset savedAsset = assetService.saveAsset(asset);

        // Step B: Save audit logging document to MongoDB directly
        try {
            String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

            AuditLog log = new AuditLog();
            log.setAssetId(savedAsset.getId());
            log.setAction("ASSET_CREATED");
            log.setDetails("Asset '" + savedAsset.getName() + "' registered by user: " + currentUsername + " [Team: " + savedAsset.getOwner() + "]");
            log.setTimestamp(LocalDateTime.now());

            mongoTemplate.save(log);
            System.out.println("📝 SUCCESS: Compliance Audit document written to MongoDB database collection for user: " + currentUsername);
        } catch (Exception e) {
            System.err.println("⚠️ MONGO ERROR DETECTED: " + e.getMessage());
            e.printStackTrace();
        }

        return savedAsset;
    }
}
