package service;

import java.io.InputStream;
import java.util.Optional;

public interface FileService {

    Optional<InputStream> convertTelegramDocumentToInputStream(String filePath);
}
