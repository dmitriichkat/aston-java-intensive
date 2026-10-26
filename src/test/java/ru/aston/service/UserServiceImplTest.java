package ru.aston.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.aston.dao.UserDao;
import ru.aston.entity.User;
import ru.aston.exception.DaoException;
import ru.aston.exception.DuplicateEmailException;
import ru.aston.exception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
public class UserServiceImplTest {

    @Mock
    private UserDao userDao;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);
    }

    private static User user (Long id, String name, String email, int age){
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setAge(age);
        return u;
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {
        @Test
        void savesAndReturnsUser (){
            User newUser = user(1L, "John Doe", "john@gmail.com", 34);
            when(userDao.save(any(User.class))).thenReturn(newUser);

            User result = userService.saveUser("John Doe", "john@gmail.com", 34);

            assertSame(newUser, result);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).save(captor.capture());
            User passed = captor.getValue();
            assertNull(passed.getId());
            assertEquals("John Doe", passed.getName());
            assertEquals("john@gmail.com", passed.getEmail());
            assertEquals(34, passed.getAge());
            verifyNoMoreInteractions(userDao);
        }

        @Test
        void checksDuplicateEmailException() {
            when(userDao.save(any(User.class)))
                    .thenThrow(new DuplicateEmailException("User with this email already exists", null));

            assertThrows(DuplicateEmailException.class,
                    () -> userService.saveUser("John Doe", "john@gmail.com", 34));
        }

        @Test
        void checksDaoException() {
            when(userDao.save(any(User.class)))
                    .thenThrow(new DaoException("Failed to save new user", null));

            assertThrows(DaoException.class,
                    () -> userService.saveUser("John Doe", "john@gmail.com", 34));
        }

    }


    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        void returnsAllUsers() {
            List<User> users = List.of(
                    user(1L, "John Doe", "john@gmail.com", 34),
                    user(2L, "Jane Doe", "jane@gmail.com", 37));
            when(userDao.findAll()).thenReturn(users);

            List<User> result = userService.findAll();

            assertEquals(users, result);
            verify(userDao).findAll();
            verifyNoMoreInteractions(userDao);
        }

        @Test
        void returnsEmptyList() {
            when(userDao.findAll()).thenReturn(List.of());

            assertTrue(userService.findAll().isEmpty());
        }

        @Test
        void checksDaoException() {
            when(userDao.findAll()).thenThrow(new DaoException("Failed to get list of all users", null));

            assertThrows(DaoException.class, () -> userService.findAll());
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        void returnsUserIfFound() {
            User found = user(1L, "John Doe", "john@gmail.com", 34);
            when(userDao.findById(1L)).thenReturn(Optional.of(found));

            Optional<User> result = userService.findById(1L);

            assertTrue(result.isPresent());
            assertSame(found, result.get());
            verify(userDao).findById(1L);
            verifyNoMoreInteractions(userDao);
        }

        @Test
        void returnsEmptyIfNotFound() {
            when(userDao.findById(100L)).thenReturn(Optional.empty());

            assertTrue(userService.findById(100L).isEmpty());
        }

        @Test
        void checksDaoException() {
            when(userDao.findById(1L)).thenThrow(new DaoException("Failed to find user", null));

            assertThrows(DaoException.class, () -> userService.findById(1L));
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        void updatesAndReturnsExistingUser() {
            User existing = user(1L, "John Doe", "john@gmail.com", 34);
            User updated = user(1L, "Jane Doe", "jane@gmail.com", 37);
            when(userDao.findById(1L)).thenReturn(Optional.of(existing));
            when(userDao.update(existing)).thenReturn(updated);

            User result = userService.updateUser(1L, "Jane Doe", "jane@gmail.com", 37);

            assertSame(updated, result);
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userDao).update(captor.capture());
            User passed = captor.getValue();
            assertEquals(1L, passed.getId());
            assertEquals("Jane Doe", passed.getName());
            assertEquals("jane@gmail.com", passed.getEmail());
            assertEquals(37, passed.getAge());
        }

        @Test
        void throwsIfUserNotFound() {
            when(userDao.findById(100L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.updateUser(100L, "Jane Doe", "jane@gmail.com", 37));
            verify(userDao, never()).update(any(User.class));
        }

        @Test
        void checksDuplicateEmail() {
            User existing = user(1L, "John Doe", "john@gmail.com", 34);
            when(userDao.findById(1L)).thenReturn(Optional.of(existing));
            when(userDao.update(any(User.class)))
                    .thenThrow(new DuplicateEmailException("User with this email already exists", null));

            assertThrows(DuplicateEmailException.class,
                    () -> userService.updateUser(1L, "John Doe", "johnny@gmail.com", 34));
        }

        @Test
        void checksDaoExceptionOnFind() {
            when(userDao.findById(1L)).thenThrow(new DaoException("Failed to find user", null));

            assertThrows(DaoException.class,
                    () -> userService.updateUser(1L, "Jane Doe", "jane@gmail.com", 37));
            verify(userDao, never()).update(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        void returnsTrueIfUserDeleted() {
            when(userDao.delete(1L)).thenReturn(true);

            assertTrue(userService.deleteUser(1L));
            verify(userDao).delete(1L);
            verifyNoMoreInteractions(userDao);
        }

        @Test
        void returnsFalseIfUserNotFound() {
            when(userDao.delete(100L)).thenReturn(false);

            assertFalse(userService.deleteUser(100L));
        }

        @Test
        void checksDaoException() {
            when(userDao.delete(1L)).thenThrow(new DaoException("Failed to delete user", null));

            assertThrows(DaoException.class, () -> userService.deleteUser(1L));
        }
    }
}
