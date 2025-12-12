package net.ygbstudio.powerwp4j.models.entities;

import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.readValueFromJson;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.toJsonString;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.writeJsonFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CacheMetaTest {

  private CacheMeta sampleCacheMeta;

  @BeforeEach
  void setUp() {
    sampleCacheMeta = new CacheMeta(10, 20, LocalDate.now());
  }

  @Test
  void cacheMetaParseTest() {
    String json = toJsonString(sampleCacheMeta);
    CacheMeta parsedCacheMeta = readValueFromJson(json, CacheMeta.class);
    assertEquals(sampleCacheMeta, parsedCacheMeta);
  }

  @Test
  void cacheMetaEqualityTest() {
    CacheMeta anotherCacheMeta = new CacheMeta(10, 20, sampleCacheMeta.lastUpdated());
    assertEquals(sampleCacheMeta, anotherCacheMeta);
  }

  @Test
  void cacheMetaInequalityTest() {
    CacheMeta differentCacheMeta = new CacheMeta(15, 25, sampleCacheMeta.lastUpdated().plusDays(1));
    assertNotEquals(sampleCacheMeta, differentCacheMeta);
  }

  @Test
  void cacheMetaWriteTest() {
    File sampleCacheMetaFile = new File("sample_cache_meta.json");

    writeJsonFs(sampleCacheMetaFile, sampleCacheMeta);
    if (!sampleCacheMetaFile.exists()) {

      throw new AssertionError("Cache meta file was not created.");
    }
    CacheMeta writtenCacheMeta = readJsonFs(new File("sample_cache_meta.json"), CacheMeta.class);
    assertEquals(sampleCacheMeta, writtenCacheMeta);
    assertEquals(writtenCacheMeta.totalPages(), sampleCacheMeta.totalPages());
    assertEquals(writtenCacheMeta.totalPosts(), sampleCacheMeta.totalPosts());
    assertEquals(writtenCacheMeta.lastUpdated(), sampleCacheMeta.lastUpdated());
    sampleCacheMetaFile.deleteOnExit();
  }
}
