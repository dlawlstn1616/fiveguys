package fiveguys.innout.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class joinDto {
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    private Date birthDate;  // 생년월일 필드 추가
    private String gender;

    public boolean checkPassword() {
        return this.password != null && this.password.equals(this.passwordConfirm);
    }
}
