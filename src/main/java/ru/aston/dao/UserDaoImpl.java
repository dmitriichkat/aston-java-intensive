package ru.aston.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.aston.entity.User;
import org.hibernate.Transaction;
import ru.aston.exception.DaoException;
import ru.aston.util.HibernateUtil;
import java.util.List;

public class UserDaoImpl implements UserDao{

    private final SessionFactory sessionFactory;

    public UserDaoImpl() {
        this(HibernateUtil.getSessionFactory());
    }

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User save(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("User with Id = {} created", user.getId());
            return user;
        } catch (RuntimeException e) {
            rollback(transaction);
            logger.error("Failed to save new user", e);
            throw new DaoException("Failed to save new user", e);
        }
    }

    @Override
    public List<User> findAll() {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            List<User> users = session.createQuery("from User order by id", User.class).list();
            transaction.commit();
            return users;
        } catch (RuntimeException e) {
            rollback(transaction);
            logger.error("Failed to get list of all users", e);
            throw new DaoException("Failed to get list of all users", e);
        }
    }

    @Override
    public User findById(Long id) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            transaction.commit();
            return user;
        } catch (RuntimeException e) {
            rollback(transaction);
            logger.error("Failed to find user with Id = {}", id, e);
            throw new DaoException("Failed to find user with Id = " + id, e);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            logger.info("User with Id = {} updated", user.getId());
            return updatedUser;
        } catch (RuntimeException e) {
            rollback(transaction);
            logger.error("Failed to update user with Id = {}", user.getId(), e);
            throw new DaoException("Failed to update user with Id = " + user.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user == null) {
                transaction.commit();
                logger.warn("User with id={} not found", id);
                return false;
            }
            session.remove(user);
            transaction.commit();
            logger.info("User with Id = {} deleted", id);
            return true;
        } catch (RuntimeException e) {
            rollback(transaction);
            logger.error("Failed to delete user with Id = {}", id, e);
            throw new DaoException("Failed to delete user with Id =" + id, e);
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (RuntimeException e) {
                logger.error("Rollback failed", e);
            }
        }
    }
}
