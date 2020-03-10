package util;

import java.util.ArrayList;
import java.util.List;

public class MimeTypesUtil {

    private final static List<String> supportedMimeTypes;

    static {
        supportedMimeTypes = new ArrayList<>();
        supportedMimeTypes.add("application/msword");
        supportedMimeTypes.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        supportedMimeTypes.add("application/rtf");
        supportedMimeTypes.add("image/jpeg");
        supportedMimeTypes.add("image/x-citrix-jpeg");
        supportedMimeTypes.add("image/png");
        supportedMimeTypes.add("image/x-citrix-png");
        supportedMimeTypes.add("image/x-png");
        supportedMimeTypes.add("image/bmp");
        supportedMimeTypes.add("application/x-mobipocket-ebook");
        supportedMimeTypes.add("application/vnd.amazon.ebook");
        supportedMimeTypes.add("application/pdf");
    }

    public static boolean supportsMimeType(String mimeType) {
        return supportedMimeTypes.contains(mimeType);
    }
}
