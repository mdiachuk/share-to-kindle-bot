package service.impl;

import service.MailService;

import java.io.IOException;
import java.io.InputStream;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;

public class MailServiceImpl implements MailService {

    private final static String SENDER_EMAIL;
    private final static String SENDER_PASSWORD;

    static {
        SENDER_EMAIL = System.getenv("SENDER_EMAIL");
        SENDER_PASSWORD = System.getenv("SENDER_PASSWORD");
    }

    public MailServiceImpl() {
    }

    private Properties configureProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        return properties;
    }

    private Session configureSession() {
        return Session.getInstance(configureProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });
    }

    @Override
    public String sendFile(String to, String fileName, String mimeType, InputStream fileStream) {
        try {
            Message message = new MimeMessage(configureSession());
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(fileStream, mimeType);
            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(byteArrayDataSource));
            part.setFileName(fileName);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(part);

            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException | IOException e) {
            return "An error occurred while sending file.";
        }
        return "File was sent to your Kindle!";
    }
}
