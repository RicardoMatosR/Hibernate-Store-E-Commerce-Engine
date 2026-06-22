package dao;

import database.HibernateUtil;
import model.Profile;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

// Data Access Object for User entities.
// All session management uses try-with-resources to guarantee connection release
// regardless of exceptions. Write operations are wrapped in explicit transactions.
public class UserDAO {

    private final SessionFactory sessionFactory;

    public UserDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    // Persists a new User in the database inside an atomic transaction.
    // Returns true if committed successfully, false on any error (e.g. duplicate email).
    public boolean insertUser(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("[UserDAO] Error inserting user: " + e.getMessage());
            return false;
        }
    }

    // Finds a single User by primary key. Read-only; no transaction needed.
    // Returns the User, or null if not found.
    public User getUserById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(User.class, id);
        } catch (Exception e) {
            System.out.println("[UserDAO] Error fetching user by ID: " + e.getMessage());
            return null;
        }
    }

    // Finds a single User by email (unique key lookup).
    // Returns the User, or null if not found.
    public User getUserByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            System.out.println("[UserDAO] Error fetching user by email: " + e.getMessage());
            return null;
        }
    }

    // Returns all Users whose first name exactly matches the given string.
    // Returns a List of matching Users, or an empty list if none found.
    public List<User> getUsersByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User u WHERE u.name = :name", User.class)
                    .setParameter("name", name)
                    .getResultList();
        } catch (Exception e) {
            System.out.println("[UserDAO] Error fetching users by name: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Returns all Users belonging to a given Profile (role).
    public List<User> getUsersByProfile(int profileId) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Profile profile = session.find(Profile.class, profileId);
            List<User> users = (profile != null) ? profile.getUsers() : Collections.emptyList();
            transaction.commit();
            return users;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("[UserDAO] Error fetching users by profile: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Updates the editable fields of a User identified by their ID.
    // Any field passed as null or blank is skipped (partial update support).
    // Returns the updated User, or null if the ID does not exist.
    public User updateUserById(Long id, String newName, String newLastName, String newEmail) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user == null) {
                transaction.rollback();
                return null;
            }
            if (user.getProfile() != null && "Admin".equalsIgnoreCase(user.getProfile().getName())) {
                transaction.rollback();
                System.out.println("[UserDAO] Protected: Cannot modify Admin accounts.");
                return null;
            }
            if (newName     != null && !newName.trim().isEmpty())     user.setName(newName);
            if (newLastName != null && !newLastName.trim().isEmpty()) user.setLastName(newLastName);
            if (newEmail    != null && !newEmail.trim().isEmpty())    user.setEmail(newEmail);
            session.merge(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("[UserDAO] Error updating user: " + e.getMessage());
            return null;
        }
    }

    // Extra Operations

    public User login(String email, String password) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User u WHERE u.email = :email AND u.password = :pass", User.class)
                    .setParameter("email", email)
                    .setParameter("pass", password)
                    .getSingleResult();
        } catch (Exception e) {
            return null; // Invalid credentials or not found
        }
    }

    public Double getWalletBalance(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, userId);
            return (user != null) ? user.getWallet() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public Profile getProfileById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(Profile.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public void seedDatabase() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Long countProfiles = session.createQuery("SELECT count(p) FROM Profile p", Long.class).getSingleResult();
            Profile adminProfile = null;
            if (countProfiles == 0) {
                System.out.println("[SYSTEM] Initializing default roles...");
                adminProfile = new Profile(); 
                adminProfile.setName("Admin");
                Profile customerProfile = new Profile(); 
                customerProfile.setName("Customer");
                session.persist(adminProfile);
                session.persist(customerProfile);
            } else {
                adminProfile = session.find(Profile.class, 1L);
            }

            Long countAdmin = session.createQuery("SELECT count(u) FROM User u WHERE u.email = 'admin@ces.com'", Long.class).getSingleResult();
            if (countAdmin == 0 && adminProfile != null) {
                System.out.println("[SYSTEM] Creating default SuperAdmin account...");
                User admin = new User("Super", "Admin", "admin@ces.com", "admin123", adminProfile);
                session.persist(admin);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("[SYSTEM ERROR] Failed to seed database: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            System.out.println("[UserDAO] Error fetching all users: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean deleteUser(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                if (user.getProfile() != null && "Admin".equalsIgnoreCase(user.getProfile().getName())) {
                    transaction.rollback();
                    System.out.println("[UserDAO] Protected: Cannot delete Admin accounts.");
                    return false;
                }
                session.remove(user);
                transaction.commit();
                return true;
            }
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("[UserDAO] Error deleting user: " + e.getMessage());
            return false;
        }
    }
}
