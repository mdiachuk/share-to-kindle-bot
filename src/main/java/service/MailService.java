package service;

import java.io.InputStream;

public interface MailService {

    String sendFile(String to, String fileName, String mimeType, InputStream fileStream);
}
