package kg.kadyrbekov.services;


import kg.kadyrbekov.model.User;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (username.matches(".*@.*")) {
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email - %s, not found", username)));
        } else {
            user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("User with phone number - %s, not found", username)));
        }
        return user;
    }
//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(username).get();
//        if (user != null) {
//            return user;
//        } else {
//            throw new UsernameNotFoundException(format("User with email - %s, not found", username));
//        }
//    }

}
