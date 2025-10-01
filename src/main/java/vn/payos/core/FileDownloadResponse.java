package vn.payos.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FileDownloadResponse */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadResponse {
  private String filename;
  private String contentType;
  private Long size;
  private byte[] data;
}
