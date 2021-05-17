package ch.valtech.kubernetes.microservice.cluster.filestorage.util;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileNameCleaner {

  private static final int[] ILLEGAL_CHARS = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
      17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};

  static {
    Arrays.sort(ILLEGAL_CHARS);
  }

  public static String cleanFilename(String badFileName) {
    String nullBytesStripped = FilenameUtils.getName(badFileName);
    StringBuilder cleanName = new StringBuilder();
    int len = nullBytesStripped.codePointCount(0, nullBytesStripped.length());
    for (int i = 0; i < len; i++) {
      int c = nullBytesStripped.codePointAt(i);
      if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0) {
        cleanName.appendCodePoint(c);
      }
    }
    return cleanName.toString();
  }

}
