package fiveguys.innout.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JoinDto {
    private String username;
    private String email;
    private Date birthdate;
    private String password;
    private String passwordConfirm;

    public boolean checkPassword() {
        return this.password != null && this.password.equals(this.passwordConfirm);
    }
}
