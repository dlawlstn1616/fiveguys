package fiveguys.innout.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class joinDto {
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;

    public boolean checkPassword() {
        return this.password != null && this.password.equals(this.passwordConfirm);
    }
}
