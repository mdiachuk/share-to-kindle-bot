package service.impl;

import dao.UserEmailDao;
import entity.UserEmail;
import service.UserEmailService;

import java.util.Optional;

public class UserEmailServiceImpl implements UserEmailService {

    private final UserEmailDao userEmailDao;

    public UserEmailServiceImpl() {
        userEmailDao = new UserEmailDao();
    }

    @Override
    public Optional<String> getEmail(long chatId) {
        return userEmailDao.get(chatId).map(UserEmail::getEmail);
    }

    @Override
    public boolean setEmail(long chatId, String email) {
        return userEmailDao.get(chatId)
                .map(userEmail -> {
                    userEmail.setEmail(email);
                    return userEmailDao.update(userEmail);
                })
                .orElseGet(() -> userEmailDao.insert(new UserEmail(chatId, email)));
    }

}
