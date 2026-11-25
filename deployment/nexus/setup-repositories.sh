#!/usr/bin/env bash
# Prepares the Nexus instance used by this platform.
#
# This reuses the repositories Nexus creates by default
#   hosted : maven-releases, maven-snapshots
#   proxy  : maven-central
# and only adds what is missing:
#   proxy  : gradle-plugins  (https://plugins.gradle.org/m2/)
#   group  : istad-public    (what every build in this repo resolves from)
#
# Nothing existing is modified or deleted, so other projects sharing this
# Nexus instance are unaffected.
#
# Usage:
#   export NEXUS_URL=http://localhost:8081
#   export NEXUS_USERNAME=admin
#   export NEXUS_PASSWORD='********'
#   ./setup-repositories.sh
set -euo pipefail

NEXUS_URL="${NEXUS_URL:-http://localhost:8081}"
: "${NEXUS_USERNAME:?set NEXUS_USERNAME}"
: "${NEXUS_PASSWORD:?set NEXUS_PASSWORD}"

api() {
  curl -fsS -u "${NEXUS_USERNAME}:${NEXUS_PASSWORD}" -H 'Content-Type: application/json' "$@"
}

repo_exists() {
  curl -s -o /dev/null -w '%{http_code}' \
    -u "${NEXUS_USERNAME}:${NEXUS_PASSWORD}" \
    "${NEXUS_URL}/service/rest/v1/repositories/$1" | grep -q '^200$'
}

create_repo() {
  local name="$1" type="$2" body="$3"
  if repo_exists "${name}"; then
    echo "  = ${name} already exists"
    return
  fi
  api -X POST "${NEXUS_URL}/service/rest/v1/repositories/maven/${type}" -d "${body}" >/dev/null
  echo "  + ${name} created"
}

echo "Configuring ${NEXUS_URL} ..."

create_repo gradle-plugins proxy '{
  "name": "gradle-plugins",
  "online": true,
  "storage": {"blobStoreName": "default", "strictContentTypeValidation": true},
  "proxy": {
    "remoteUrl": "https://plugins.gradle.org/m2/",
    "contentMaxAge": 1440,
    "metadataMaxAge": 1440
  },
  "negativeCache": {"enabled": true, "timeToLive": 1440},
  "httpClient": {"blocked": false, "autoBlock": true},
  "maven": {"versionPolicy": "RELEASE", "layoutPolicy": "PERMISSIVE"}
}'

create_repo istad-public group '{
  "name": "istad-public",
  "online": true,
  "storage": {"blobStoreName": "default", "strictContentTypeValidation": true},
  "group": {"memberNames": ["maven-releases", "maven-snapshots", "maven-central", "gradle-plugins"]}
}'

# Gradle resolves dependencies without credentials, so read access is anonymous
# (the Nexus default). Skip with SKIP_ANONYMOUS=1 if your instance forbids it.
if [ "${SKIP_ANONYMOUS:-0}" != "1" ]; then
  echo "Enabling anonymous read access ..."
  api -X PUT "${NEXUS_URL}/service/rest/v1/security/anonymous" -d '{
    "enabled": true,
    "userId": "anonymous",
    "realmName": "NexusAuthorizingRealm"
  }' >/dev/null
  echo "  + anonymous read enabled"
fi

echo "Check:  curl -s -o /dev/null -w '%{http_code}\\n' ${NEXUS_URL}/repository/istad-public/"
echo "Browse: ${NEXUS_URL}/#browse/browse:istad-public"
