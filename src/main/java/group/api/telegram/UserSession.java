package group.api.telegram;

public class UserSession {
    private boolean waitingForLogin = false;
    private boolean waitingForPassword = false;
    private String login;
    private String password;
    private String userRole;
    private Integer userId;
    
    public boolean isWaitingForLogin() { return waitingForLogin; }
    public void setWaitingForLogin(boolean waitingForLogin) { this.waitingForLogin = waitingForLogin; }
    
    public boolean isWaitingForPassword() { return waitingForPassword; }
    public void setWaitingForPassword(boolean waitingForPassword) { this.waitingForPassword = waitingForPassword; }
    
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public void reset() {
        waitingForLogin = false;
        waitingForPassword = false;
        login = null;
        password = null;
        userRole = null;
        userId = null;
    }
}