# PowerWP4j

[![CI](https://github.com/YGBStudio/PowerWP4j/actions/workflows/maven.yml/badge.svg)](https://github.com/YGBStudio/PowerWP4j/actions/workflows/maven.yml)
![Java](https://img.shields.io/badge/java-21-blue)
[![License](https://img.shields.io/badge/license-Apache--2.0-orange)](https://www.apache.org/licenses/LICENSE-2.0)
![Code Style](https://img.shields.io/badge/code_style-Google%20Java-blueviolet)
![Status](https://img.shields.io/badge/status-alpha-orange)

A modern Java toolkit for WordPress automation and offline content analysis. Build, update, and analyze WordPress content with a type-safe REST client, incremental caching, and powerful analysis utilities—all designed with expressive Java idioms and testable abstractions.

## Table of Contents
1. [Features](#features)
2. [Workflows](#workflows)
3. [Requirements](#requirements)
4. [Installation](#installation)
5. [Quickstart](#quickstart)
6. [Use Cases](#use-cases)
7. [Key Modules](#key-modules)
8. [Extensibility](#extensibility)
9. [FAQs](#faqs)
10. [Development](#development)
11. [License](#license)

## Features

| Capability | Description |
|------------|-------------|
| **REST Client** | Create, update, delete posts, categories, tags, and media using Application Password auth |
| **Local Cache** | Fetch WordPress posts into a JSON file with metadata; supports incremental sync via WordPress headers |
| **Offline Analysis** | Query the cache without HTTP calls—counts, sets, snapshots of posts, slugs, tags, categories, GUIDs |
| **Taxonomy Extraction** | Extract and aggregate taxonomy data for automation, reporting, or ML workflows |

**Design philosophy**: Expressive, declarative modern Java (records, `Optional`, streams, immutability-first) with documented nullability. Alpha status—API may evolve.

## Workflows

### REST Client
- Configure site connection via `WPSiteInfo` (properties file or environment variables)
- Create, update, delete posts with `WPBasicPayloadBuilder`
- Manage taxonomies (categories, tags) and upload media with optional metadata

### Cache Management
- Fetch posts into a local JSON cache (`fetchJsonCache`)
- Sync incrementally using WordPress `x-wp-total` / `x-wp-totalpages` headers (`cacheSync`)
- Metadata stored in companion `<cacheName>_metadata.json` file

### Cache Analysis
- Load cache in-memory—no network calls during analysis
- Extract sets/snapshots: IDs, slugs, links, categories, tags, excerpts, GUIDs
- Map taxonomy data with custom transformations for automation or ML workflows

## Requirements

- **JDK 21**
- A WordPress site with:
  - REST API enabled (`/wp-json/wp/v2/...`)
  - An **Application Password** (WP Admin → Users → Profile → Application Passwords)

## Installation

### Build & install locally
```bash
mvn clean install
```

### Maven dependency
```xml
<dependency>
  <groupId>net.ygbstudio</groupId>
  <artifactId>powerwp4j</artifactId>
  <version><!-- version --></version>
</dependency>
```

### Runtime dependencies (minimal)
- **Jackson 3.x** — JSON processing
- **Apache Tika Core** — MIME type detection
- **Apache Commons Lang3** — String utilities
- **SLF4J API** — Logging abstraction (no implementation forced)

## Quickstart

### 1. Configure site info

**Properties file** (`appConfig.properties` on classpath):
```properties
wp.fqdn=example.com
wp.user=my_username
wp.appPass=xxxx xxxx xxxx xxxx
```

```java
WPSiteInfo siteInfo = WPSiteInfo.fromConfigResource("appConfig.properties")
    .orElseThrow(() -> new IllegalStateException("Missing config"));
```

**Environment variables**:
```bash
export WP_FQDN=example.com WP_USER=my_username WP_APP_PASS='xxxx xxxx xxxx'
```
```java
WPSiteInfo siteInfo = WPSiteInfo.fromEnv().orElseThrow();
```

### 2. Create a post
```java
var payload = WPBasicPayloadBuilder.builder()
    .title("Hello from PowerWP4j")
    .status(WPStatus.DRAFT)
    .type(WPPostType.POST)
    .slug("hello-powerwp4j")
    .content("Created via WP REST API")
    .build();

WPRestClient client = WPRestClient.of(siteInfo);
Optional<HttpResponse<String>> response = client.createPost(payload);
```

### 3. Upload media
```java
// Optional: pass WPMediaPayloadBuilder to update alt text, caption, description
client.uploadMedia(Path.of("/path/to/image.jpg"));
```

### 4. Create and sync cache
```java
Path cachePath = Path.of("wp-posts.json");
WPCacheManager cacheManager = new WPCacheManager(siteInfo, cachePath);

cacheManager.fetchJsonCache(true);      // Initial fetch (overwrites if true)
boolean updated = cacheManager.cacheSync(); // Incremental sync
```

### 5. Analyze cache offline
```java
WPCacheAnalyzer analyzer = new WPCacheAnalyzer(Path.of("wp-posts.json"));

long count = analyzer.getPostCount();
var slugs = analyzer.getSlugs();
var categories = analyzer.getCleanCategories();
var tags = analyzer.getCleanTags();
```

### 6. Extract taxonomies
```java
UnaryOperator<String> cleanOp = tag ->
    tag.replaceFirst("^tag-", "").replaceAll("[^a-zA-Z0-9]", " ").trim();

var mappedTags = analyzer.mapWPClassId(cleanOp, TaxonomyMarker.TAG, TaxonomyValues.TAGS);
```

## Use Cases

**Content Automation**
- Programmatically create/update posts using metadata from existing content
- Automate taxonomy assignment based on cached data

**Taxonomy & Metadata Analysis**
- Aggregate category/tag usage across a site
- Compute taxonomy frequencies without REST calls

**Data Modeling & ML**
- Use the local cache as a structured dataset
- Feed analyzed WordPress data into ML pipelines

**Offline-First Analysis**
- Perform repeatable analysis without network dependency
- Ensure deterministic results from fixed snapshots

### Non-Goals

PowerWP4j **does not** aim to:
- Replace WordPress as a CMS
- Provide exhaustive WordPress admin coverage
- Offer automatic/real-time cache sync
- Include UI components or CLI tooling
- Serve as a general-purpose HTTP framework

## Key Modules

| Module | Purpose |
|--------|---------|
| `engine.WPRestClient` | REST façade for posts, taxonomies, media |
| `engine.WPCacheManager` | Fetches/syncs cache JSON with incremental support |
| `engine.WPCacheAnalyzer` | Offline analysis utilities |
| `builders.*` | Chainable payload builders with snake_case Jackson mapping |
| `services.*` | HTTP plumbing and REST utilities |
| `models.schema` | Default WordPress schema enums (prefixed `WP`) |
| `models.taxonomies` | Taxonomy helpers |

### Cache Design

- **Source of truth**: Analysis runs strictly against the local cache
- **Metadata**: Uses WordPress `x-wp-total` and `x-wp-totalpages` headers
- **Incremental sync**: New pages fetched and merged by post `id`
- **Files**: `<cacheName>.json` + `<cacheName>_metadata.json`

## Extensibility

PowerWP4j supports custom post types and taxonomies via extension interfaces:

| Interface | Purpose |
|-----------|---------|
| `PostTypeEnum` | Custom post types (must have `show_in_rest => true`) |
| `ClassMarkerEnum` | Custom taxonomy markers |
| `ClassValueEnum` | Custom taxonomy values |
| `CacheKeyEnum` | Custom cache keys |
| `CacheSubKeyEnum` | Nested JSON object keys |

**Example implementation:**
```java
public enum MyCacheKeys implements CacheKeyEnum {
    SEO_SCORE("seo_score");

    private final String key;

    MyCacheKeys(String key) { this.key = key; }

    @Override
    public String value() { return key; }
}
```

> **Note**: Extension interfaces use `value()` for serialization/REST mapping. `toString()` may be overridden for debugging but isn't used internally.

Default implementations are in `net.ygbstudio.powerwp4j.models.schema` (schema) and `net.ygbstudio.powerwp4j.models.taxonomies` (taxonomies).

## FAQs

**Why alpha status?**  
Core concepts are stable, but method signatures/package boundaries may change before 1.0.

**Why JSON cache instead of a database?**  
Keeps the library lightweight, enables deterministic offline analysis, and makes content easy to inspect/version-control. Use it as an ingestion layer for database workflows.

**Can I use local WordPress environments?**  
Yes. Supports ignoring SSL certificate issues for localhost/Docker/self-signed setups. SSL relaxation is for development only.

## Development

| Command | Purpose |
|---------|---------|
| `mvn clean package` | Build |
| `mvn test` | Run tests |
| `mvn fmt:format` | Format code (Google Java Style) |
| `mvn javadoc:javadoc` | Generate docs |

### Project Layout
```
src/main/java/net/ygbstudio/powerwp4j/
├── base/       # Extension models and abstractions
├── engine/     # Entry points (WPCacheManager, WPCacheAnalyzer, WPRestClient)
├── builders/   # Chainable JSON payload builders
├── services/   # HTTP plumbing
├── models/     # Schema enums, entities, taxonomies
├── exceptions/ # Library exceptions
└── utils/      # JSON support, functional helpers
```

## Security

- Never commit credentials (application passwords, usernames, site info)
- Ensure `appConfig.properties` is in `.gitignore`

## License

Apache License, Version 2.0  
Copyright © 2025–2026 YGBStudio

---

*This project is not affiliated with or endorsed by WordPress.org.*
