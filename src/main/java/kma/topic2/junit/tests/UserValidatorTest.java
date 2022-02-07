package kma.topic2.junit.tests;

import kma.topic2.junit.exceptions.ConstraintViolationException;
import kma.topic2.junit.exceptions.LoginExistsException;
import kma.topic2.junit.model.NewUser;
import kma.topic2.junit.repository.UserRepository;
import kma.topic2.junit.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateNewUserValidTest() {
        String login = "qwerty12";
        String password = "qwerty";
        String fullName = "qwerty1";
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        assertThatCode(() -> userValidator.validateNewUser(NewUser.builder().login(login)
                .password(password).fullName(fullName).build())).doesNotThrowAnyException();
    }

    @Test
    void validateNewUserNotValidTest() {
        String login = "qwerty12";
        String password = "qwerty";
        String fullName = "qwerty1";
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(true);
        assertThatThrownBy(() -> userValidator.validateNewUser(NewUser.builder().login(login)
                .password(password).fullName(fullName).build())).isInstanceOf(LoginExistsException.class);
    }

    @Test
    void validatePasswordLongTest() {
        String login = "qwerty12";
        String password = "qwertysdgjdssdsd";
        String fullName = "qwerty1";
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        ConstraintViolationException throwable = catchThrowableOfType(() -> userValidator.validateNewUser(
                (NewUser.builder().login(login)
                        .password(password).fullName(fullName).build())), ConstraintViolationException.class);
        assertThat(throwable.getErrors()).containsExactly("Password has invalid size");
    }

    @Test
    void validatePasswordRegexTest() {
        String login = "qwerty12";
        String password = "qwer-t";
        String fullName = "qwerty1";
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        ConstraintViolationException throwable = catchThrowableOfType(() -> userValidator.validateNewUser(
                (NewUser.builder().login(login)
                        .password(password).fullName(fullName).build())), ConstraintViolationException.class);
        assertThat(throwable.getErrors()).containsExactly("Password doesn't match regex");
    }

    @Test
    void validatePasswordRegexAndLongTest() {
        String login = "qwerty12";
        String password = " ";
        String fullName = "qwerty1";
        Mockito.when(userRepository.isLoginExists(ArgumentMatchers.anyString())).thenReturn(false);
        ConstraintViolationException throwable = catchThrowableOfType(() -> userValidator.validateNewUser(
                (NewUser.builder().login(login)
                        .password(password).fullName(fullName).build())), ConstraintViolationException.class);
        assertThat(throwable.getErrors()).containsExactly("Password has invalid size", "Password doesn't match regex");
    }
}