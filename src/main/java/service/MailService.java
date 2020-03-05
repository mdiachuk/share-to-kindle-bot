package service;

import java.io.InputStream;

public interface MailService {

    boolean sendFile(String to, String fileName, String mimeType, InputStream fileStream);
}
