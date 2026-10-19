package ru.aston;

import ru.aston.dao.UserDao;
import ru.aston.dao.UserDaoImpl;
import ru.aston.ui.ConsoleUI;
import ru.aston.util.HibernateUtil;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserDao userDao = new UserDaoImpl();
            new ConsoleUI(userDao, scanner).start();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}
