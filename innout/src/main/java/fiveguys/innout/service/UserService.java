package fiveguys.innout.service;

import fiveguys.innout.dto.JoinDto;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입 로직
    public User registerUser(JoinDto joinDto) throws Exception {
        if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new Exception("이미 사용 중인 이메일입니다.");
        }
        if (!joinDto.getPassword().equals(joinDto.getPasswordConfirm())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setEmail(joinDto.getEmail());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword()));

        return userRepository.save(user);
    }

    // 이메일로 사용자 찾기
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    // 다른 사용자 관련 로직들 (업데이트, 삭제 등)
}
