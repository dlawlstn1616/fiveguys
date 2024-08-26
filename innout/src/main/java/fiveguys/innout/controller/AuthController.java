package fiveguys.innout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import fiveguys.innout.dto.LoginDto;
import fiveguys.innout.dto.JoinDto;
import fiveguys.innout.dto.JwtAuthenticationDto;
import fiveguys.innout.security.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationDto(jwt));
    }

    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestBody JoinDto joinRequest) {
        // 사용자 등록 로직을 추가하세요.
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // 클라이언트 측에서 JWT 삭제를 처리하도록 구현할 수 있습니다.
        return ResponseEntity.ok().build();
    }
}
