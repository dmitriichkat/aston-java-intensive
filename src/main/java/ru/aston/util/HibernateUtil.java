package ru.aston.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {

    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();

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
            }

            SessionFactory factory = configuration.buildSessionFactory();
            log.info("SessionFactory created successfully");
            return factory;
        } catch (Exception e) {
            log.error("Creation of SessionFactory failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            log.info("SessionFactory closed successfully");
        }
    }
}
