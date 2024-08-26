package fiveguys.innout.service;

import fiveguys.innout.dto.JoinDto;
import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(JoinDto joinDto) throws Exception {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(joinDto.getEmail())) {
            throw new Exception("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호와 비밀번호 확인 일치 여부 확인
        if (!joinDto.getPassword().equals(joinDto.getPasswordConfirm())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 생성 및 저장
        User user = new User();
        user.setUsername(joinDto.getUsername());
        user.setEmail(joinDto.getEmail());
        user.setPassword(passwordEncoder.encode(joinDto.getPassword())); // 비밀번호 암호화
        user.setBirthdate(joinDto.getBirthdate());

        return userRepository.save(user);
    }
}
