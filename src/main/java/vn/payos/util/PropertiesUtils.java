package vn.payos.util;

import java.io.InputStream;
import java.util.Properties;

/** PropertiesUtils */
public class PropertiesUtils {
  /**
   * Get package version
   *
   * @return version
   */
  public static String getPackageVersion() {
    Properties props = new Properties();
    try (InputStream in = PropertiesUtils.class.getResourceAsStream("/version.properties")) {
      props.load(in);
      String version = props.getProperty("version");
      String manifestVersion = PropertiesUtils.class.getPackage().getImplementationVersion();
      return manifestVersion != null ? manifestVersion : version;
    } catch (Exception e) {
      // Ignore any exception and fall back to default
    }

    return "unknown";
  }
}
