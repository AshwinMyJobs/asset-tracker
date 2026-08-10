package com.enterprise.asset_service.config.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {
    private static final Logger log = LoggerFactory.getLogger(RoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType currentType = DataSourceContextHolder.get();
        log.info("🔀 ROUTING DATABASE CONNECTION -> Target Routing Key: [{}]", currentType);
        return currentType;
    }
}
