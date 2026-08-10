package com.enterprise.asset_service.service;

import com.enterprise.asset_service.model.Asset;
import com.enterprise.asset_service.repository.jpa.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;

    public AssetService(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    // 👈 CRITICAL: This annotation tells your AOP Aspect to switch the DataSource context key to REPLICA_READ
    @Transactional(readOnly = true)
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    // 👈 CRITICAL: Standard write operations run without readOnly, automatically targeting the primary writer node
    @Transactional
    public Asset saveAsset(Asset asset) {
        return assetRepository.save(asset);
    }
}
