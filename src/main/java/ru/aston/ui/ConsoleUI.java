package ru.aston.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.dao.UserDao;
import ru.aston.entity.User;
import ru.aston.exception.DaoException;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUI {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleUI.class);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final int NAME_MIN = 2;
    private static final int NAME_MAX = 100;
    private static final int AGE_MIN = 1;
    private static final int AGE_MAX = 150;

    private final UserDao userDao;
    private final Scanner scanner;

    public ConsoleUI(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        logger.info("Console UI started");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> createUser();
                    case "2" -> findById();
                    case "3" -> findAll();
                    case "4" -> updateUser();
                    case "5" -> deleteUser();
                    case "0" -> running = false;
                    default -> System.out.println("Неизвестная команда. Введите число из меню.");
                }
            } catch (DaoException e) {
                System.out.println("Ошибка при работе с БД: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error in UI", e);
                System.out.println("Непредвиденная ошибка. Подробности в логах.");
            }
        }
        logger.info("Console UI finished");
        System.out.println("До свидания!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== МЕНЮ =====");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти по ID");
        System.out.println("3. Показать всех");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private void createUser() {
        System.out.println("--- Создание пользователя ---");
        String name = readName("Имя: ");
        String email = readEmail("Email: ");
        int age = readAge("Возраст: ");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        User saved = userDao.save(user);
        System.out.println("Пользователь создан: " + saved);
    }

    private void findById() {
        Long id = readId("Введите ID: ");
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID=" + id + " не найден.");
        } else {
            System.out.println(user);
        }
    }

    private void findAll() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Список пуст.");
            return;
        }
        System.out.println("Всего пользователей: " + users.size());
        users.forEach(System.out::println);
    }

    private void updateUser() {
        System.out.println("--- Обновление пользователя ---");
        Long id = readId("Введите ID пользователя: ");
        User existing = userDao.findById(id);
        if (existing == null) {
            System.out.println("Пользователь с ID=" + id + " не найден.");
            return;
        }
        System.out.println("Текущие данные: " + existing);

        String name = readName("Новое имя [" + existing.getName() + "]: ", existing.getName());
        String email = readEmail("Новый email [" + existing.getEmail() + "]: ", existing.getEmail());
        int age = readAge("Новый возраст [" + existing.getAge() + "]: ", existing.getAge());

        existing.setName(name);
        existing.setEmail(email);
        existing.setAge(age);

        User updated = userDao.update(existing);
        System.out.println("Обновлено: " + updated);
    }

    private void deleteUser() {
        Long id = readId("Введите ID для удаления: ");
        boolean deleted = userDao.delete(id);
        if (deleted) {
            System.out.println("Пользователь с ID=" + id + " удалён.");
        } else {
            System.out.println("Пользователь с ID=" + id + " не найден.");
        }
    }

    private String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            String error = validateName(value);
            if (error == null) return value;
            System.out.println("! " + error);
        }
    }

    private String readName(String prompt, String current) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) return current;
            String error = validateName(value);
            if (error == null) return value;
            System.out.println("! " + error);
        }
    }

    private String validateName(String value) {
        if (value.isEmpty()) return "Имя не может быть пустым.";
        if (value.length() < NAME_MIN) return "Имя должно содержать минимум " + NAME_MIN + " символа.";
        if (value.length() > NAME_MAX) return "Имя должно содержать не более " + NAME_MAX + " символов.";
        return null;
    }

    private String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            String error = validateEmail(value);
            if (error == null) return value;
            System.out.println("! " + error);
        }
    }

    private String readEmail(String prompt, String current) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) return current;
            String error = validateEmail(value);
            if (error == null) return value;
            System.out.println("! " + error);
        }
    }

    private String validateEmail(String value) {
        if (value.isEmpty()) return "Email не может быть пустым.";
        if (!EMAIL_PATTERN.matcher(value).matches()) return "Некорректный формат email.";
        return null;
    }

    private int readAge(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            Integer age = parseAge(value);
            if (age != null) return age;
        }
    }

    private int readAge(String prompt, Integer current) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (value.isEmpty() && current != null) return current;
            Integer age = parseAge(value);
            if (age != null) return age;
        }
    }

    private Integer parseAge(String value) {
        if (value.isEmpty()) {
            System.out.println("! Возраст не может быть пустым.");
            return null;
        }
        try {
            int age = Integer.parseInt(value);
            if (age < AGE_MIN || age > AGE_MAX) {
                System.out.println("! Возраст должен быть от " + AGE_MIN + " до " + AGE_MAX + ".");
                return null;
            }
            return age;
        } catch (NumberFormatException e) {
            System.out.println("! Возраст должен быть целым числом.");
            return null;
        }
    }

    private Long readId(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                long id = Long.parseLong(value);
                if (id <= 0) {
                    System.out.println("! ID должен быть положительным числом.");
                    continue;
                }
                return id;
            } catch (NumberFormatException e) {
                System.out.println("! ID должен быть целым числом.");
            }
        }
    }
}
