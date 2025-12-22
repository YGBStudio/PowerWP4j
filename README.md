# PowerWP4j - Power WP for Java

![Java](https://img.shields.io/badge/java-21-blue)
[![License](https://img.shields.io/badge/license-Apache--2.0-orange)](https://www.apache.org/licenses/LICENSE-2.0)
![Code Style](https://img.shields.io/badge/code_style-Google%20Java-blueviolet)
![Status](https://img.shields.io/badge/status-alpha-orange)

Build, update, and analyze WordPress content efficiently with a type-safe REST client, incremental caching, 
and offline analysis utilities — all designed with expressive Java idioms and testable, lightweight abstractions.

## Table of contents
1. [What is PowerWP4j?](#what-is-powerwp4j)
2. [At a glance](#at-a-glance)
3. [Workflows](#workflows)
4. [Requirements](#requirements)
5. [Install / dependency usage](#install--dependency-usage)
6. [Quickstart](#quickstart)
7. [FAQs](#faqs)
8. [Cache design notes](#cache-design-notes)
9. [Key modules](#key-modules)
10. [Possible Use Cases](#possible-use-cases)
11. [Non-Goals](#non-goals)
12. [Project layout](#project-layout)
13. [Development](#development)
14. [Security](#security)
15. [License](#license)

## What is PowerWP4j?

A modern, modular Java toolkit designed to simplify WordPress automation and offline content analysis.  

It provides:
- A type-safe REST client for creating and updating content
- An incremental local caching system for offline workflows
- Powerful analysis utilities for taxonomy extraction, reporting, and data modeling
- Developer-friendly abstractions that preserve WordPress concepts while making it easier to automate, test, analyze, and innovate by building new workflows or insights from WordPress content

Built with expressive Java idioms and immutability, PowerWP4j streamlines content management automation in a safe and testable way.

**See [Possible Use Cases](#possible-use-cases) & [Non-Goals](#non-goals)**

## At a glance
- **Build**: Maven (`pom.xml`) · **Java**: `21` · **Packaging**: `jar` · **License**: Apache-2.0
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
## FAQs

### Why is PowerWP4j marked as alpha?
PowerWP4j is currently in alpha because its public API may evolve as real-world usage
increases. Core concepts (REST client, cache, analyzer) are stable, but method
signatures and module boundaries may change before a 1.0 release.

Additional features and refinements are expected as the project incorporates
both internal experimentation and external feedback.

### Why does PowerWP4j use a local JSON cache instead of a database?
The local JSON cache is designed to:
- Keep the library lightweight and dependency-free
- Enable deterministic, offline analysis
- Make cached content easy to inspect, move, and version-control

If you require a database-backed workflow, the cache can be treated as an ingestion
layer, with analyzed results persisted to a datastore of your choice.

### Does PowerWP4j support custom post types?
Yes. PowerWP4j supports custom post types exposed through the WordPress REST API,
as long as they are registered with `'show_in_rest' => true`.

Depending on your use case, additional modeling may be required. Custom post types
can be introduced by implementing the
`net.ygbstudio.base.extension.PostTypeEnum` interface.

PowerWP4j also provides extension interfaces for custom taxonomies. If your theme or
plugin defines additional taxonomies, you can implement:
- `net.ygbstudio.base.extension.ClassMarkerEnum`
- `net.ygbstudio.base.extension.ClassValueEnum`

It is also possible to extract specific keys from the JSON cache by implementing:
- `net.ygbstudio.base.extension.CacheKeyEnum`
- `net.ygbstudio.base.extension.CacheSubKeyEnum` (for elements with nested JSON objects)

Below is a minimal sample implementation illustrating the pattern used by all
extension interfaces:

```java
import net.ygbstudio.base.extension.CacheKeyEnum;

public enum MyCacheKeys implements CacheKeyEnum {
    SEO_SCORE("seo_score");

    private final String key;

    MyCacheKeys(String key) {
        this.key = key;
    }

    @Override
    public String value() {
        return key;
    }
}
```
**Note:** Extension interfaces define an explicit `value()` method, which is used
internally for serialization and REST mapping. Implementations may override
`toString()` for convenience or debugging, but the library does not rely on it for
correctness.

All utilities in PowerWP4j operate on extension interface types rather than concrete
implementations. As a result, any client-provided extension that follows this pattern
is supported transparently by the library.


### Does PowerWP4j provide default implementations of its extension interfaces?
Yes. Default implementations are provided in the
`net.ygbstudio.powerwp4j.models.schema` package.
All built-in implementations are prefixed with `WP`.

### Is this library safe for production use?
PowerWP4j can be used in production automation workflows, provided that:
- A specific version is pinned
- API changes are expected until the 1.0 release
- Cache synchronization is treated as an explicit, controlled operation


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
- Formatting: `mvn fmt:format`
- JDK: 21

PowerWP4j uses [fmt-maven-plugin](https://github.com/spotify/fmt-maven-plugin) to maintain consistent code style. 
The plugin automatically formats Java code according to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).


## Security
- Never commit credentials (application passwords, usernames, site info).
- If you create `appConfig.properties`, ensure it is ignored by version control.

## License

PowerWP4j is licensed under the Apache License, Version 2.0.

Copyright © 2025–2026 YGBStudio

## Disclaimer

This project is a third-party tool and is not affiliated with or endorsed by WordPress.org.


