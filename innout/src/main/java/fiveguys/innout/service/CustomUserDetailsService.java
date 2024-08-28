package fiveguys.innout.service;

import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
// Spring Security가 사용자의 정보를 가져올 수 있도록 하는 클래스
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .roles("USER") // 예를 들어 기본 역할로 "USER"를 설정
                .build();
    }
}
