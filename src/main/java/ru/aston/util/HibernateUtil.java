package ru.aston.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.exception.InitializationException;

public final class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");
            if (user != null) {
                configuration.setProperty("hibernate.connection.username", user);
            }
            if (password != null) {
                configuration.setProperty("hibernate.connection.password", password);
            } else if (configuration.getProperty("hibernate.connection.password") == null){
                log.warn("Password is not set");
            }
            SessionFactory factory = configuration.buildSessionFactory();
            log.info("SessionFactory created successfully");
            return factory;
        } catch (Exception e) {
            log.error("Creation of SessionFactory failed", e);
            throw new InitializationException("Failed to connect to the database", e);
        }
    }
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory closed successfully");
        }
    }
}
