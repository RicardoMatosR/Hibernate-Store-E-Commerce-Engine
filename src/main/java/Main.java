import controller.TiendaController;
import database.HibernateUtil;
import model.Profile;
import model.User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TiendaController controller = new TiendaController();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        User currentUser = null;

        controller.seedDatabase();

        System.out.println("================================================");
        System.out.println("        HIBERNATE STORE DUMMY PRODUCTS          ");
        System.out.println("================================================");

        while (!exit) {
            try {
                //PHASE 1: USER NOT LOGGED
                if (currentUser == null) {
                    System.out.println("\n--- WELCOME GATEWAY ---");
                    System.out.println("1. Login");
                    System.out.println("2. Register (Customer)");
                    System.out.println("3. Exit");
                    System.out.print("Choose an option: ");

                    String option = scanner.nextLine();

                    switch (option) {
                        case "1":
                            System.out.print("Email: ");
                            String loginEmail = scanner.nextLine();
                            System.out.print("Password: ");
                            String loginPass = scanner.nextLine();

                            currentUser = controller.login(loginEmail, loginPass);
                            if (currentUser != null) {
                                String pName = (currentUser.getProfile() != null) ? currentUser.getProfile().getName() : "Unknown Role";
                                System.out.println("Welcome back, " + currentUser.getName() + " (" + pName + ")");
                            } else {
                                System.out.println("Invalid credentials.");
                            }
                            break;

                        case "2":
                            System.out.println("\n--- CUSTOMER REGISTRATION ---");
                            System.out.print("First Name: ");
                            String firstName = scanner.nextLine();
                            System.out.print("Last Name: ");
                            String lastName = scanner.nextLine();
                            System.out.print("Email: ");
                            String email = scanner.nextLine();
                            System.out.print("Password: ");
                            String pass = scanner.nextLine();

                            Profile defaultProfile = controller.getProfileById(2L); // 2 = Customer
                            User newUser = new User(firstName, lastName, email, pass, defaultProfile);
                            newUser.setWallet(1000.00);

                            boolean successRegister = controller.signInUser(newUser);

                            if (successRegister) {
                                System.out.println("Account created. Please Login to continue.");
                            } else {
                                System.out.println("Registration failed. Email might already be in use.");
                            }
                            break;

                        case "3":
                            System.out.println("\nShutting down Hibernate connections...");
                            exit = true;
                            break;
                        default:
                            System.out.println("Invalid option.");
                    }
                }
                // --- PHASE 2: USER LOGGED ---
                else {
                    String role = (currentUser.getProfile() != null) ? currentUser.getProfile().getName() : "Customer";
                    System.out.println("\n--- " + role.toUpperCase() + " DASHBOARD ---");

                    if (role.equals("Admin")) {
                        System.out.println("1. Import products from API (DummyJSON)");
                        System.out.println("2. Search user by exact name");
                        System.out.println("3. Search user by email");
                        System.out.println("4. Update user details (Customers only)");
                        System.out.println("5. List Admins");
                        System.out.println("6. List Customers");
                        System.out.println("7. Delete a user (Customers only)");
                        System.out.println("8. Logout");
                        System.out.print("Choose an action: ");

                        String option = scanner.nextLine();
                        switch (option) {
                            case "1":
                                System.out.println("\n[INFO] Connecting to API and fetching stock...");
                                controller.importProducts();
                                System.out.println("Products successfully synced.");
                                break;
                            case "2":
                                System.out.print("Enter exact first name to search: ");
                                String searchName = scanner.nextLine();
                                controller.findUsers(searchName);
                                break;
                            case "3":
                                System.out.print("Enter email to search: ");
                                String searchEmail = scanner.nextLine();
                                controller.findUserByEmail(searchEmail);
                                break;
                            case "4":
                                System.out.print("Enter User ID to update: ");
                                Long updateId = Long.parseLong(scanner.nextLine());
                                System.out.print("Enter new First Name (leave blank to keep current): ");
                                String newName = scanner.nextLine();
                                System.out.print("Enter new Last Name (leave blank to keep current): ");
                                String newLastName = scanner.nextLine();
                                System.out.print("Enter new Email (leave blank to keep current): ");
                                String newEmail = scanner.nextLine();
                                controller.updateUserDetails(updateId, newName, newLastName, newEmail);
                                break;
                            case "5":
                                controller.listAdmins();
                                break;
                            case "6":
                                controller.listCustomers();
                                break;
                            case "7":
                                System.out.print("Enter User ID to delete: ");
                                Long deleteId = Long.parseLong(scanner.nextLine());
                                controller.deleteUser(deleteId);
                                break;
                            case "8":
                                currentUser = null;
                                System.out.println("Logged out successfully.");
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    else if (role.equals("Customer")) {
                        System.out.println("1. Buy a product");
                        System.out.println("2. Check my Wallet Balance");
                        System.out.println("3. Logout");
                        System.out.print("Choose an action: ");

                        String option = scanner.nextLine();
                        switch (option) {
                            case "1":
                                controller.listAllProducts();
                                System.out.print("Product ID to purchase (e.g., 1, 2, 3...): ");
                                int productId = Integer.parseInt(scanner.nextLine());
                                controller.sellProduct(currentUser.getEmail(), currentUser.getPassword(), productId);
                                break;
                            case "2":
                                Double balance = controller.getWalletBalance(currentUser.getId());
                                System.out.println("Current Wallet Balance: " + balance + " Euros");
                                break;
                            case "3":
                                currentUser = null;
                                System.out.println("Logged out successfully.");
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid number format.");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                currentUser = null;
            }
        }

        scanner.close();
        HibernateUtil.shutdown();
        System.out.println("Goodbye! System safely closed.");
    }
}