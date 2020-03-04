package service;

import java.io.InputStream;

public interface FileService {

    InputStream convertTelegramDocumentToInputStream(String filePath);
}
