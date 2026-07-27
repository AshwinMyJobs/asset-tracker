import Keycloak from 'keycloak-js';

const keycloakConfig = {
  url: 'http://localhost:8082',
  realm: 'asset-tracker-realm',
  clientId: 'asset-tracker-frontend',
};

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
