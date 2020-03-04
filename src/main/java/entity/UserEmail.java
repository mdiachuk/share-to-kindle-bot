package entity;

public class UserEmail {

    long chatId;
    String email;

    public UserEmail() {
    }

    public UserEmail(long chatId, String email) {
        this.chatId = chatId;
        this.email = email;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
