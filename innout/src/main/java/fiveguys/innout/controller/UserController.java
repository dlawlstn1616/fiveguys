package fiveguys.innout.controller;

import fiveguys.innout.dto.joinDto;
import fiveguys.innout.dto.loginDto;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import fiveguys.innout.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/innout")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil; // JWT 유틸리티 추가

    @PostMapping("/join")
    public String join(@RequestBody joinDto joinDto) {
        if (userRepository.findByEmail(joinDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        if (!joinDto.checkPassword()) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = new User();
        user.setEmail(joinDto.getEmail());
        user.setName(joinDto.getUsername());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        user.setBirthDate(joinDto.getBirthDate());  // 생년월일 설정
        userRepository.save(user);

        return "User created successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody loginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtil.generateToken(userDetails);
    }
}
