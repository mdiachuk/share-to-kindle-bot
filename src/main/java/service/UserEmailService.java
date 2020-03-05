package service;

import java.util.Optional;

public interface UserEmailService {

    Optional<String> getEmail(long chatId);
    boolean setEmail(long chatId, String email);
}
