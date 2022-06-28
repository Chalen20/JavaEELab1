import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.exceptions.UserNotFoundException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.model.User;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.service.UserService;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes={UserService.class, UserRepository.class})
class UserServiceTest {

    @SpyBean
    private UserValidator userValidator;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createUserTest() {
        String login = "qwerty12";
        String password = "qwerty";
        String fullName = "qwerty1";
        userService.createNewUser(NewUser.builder().login(login)
                .password(password).fullName(fullName).build());
        Mockito.verify(userValidator).validateNewUser(ArgumentMatchers.any());
        assertThat(userRepository.isLoginExists(login)).isTrue();
    }

    @Test
    void getUserByLoginTest() {
        String login = "qwerty13";
        String password = "qwerty";
        String fullName = "qwerty1";
        NewUser newUser = NewUser.builder().login(login)
                .password(password).fullName(fullName).build();
        userService.createNewUser(newUser);
        User user = userService.getUserByLogin(login);
        assertThat(user.getFullName()).isEqualTo(fullName);
        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    void getUserByNoExistingLoginTest() {
        String login = "SomeLogin";
        assertThatThrownBy(() -> userService.getUserByLogin(login)).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void createDuplicateTest() {
        String login = "qwerty14";
        String password = "qwerty";
        String fullName = "qwerty12";
        NewUser newUser = NewUser.builder().login(login)
                .password(password).fullName(fullName).build();
        userService.createNewUser(newUser);
        assertThatThrownBy(() -> userService.createNewUser(newUser)).isInstanceOf(LoginExistsException.class);
    }

}