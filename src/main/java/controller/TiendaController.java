package controller;

import com.google.gson.Gson;
import dao.ProductDAO;
import dao.UserDAO;
import model.Product;
import model.ProductResponse;
import model.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TiendaController {

    private UserDAO userDAO;
    private ProductDAO productDAO;

    public TiendaController() {
        userDAO = new UserDAO();
        productDAO = new ProductDAO();
    }

    // PRODUCT METHODS

    public void importProducts() {
        HttpClient client = null;
        Gson gson = new Gson();
        try {
            client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://dummyjson.com/products"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ProductResponse productResponse = gson.fromJson(response.body(), ProductResponse.class);
            productDAO.insertProducts(productResponse.getProducts());
        } catch (Exception e) {
            System.out.println("Connection failed when importing products: " + e.getMessage());
        }
    }

    public void listAllProducts() {
        List<Product> products = productDAO.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products available.");
        } else {
            products.forEach(p -> System.out.println("ID: " + p.getId() + " | Name: " + p.getTitle() + " | Price: "
                    + p.getPrice() + "€ | Stock: " + p.getStock()));
        }
    }

    public boolean sellProduct(String email, String pass, int idProduct) {
        // SINGLE ATOMIC TRANSACTION
        Session session = null;
        Transaction transaction = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            // 1. Verify credentials and fetch user in this session
            User sessionUser;
            try {
                sessionUser = session
                        .createQuery("FROM User u WHERE u.email = :email AND u.password = :pass", User.class)
                        .setParameter("email", email)
                        .setParameter("pass", pass)
                        .getSingleResult();
            } catch (Exception e) {
                System.out.println("Invalid credentials.");
                transaction.rollback();
                return false;
            }

            // 2. Fetch product in the same session
            Product product = session.find(Product.class, (long) idProduct);

            // 3. VALIDATIONS
            if (product == null) {
                System.out.println("Product not found.");
                transaction.rollback();
                return false;
            }
            if (product.getStock() <= 0) {
                System.out.println("Out of stock for: " + product.getTitle());
                transaction.rollback();
                return false;
            }
            if (sessionUser.getWallet() < product.getPrice()) {
                System.out.println("Insufficient funds. You have " + sessionUser.getWallet()
                        + " Euros but the product costs " + product.getPrice() + " Euros.");
                transaction.rollback();
                return false;
            }

            // 4. EXECUTION
            // Subtract stock and balance in memory
            product.setStock(product.getStock() - 1);
            sessionUser.setWallet(sessionUser.getWallet() - product.getPrice());

            // Prepare changes for the database
            session.merge(product);
            session.merge(sessionUser);

            transaction.commit();
            System.out.println("Purchase successful: " + product.getTitle());
            System.out.println("Remaining balance: " + sessionUser.getWallet() + " Euros");
            return true;

        } catch (Exception e) {
            // If anything fails, rollback everything
            if (transaction != null)
                transaction.rollback();
            System.out.println("Transaction error. Rollback executed.");
            e.printStackTrace();
            return false;
        } finally {
            if (session != null)
                session.close();
        }
    }

    // USER METHODS

    public void seedDatabase() {
        userDAO.seedDatabase();
    }

    public User login(String email, String password) {
        return userDAO.login(email, password);
    }

    public Double getWalletBalance(Long userId) {
        return userDAO.getWalletBalance(userId);
    }

    public model.Profile getProfileById(Long id) {
        return userDAO.getProfileById(id);
    }

    public boolean signInUser(User user) {
        return userDAO.insertUser(user);
    }

    public void findUserByEmail(String email) {
        User user = userDAO.getUserByEmail(email);
        if (user != null) {
            user.showData();
        } else {
            System.out.println("No user found with email: " + email);
        }
    }

    public void listAdmins() {
        List<User> users = userDAO.getUsersByProfile(1); // 1 = Admin
        if (users.isEmpty()) {
            System.out.println("No admins available.");
        } else {
            users.forEach(User::showData);
        }
    }

    public void listCustomers() {
        List<User> users = userDAO.getUsersByProfile(2); // 2 = Customer
        if (users.isEmpty()) {
            System.out.println("No customers available.");
        } else {
            users.forEach(User::showData);
        }
    }

    public void deleteUser(Long id) {
        if (userDAO.deleteUser(id)) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Delete failed: User not found.");
        }
    }

    public void findUsers(String name) {
        List<User> lista = userDAO.getUsersByName(name);
        if (!lista.isEmpty()) {
            lista.forEach(User::showData);
        } else {
            System.out.println("No users found matching your criteria.");
        }
    }

    public void updateUserDetails(Long id, String name, String lastName, String email) {
        User user = userDAO.updateUserById(id, name, lastName, email);
        if (user != null) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("Update failed: User not found.");
        }
    }
}
