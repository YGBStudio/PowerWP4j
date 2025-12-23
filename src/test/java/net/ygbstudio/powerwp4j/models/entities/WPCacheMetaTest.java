package net.ygbstudio.powerwp4j.models.entities;

import static net.ygbstudio.powerwp4j.utils.JsonSupport.readJsonFs;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.readValueFromJson;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.toJsonString;
import static net.ygbstudio.powerwp4j.utils.JsonSupport.writeJsonFs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import net.ygbstudio.powerwp4j.engine.WPCacheMeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WPCacheMetaTest {

  private WPCacheMeta sampleCacheMeta;

  @BeforeEach
  void setUp() {
    URL sampleCacheFile = WPCacheMetaTest.class.getClassLoader().getResource("wp-posts-test.json");
    WPCacheMeta.from(Path.of(sampleCacheFile.getPath()))
        .ifPresent(cacheMeta -> sampleCacheMeta = cacheMeta);
  }

  @Test
  void cacheMetaParseTest() {
    String json = toJsonString(sampleCacheMeta);
    WPCacheMeta parsedCacheMeta = readValueFromJson(json, WPCacheMeta.class);
    assertEquals(sampleCacheMeta, parsedCacheMeta);
  }

  @Test
  void cacheMetaEqualityTest() {
    WPCacheMeta anotherCacheMeta = new WPCacheMeta(10, 100, sampleCacheMeta.lastUpdated());
    assertEquals(sampleCacheMeta, anotherCacheMeta);
  }

  @Test
  void cacheMetaInequalityTest() {
    WPCacheMeta differentCacheMeta = new WPCacheMeta(15, 25, sampleCacheMeta.lastUpdated());
    assertNotEquals(sampleCacheMeta, differentCacheMeta);
  }

  @Test
  void cacheMetaWriteTest() {
    File sampleCacheMetaFile = new File("sample_cache_meta.json");

    writeJsonFs(sampleCacheMetaFile, sampleCacheMeta);
    if (!sampleCacheMetaFile.exists()) {

      throw new AssertionError("Cache meta file was not created.");
    }
    WPCacheMeta writtenCacheMeta =
        readJsonFs(new File("sample_cache_meta.json"), WPCacheMeta.class);
    assertEquals(sampleCacheMeta, writtenCacheMeta);
    assertEquals(writtenCacheMeta.totalPages(), sampleCacheMeta.totalPages());
    assertEquals(writtenCacheMeta.totalPosts(), sampleCacheMeta.totalPosts());
    assertEquals(writtenCacheMeta.lastUpdated(), sampleCacheMeta.lastUpdated());
    sampleCacheMetaFile.deleteOnExit();
  }
}
