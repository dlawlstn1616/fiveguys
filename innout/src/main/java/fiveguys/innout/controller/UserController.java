package fiveguys.innout.controller;

import fiveguys.innout.dto.joinDto;
import fiveguys.innout.dto.loginDto;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import fiveguys.innout.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
        user.setBirthDate(joinDto.getBirthDate());
        user.setGender(joinDto.getGender());
// 생년월일 설정
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

    @PutMapping("/{id}/change-password")
    public String changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return "Password changed successfully";
    }

    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String username = request.get("username");

        Optional<User> userOptional = userRepository.findByEmailAndName(email, username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 임시 비밀번호 생성
            String tempPassword = UUID.randomUUID().toString().substring(0, 8); // 임의의 8자리 비밀번호 생성
            user.setPassword(passwordEncoder.encode(tempPassword)); // 비밀번호 암호화 후 저장
            userRepository.save(user);

            return ResponseEntity.ok(tempPassword); // 임시 비밀번호를 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }



}
