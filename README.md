# PowerWP4j

![Java](https://img.shields.io/badge/java-21-blue)
![Code Style](https://img.shields.io/badge/code_style-Google%20Java-blueviolet)
![License](https://img.shields.io/badge/license-GPL--3.0-blue)
![Status](https://img.shields.io/badge/status-alpha-orange)

PowerWP4j (Power WP for Java) is a safe, modular, Java-native toolkit for WordPress automation.
It provides a cohesive set of APIs for REST operations, local caching, and offline analysis,
supporting reliable, testable, and maintainable content management automation and reporting workflows.

## Table of contents
1. [At a glance](#at-a-glance)
2. [Workflows](#workflows)
3. [Requirements](#requirements)
4. [Install / dependency usage](#install--dependency-usage)
5. [Quickstart](#quickstart)
6. [Cache design notes](#cache-design-notes)
7. [Key modules](#key-modules)
8. [Possible Use Cases](#possible-use-cases)
9. [Non-Goals](#non-goals)
10. [Project layout](#project-layout)
11. [Development](#development)
12. [Security](#security)
13. [License](#license)

## At a glance
- **Build**: Maven (`pom.xml`) · **Java**: `21` · **Packaging**: `jar` · **License**: GPL-3.0-or-later
- **Style**: expressive, declarative modern Java (records, `Optional`, streams, immutability-first), using documented nullability only where necessary.
- **REST client**: create/update/delete posts, categories, tags, and media (with metadata) using Application Password auth.
- **Local cache**: fetch WordPress posts into a JSON file plus metadata; supports incremental sync.
- **Cache analysis**: query the local cache without additional HTTP calls—counts, sets, and snapshots of posts, slugs, tags, categories, GUIDs, excerpts, and more.
- **Status**: Alpha (API may evolve)

## Workflows
### REST client
- Instance-based configuration via `WPSiteInfo`.
- Convenience operations: create posts, change status, add taxonomies, upload media (with optional metadata update).

### Cache
- Fetch posts into a local JSON cache (`fetchJsonCache`).
- Incrementally update the cache using `cacheSync()` (driven by WordPress `x-wp-total` and `x-wp-totalpages` headers).
- Companion metadata file named `<cacheName>_metadata.json`.

### Cache analysis
- Load the cache in-memory; no REST calls while analyzing.
- Extract sets and snapshots (IDs, slugs, links, categories, tags, excerpts, GUIDs) and compute counts.

### Taxonomy Extraction
- Extract taxonomy information (categories, tags, term IDs) directly from the local cache—no additional REST calls required.
- Ideal for automation, reporting, or ML workflows.
- See the [Quickstart](#quickstart) section for usage examples with `WPCacheAnalyzer`.

## Requirements
- **JDK 21**
- A WordPress site with:
  - REST API enabled (`/wp-json/wp/v2/...`)
  - A user with an **Application Password** (WP Admin → Users → Profile → Application Passwords)

## Install / dependency usage
### Local install (recommended during development)
Build and install into your local Maven repository:

```bash
mvn clean install
```

Then depend on it from another Maven project:

```xml
<dependency>
  <groupId>net.ygbstudio</groupId>
  <artifactId>powerwp4j</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Quickstart
PowerWP4j’s offline analysis can be leveraged in a variety of workflows.

### 1) Configure site info (`WPSiteInfo`)
`WPSiteInfo` centralizes connection details (`wp.fqdm`, `wp.user`, `wp.appPass`).

**Properties file (recommended)**  
Place `appConfig.properties` on the classpath (e.g., `src/main/resources` or `src/test/resources`):
```properties
wp.fqdm=example.com
wp.user=my_username
wp.appPass=xxxx xxxx xxxx xxxx
```
Load it with:
```java
WPSiteInfo siteInfo = WPSiteInfo.fromConfigResource("appConfig.properties")
    .orElseThrow(() -> new IllegalStateException("Missing appConfig.properties"));
```

**Environment variables**  
Alternatively, use `WPSiteInfo.fromEnv()` to pick the site information from environment variables:
```bash
# Using conventional environment variables
export WP_FQDM=example.com
export WP_USER=my_username
export WP_APP_PASS='xxxx xxxx xxxx xxxx'

# Run tests with the environment variables
mvn test
```

### 2) Create a post (REST client)
```java
import java.net.http.HttpResponse;
import java.util.Optional;
import net.ygbstudio.powerwp4j.builders.WPBasicPayloadBuilder;
import net.ygbstudio.powerwp4j.engine.WPRestClient;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;
import net.ygbstudio.powerwp4j.models.schema.WPPostType;
import net.ygbstudio.powerwp4j.models.schema.WPStatus;

WPSiteInfo siteInfo = WPSiteInfo.fromConfigResource("appConfig.properties")
    .orElseThrow();

// The .clear() call is optional as the builder can be reused.
var payload = WPBasicPayloadBuilder.builder()
    .clear()
    .title("Hello from PowerWP4j")
    .status(WPStatus.DRAFT)
    .type(WPPostType.POST)
    .slug("hello-powerwp4j")
    .content("This is a post created through the WP REST API")
    .build();

WPRestClient client = WPRestClient.of(siteInfo);
Optional<HttpResponse<String>> response = client.createPost(payload);
```

### 3) Upload media
```java
import java.nio.file.Path;
import net.ygbstudio.powerwp4j.engine.WPRestClient;

// Optionally provide a WPMediaPayloadBuilder payload to update metadata after upload.
// Add alt text, caption, and description to your media. 
client.uploadMedia(Path.of("/path/to/image.jpg"));
```

### 4) Create and sync the local cache
```java
import java.nio.file.Path;
import net.ygbstudio.powerwp4j.engine.WPCacheManager;
import net.ygbstudio.powerwp4j.models.entities.WPSiteInfo;

WPSiteInfo siteInfo = WPSiteInfo.fromConfigResource("appConfig.properties")
    .orElseThrow();

Path cachePath = Path.of("wp-posts.json");
WPCacheManager cacheManager = new WPCacheManager(siteInfo, cachePath);

// Initial cache creation (overwrites if true)
cacheManager.fetchJsonCache(true);

// Later: incremental sync (returns false if already up-to-date)
boolean updated = cacheManager.cacheSync();
```

### 5) Analyze the cache (offline)
```java
import java.nio.file.Path;
import net.ygbstudio.powerwp4j.engine.WPCacheAnalyzer;

WPCacheAnalyzer analyzer = new WPCacheAnalyzer(Path.of("wp-posts.json"));

long postCount = analyzer.getPostCount();
var slugs = analyzer.getSlugs();
var categories = analyzer.getCategories();
var tags = analyzer.getTags();
var guids = analyzer.getGuids(); // snapshots of GUIDs with rendered value
```

### 6) Taxonomy Extraction (Offline)

```java
import java.util.function.UnaryOperator;
import net.ygbstudio.powerwp4j.engine.WPCacheAnalyzer;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyMarker;
import net.ygbstudio.powerwp4j.models.taxonomies.TaxonomyValues;

WPCacheAnalyzer analyzer = new WPCacheAnalyzer(Path.of("wp-posts.json"));

// Normalize taxonomy strings
UnaryOperator<String> cleanOperator = tag ->
    tag.replaceFirst("^tag-", "").replaceAll("[^a-zA-Z0-9]", " ").trim();

// Map tags to WordPress counts
// You can pass the UnaryOperator.identity() to ignore the transformation step.
var mappedTags = analyzer.mapWPClassId(cleanOperator, TaxonomyMarker.TAG, TaxonomyValues.TAGS);
```

## Cache design notes
- **Source of truth**: analysis is strictly against the local cache; keep it fresh with `cacheSync()`.
- **Metadata**: `WPCacheManager` uses WordPress headers `x-wp-total` and `x-wp-totalpages` to track totals and decide deltas.
- **Incremental strategy**: new pages are fetched and merged by comparing post `id`.
- **Files**: cache JSON plus a sibling `<cacheName>_metadata.json`.

## Key modules
- **`engine.WPRestClient`** — facade over REST calls using `WPSiteInfo`; helpers for posts, taxonomies, and media.
- **`engine.WPCacheManager`** — fetches/syncs cache JSON and metadata; incremental sync support.
- **`engine.WPCacheAnalyzer`** — offline analysis utilities (counts, sets, and snapshots of cache keys/subkeys).
- **Payload builders** — `builders.WPBasicPayloadBuilder` and `builders.WPMediaPayloadBuilder` (chainable, snake_case Jackson mapper).
- **HTTP utilities** — `services.HttpRequestService` and `services.RestClientService` for URL composition, Basic Auth header, and CRUD helpers.

## Possible use cases

PowerWP4j is designed to support a variety of automation and analysis workflows built on top of WordPress data. While it is primarily a REST and caching toolkit, its offline analysis capabilities make it suitable for more advanced use cases.

### Content automation
- Programmatically create, update, and publish posts using metadata derived from existing content.
- Automate taxonomy assignment (categories, tags) based on previously published posts.
- Generate new posts by reusing or transforming cached content, excerpts, or taxonomies.

### Taxonomy and metadata analysis
- Extract and aggregate category and tag usage across a WordPress site.
- Compute taxonomy frequencies (e.g., how often a tag or category appears).
- Build lightweight content statistics without issuing additional REST API calls.

### Data modeling and machine learning workflows
- Use the local cache as a structured dataset for feature extraction.
- Aggregate metadata (tags, categories, excerpts, GUIDs, content fields) to build content models.
- Feed offline-analyzed WordPress data into machine learning pipelines or recommendation systems.

### Offline-first and repeatable analysis
- Perform repeated analysis runs without network dependency.
- Ensure deterministic results by analyzing a fixed snapshot of WordPress content.
- Decouple data acquisition (REST calls) from data processing and experimentation.

## Non-goals

PowerWP4j is intentionally focused and does **not** aim to:

- **Replace WordPress itself**  
  PowerWP4j is a client-side automation and analysis toolkit, not a CMS, theme framework, or plugin system.

- **Provide a full WordPress admin abstraction**  
  The library targets common REST-driven workflows and offline analysis, not exhaustive coverage of every WordPress admin feature.

- **Provide automatic or real-time cache synchronization**  
  Cache updates are explicit and client-driven; synchronization occurs only when requested by the consuming application.

- **Hide WordPress concepts behind heavy abstractions**  
  WordPress-specific terminology (posts, taxonomies, statuses, REST endpoints) is preserved to maintain clarity and debuggability.

- **Include UI components or frontend tooling**  
  PowerWP4j is strictly a backend/library project and does not provide dashboards, CLIs, or visual tooling by default.

- **Serve as a general-purpose HTTP or REST framework**  
  Internal HTTP utilities exist only to support WordPress workflows and are not designed for reuse outside this context.

These constraints are deliberate and help keep PowerWP4j predictable, testable, and maintainable.

## Project layout
- `src/main/java/net/ygbstudio/powerwp4j`
  - `base/`   - extension models and base abstractions
  - `engine/` — entry points (`WPCacheManager`, `WPCacheAnalyzer`, `WPRestClient`)
  - `builders/` — chainable JSON payload builders
  - `services/` — HTTP plumbing and REST conveniences
  - `models/` — schema enums, entities, taxonomy helpers
  - `exceptions/` — library-specific exceptions
  - `utils/` — JSON support, functional helpers

## Development
- Build: `mvn clean package`
- Tests: `mvn test`
- Formatting: `mvn fmt:format` (uses `com.spotify.fmt:fmt-maven-plugin`)
- JDK: 21

PowerWP4j uses [fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin) to maintain consistent code style. 
The plugin automatically formats Java code according to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).


## Security
- Never commit credentials (application passwords, usernames, site info).
- If you create `appConfig.properties`, ensure it is ignored by version control.

## License
PowerWP4j is licensed under the GNU General Public License v3.0 or later.
See the LICENSE file for details.

> Copyright © 2025-2026 YGBStudio
> 
> Original Author & Maintainer: Yoham Gabriel Barboza B.
> 
> SPDX-License-Identifier: GPL-3.0-or-later

