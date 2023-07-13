package kg.kadyrbekov.services;


import kg.kadyrbekov.model.User;
import kg.kadyrbekov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Optional;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userByEmail = userRepository.findByEmail(username);
        Optional<User> userByPhoneNumber = userRepository.findByPhoneNumber(username);

        User user = userByEmail.orElseGet(() -> userByPhoneNumber.orElse(null));

        if (user != null) {
            return user;
        } else {
            throw new UsernameNotFoundException(String.format("User with email or phone number - %s, not found", username));
        }

    }
}
