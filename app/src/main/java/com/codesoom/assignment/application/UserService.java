package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.UserModificationData;
import com.codesoom.assignment.dto.UserRegistrationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {
    private final Mapper mapper;
    private final UserRepository userRepository;

    public UserService(Mapper dozerMapper, UserRepository userRepository) {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationData registrationData) {
        String email = registrationData.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = mapper.map(registrationData, User.class);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        User user = findUserById(id);

        User source = mapper.map(modificationData, User.class);
        user.changeWith(source);

        return user;
    }

    public User deleteUser(Long id) {
        User user = findUserById(id);
        user.destroy();
        return user;
    }

    private User findUserById(Long id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원 식별자가 주어졌으므로 회원을 찾을 수 없습니다. " +
                        "문제의 id = " + id));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원 이메일이 주어졌으므로 회원을 찾을 수 없습니다. " +
                        "문제의 email = " + email));
    }
}
