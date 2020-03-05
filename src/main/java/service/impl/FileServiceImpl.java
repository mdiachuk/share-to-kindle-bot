package service.impl;

import service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;


public class FileServiceImpl implements FileService {

    @Override
    public Optional<InputStream> convertTelegramDocumentToInputStream(String filePath) {
        try {
            InputStream fileStream = new URL("https://api.telegram.org/file/bot"
                    + System.getenv("BOT_TOKEN") + "/" + filePath).openStream();
            return Optional.of(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
