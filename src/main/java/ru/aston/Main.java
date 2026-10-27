package ru.aston;

import ru.aston.service.UserService;
import ru.aston.service.UserServiceImpl;
import ru.aston.ui.ConsoleUI;
import ru.aston.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserService userService = new UserServiceImpl();
            new ConsoleUI(userService, scanner).start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
