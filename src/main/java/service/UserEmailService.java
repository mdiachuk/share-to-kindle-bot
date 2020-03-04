package service;

public interface UserEmailService {

    boolean userEmailExists(long chatId);
    String getEmail(long chatId);
    String getEmailInfo(long chatId);
    String setEmail(long chatId, String email);
}
