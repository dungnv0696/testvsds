package com.lifesup.gbtd.dto.object;

public class AccountEndCode {
    String username;
    String password;

    public AccountEndCode(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
