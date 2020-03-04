package service.impl;

import dao.UserEmailDao;
import entity.UserEmail;
import service.UserEmailService;

public class UserEmailServiceImpl implements UserEmailService {

    private final UserEmailDao userEmailDao;

    public UserEmailServiceImpl() {
        userEmailDao = new UserEmailDao();
    }

    @Override
    public boolean userEmailExists(long chatId) {
        return userEmailDao.get(chatId).isPresent();
    }

    @Override
    public String getEmail(long chatId) {
        return userEmailDao.get(chatId).map(UserEmail::getEmail).get();
    }

    @Override
    public String getEmailInfo(long chatId) {
        return userEmailDao.get(chatId)
                .map(userEmail -> String.format("Current email address of your Kindle — `%s`", userEmail.getEmail()))
                .orElse("Sorry... I forgot your email address. Try to set it one more time");
    }

    @Override
    public String setEmail(long chatId, String email) {
        boolean isSet = userEmailDao
                .get(chatId)
                .map(userEmail -> {
                    userEmail.setEmail(email);
                    return userEmailDao.update(userEmail);
                })
                .orElseGet(() -> userEmailDao.insert(new UserEmail(chatId, email)));
        return isSet ? "Email was successfully changed" :
                "An error occurred while saving email";
    }

}
