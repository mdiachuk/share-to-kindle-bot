package service.impl;

import service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class FileServiceImpl implements FileService {

    @Override
    public InputStream convertTelegramDocumentToInputStream(String filePath) {
        InputStream is = null;
        try {
            is = new URL("https://api.telegram.org/file/bot" + System.getenv("BOT_TOKEN") + "/" + filePath).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }
}
