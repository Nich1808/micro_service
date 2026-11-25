# Nexus Repository

Nexus 3 is the artifact store **and** the dependency proxy for the whole platform:

- every Gradle build in this repo resolves third-party dependencies through `istad-public`
  (a group that proxies Maven Central and the Gradle Plugin Portal)
- every reusable internal module (`kh.edu.istad:*`) is published to the hosted repos
- `kh.edu.istad:istad-common-bom` is a Java Platform (BOM) that pins the internal module
  versions plus the Spring Boot / springdoc versions, and consumers apply it with
  `implementation platform('kh.edu.istad:istad-common-bom:1.0.0')`

## Repositories

| Repository        | Type    | Origin   | Purpose                               |
| ----------------- | ------- | -------- | ------------------------------------- |
| `istad-public`    | group   | added    | single URL every build resolves from   |
| `gradle-plugins`  | proxy   | added    | `https://plugins.gradle.org/m2/`       |
| `maven-releases`  | hosted  | default  | published release artifacts (`1.0.0`) |
| `maven-snapshots` | hosted  | default  | published `-SNAPSHOT` builds          |
| `maven-central`   | proxy   | default  | `https://repo1.maven.org/maven2/`     |

`istad-public` groups the four repositories above, so it serves both the artifacts we
publish and everything proxied from Maven Central / the plugin portal.

Resolution URL: <http://localhost:8081/repository/istad-public/>

## 1. Start Nexus

```bash
docker compose -f deployment/nexus/docker-compose.yaml up -d
```

If a Nexus is already listening on `:8081` (for example the `nexus` container started
from another project), either keep it and skip this step, or stop it first — two
containers cannot share the port.

First boot takes ~1 minute. The initial admin password is at
`/nexus-data/admin.password` inside the container:

```bash
docker exec ite-nexus cat /nexus-data/admin.password
```

Change it in the UI on first login, then keep the new one for the next step.

## 2. Create the repositories

```bash
export NEXUS_URL=http://localhost:8081
export NEXUS_USERNAME=admin
export NEXUS_PASSWORD='your-admin-password'
bash deployment/nexus/setup-repositories.sh
```

The script is idempotent — existing repositories are left untouched. You can also create
them by hand in the UI (`Server administration → Repository → Create repository`); the
names above must match exactly because the builds reference them.

It also enables **anonymous read** (`Security → Anonymous`) because Gradle resolves
dependencies without credentials — that is the Nexus default and the normal setup for a
proxy. Use `SKIP_ANONYMOUS=1` to leave the security settings untouched, but then resolution
falls back to `mavenCentral()`.

## 3. Credentials for Gradle

The publish convention plugin reads credentials from, in order:

1. environment variables `NEXUS_USERNAME` / `NEXUS_PASSWORD`
2. Gradle properties `nexusUsername` / `nexusPassword` (e.g. in `~/.gradle/gradle.properties`)

The base URL comes from the `nexusUrl` Gradle property or `NEXUS_URL`, defaulting to
`http://localhost:8081`. Nothing secret is stored in the repo.

```bash
export NEXUS_USERNAME=deploy
export NEXUS_PASSWORD='********'
```

## 4. Publish, in this order

`istad-common-webflux` and the order-service libraries consume already-published
coordinates, so the BOM has to exist first:

```bash
# 4.1 BOM (Java Platform)
./gradlew -p istad-common :istad-common-bom:publish

# 4.2 shared libraries
./gradlew -p istad-common :istad-common-domain:publish :istad-common-restapi:publish \
    :istad-common-swagger:publish :istad-common-webflux:publish

# 4.3 order-service libraries
./gradlew -p microservices/order-service :order-service-domain:order-domain-core:publish
./gradlew -p microservices/order-service :order-service-domain:order-application-service:publish
./gradlew -p microservices/order-service :order-service-persistence:publish
./gradlew -p microservices/order-service :order-service-client:publish
```

`version` is `1.0.0` for every module (set in `build-logic/src/main/groovy/*-conventions.gradle`),
so artifacts land in `maven-releases`; a `-SNAPSHOT` version would go to `maven-snapshots`.

Publish each project in the order above — later modules compile against coordinates that
must already exist in Nexus, and `maven-releases` refuses to overwrite an existing asset
(`409 ... cannot be updated as asset already exists`). If you hit a stale "Could not find"
after publishing a dependency, re-run with `--refresh-dependencies`.

## 5. Consume

- `istad-common/istad-common-webflux` and `microservices/order-service/order-service-main`
  apply the BOM with `implementation platform('kh.edu.istad:istad-common-bom:1.0.0')` and
  then declare `kh.edu.istad:*` dependencies **without** a version.
- All `settings.gradle` files, `build-logic` conventions and `build-logic/build.gradle`
  declare `http://localhost:8081/repository/istad-public/` as the first repository.
  `mavenCentral()` / `gradlePluginPortal()` are kept as fallbacks so the build still works
  before Nexus has been populated — delete those fallback lines to force *all* resolution
  through the proxy.

## 6. Verify

```bash
# artifacts present in Nexus
curl -s -u "$NEXUS_USERNAME:$NEXUS_PASSWORD" \
  "http://localhost:8081/service/rest/v1/search?repository=maven-releases&group=kh.edu.istad"

# the BOM's dependencyManagement (anonymous, through the group)
curl -s "http://localhost:8081/repository/istad-public/kh/edu/istad/istad-common-bom/1.0.0/istad-common-bom-1.0.0.pom"

# consumer resolves everything through Nexus only
./gradlew -p microservices/order-service :order-service-main:dependencies \
  --configuration compileClasspath
```

The Nexus UI shows the downloads on `istad-public` (`Browse → istad-public`), which is the
proof that Maven Central is being proxied rather than contacted directly.
