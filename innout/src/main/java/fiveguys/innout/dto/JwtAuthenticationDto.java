package fiveguys.innout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationDto {
    private String accessToken;

    public JwtAuthenticationDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
