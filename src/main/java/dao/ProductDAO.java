package dao;

import database.HibernateUtil;
import model.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Collections;
import java.util.List;

public class ProductDAO {

    private final SessionFactory sessionFactory;

    public ProductDAO() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public void insertProducts(List<Product> list) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (Product item : list) {
                session.merge(item);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("Error inserting product list: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Product", Product.class).getResultList();
        } catch (Exception e) {
            System.out.println("Error fetching all products: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
