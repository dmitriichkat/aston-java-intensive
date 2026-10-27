package ru.aston.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.aston.entity.User;
import ru.aston.exception.DaoException;
import ru.aston.exception.DuplicateEmailException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void beforeAll() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", POSTGRES.getDriverClassName());
        configuration.setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", POSTGRES.getUsername());
        configuration.setProperty("hibernate.connection.password", POSTGRES.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", false);
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.createMutationQuery("delete from User").executeUpdate();
            transaction.commit();
        }
    }

    private static User createUser(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return user;
    }

    private User userExist(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new AssertionError("No user with id = " + id));
    }

    @Nested
    @DisplayName("Save operation tests")
    class CreateUser {

        @Test
        @DisplayName("User successfully created, added to db")
        void returnUserWhenCreated() {
            User user = createUser("Ivan", "ivan@example.com", 20);
            assertNull(user.getId());
            User saved = userDao.save(user);
            assertNotNull(saved);
            assertNotNull(saved.getId());
            assertNotNull(user.getId());
            assertAll(
                    () -> assertSame(user, saved),
                    () -> assertEquals(1, userDao.findAll().size())
            );
            User fromDb = userExist(saved.getId());
            assertAll(
                    () -> assertEquals(saved.getId(), fromDb.getId()),
                    () -> assertEquals("Ivan", fromDb.getName()),
                    () -> assertEquals("ivan@example.com", fromDb.getEmail()),
                    () -> assertEquals(20, fromDb.getAge())
            );
        }

        @Test
        @DisplayName("User with duplicate email throws duplicate exception, not added to db")
        void throwDuplicateEmailExceptionWhenCreating() {
            User firstUser = createUser("Ivan", "ivan@example.com", 20);
            User secondUser = createUser("Alex", "ivan@example.com", 30);
            User firstSaved = assertDoesNotThrow(() -> userDao.save(firstUser));
            assertAll(
                    () -> assertThrows(DuplicateEmailException.class, () -> userDao.save(secondUser)),
                    () -> assertEquals(1, userDao.findAll().size()),
                    () -> assertEquals(firstSaved.getId(), userDao.findAll().get(0).getId())
            );
        }

        @Test
        @DisplayName("User with invalid data throws dao exception, not added to db")
        void throwDaoExceptionWhenCreatingWithInvalidData() {
            User user = createUser(null, null, null);
            assertThrows(DaoException.class, () -> userDao.save(user));
        }
    }

    @Nested
    @DisplayName("Find All operation tests")
    class FindAll {

        @Test
        @DisplayName("Successfully found users from db")
        void returnUsersListWhenTableAccessible() {
            User firstUser = createUser("Ivan", "ivan@example.com", 20);
            User secondUser = createUser("Alex", "alex@example.com", 20);
            User firstSaved = userDao.save(firstUser);
            User secondSaved = userDao.save(secondUser);
            List<User> users = userDao.findAll();

            assertEquals(2, users.size());
            assertAll(
                    () -> assertEquals(firstSaved.getId(), users.get(0).getId()),
                    () -> assertEquals(firstSaved.getName(), users.get(0).getName()),
                    () -> assertEquals(firstSaved.getEmail(), users.get(0).getEmail()),
                    () -> assertEquals(firstSaved.getAge(), users.get(0).getAge()),
                    () -> assertEquals(secondSaved.getId(), users.get(1).getId()),
                    () -> assertEquals(secondSaved.getName(), users.get(1).getName()),
                    () -> assertEquals(secondSaved.getEmail(), users.get(1).getEmail()),
                    () -> assertEquals(secondSaved.getAge(), users.get(1).getAge())
            );
        }

        @Test
        @DisplayName("Return empty list when nothing saved")
        void returnEmptyListWhenNothingSaved() {
            assertTrue(userDao.findAll().isEmpty());
        }
    }

    @Nested
    @DisplayName("Find by Id operation tests")
    class FindById {

        @Test
        @DisplayName("Successfully found user from db")
        void returnUserWhenFound() {
            User user = createUser("Ivan", "ivan@example.com", 20);
            User saved = userDao.save(user);
            User fromDb = userExist(saved.getId());
            assertAll(
                    () -> assertEquals(saved.getId(), fromDb.getId()),
                    () -> assertEquals("Ivan", fromDb.getName()),
                    () -> assertEquals("ivan@example.com", fromDb.getEmail()),
                    () -> assertEquals(20, fromDb.getAge())
            );
        }

        @Test
        @DisplayName("Optional empty when no user in db")
        void returnNullWhenUserNotFound() {
            assertTrue(userDao.findById(Long.MAX_VALUE).isEmpty());
        }
    }
}
