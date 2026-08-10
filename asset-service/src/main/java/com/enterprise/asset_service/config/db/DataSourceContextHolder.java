    package com.enterprise.asset_service.config.db;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class DataSourceContextHolder {
        private static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);
        private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

        public static void set(DataSourceType type) {
            log.debug("Setting DataSource context routing key to: [{}]", type);
            CONTEXT.set(type);
        }

        public static DataSourceType get() {
            DataSourceType current = CONTEXT.get();
            if (current == null) {
                // Defaulting fallback safely
                return DataSourceType.PRIMARY_WRITE;
            }
            return current;
        }

        public static void clear() {
            CONTEXT.remove();
        }
    }
