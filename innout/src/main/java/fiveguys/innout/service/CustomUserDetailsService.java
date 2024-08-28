package fiveguys.innout.service;

import fiveguys.innout.entity.User;
import fiveguys.innout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            Long userId = Long.parseLong(username);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
        } catch (NumberFormatException e) {
            user = userRepository.findByEmail(username);

        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getId())) // ID 사용
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}

