import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;

public class finalProject {
    private static Scanner sc = new Scanner(System.in);
    private static List<String> username = new ArrayList<>();
    private static List<String> password = new ArrayList<>();
    private static ArrayList<String> item = new ArrayList<>();
    private static ArrayList<Integer> quantities = new ArrayList<>();
    private static ArrayList<Double> prices = new ArrayList<>();

    private static String currentLoggedInUser = "Guest";
    private static final String USER_DATA_FILE = "users.txt";
    public static void main(String[] args) {
        loadUserData(); // Load user data when the program starts
        System.out.println("Welcome to Hannah's Pet Supplies Shop! Let's get started? (yes/no)"); 
        String startChoice = sc.nextLine();
        if (startChoice.equalsIgnoreCase("yes")) {
            userConfirm();
        } else {
            System.out.println("Okay, maybe next time!");
        }
    }

    public static void userConfirm() {
        while (true) {
            System.out.println("\nSelect an Option");
            System.out.println("1. Log in to your account");
            System.out.println("2. Create a new account (Sign up)");
            System.out.print("Enter the number of your choice: ");
            int option = -1;
            try {
                option = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number (1 or 2).");
                sc.nextLine();
                continue;
            }
            sc.nextLine();

            if (option == 1) {
                if (login()) {
                    cashierProcess();
                }
            } else if (option == 2) {
                signUp();
                cashierProcess();
            } else {
                System.out.println("Not a valid option, try again.");
            }
        }
    }

    public static void signUp() {
        System.out.println("\n----- Sign Up -----");
        while (true) {
            System.out.print("Choose a Username (5-15 letters/numbers): ");
            String signUpName = sc.nextLine();
            if (signUpName.length() >= 5 && signUpName.length() <= 15) {
                Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
                Matcher usernameMatcher = usernamePattern.matcher(signUpName);
                if (usernameMatcher.matches()) {
                    if (!username.contains(signUpName)) {
                        username.add(signUpName);
                        System.out.println("Great!'" + signUpName + "' is now taken.");
                        currentLoggedInUser = signUpName;
                        break;
                    } else {
                        System.out.println("Sorry, that username is already in use.");
                    }
                } else {
                    System.out.println("Your username can only have letters and numbers.");
                }
            } else {
                System.out.println("Your username needs to be between 5 and 15 characters long.");
            }
        }

        while (true) {
            System.out.print("Create a Password (8-20 chars, 1 uppercase, 1 lowercase, 1 number): ");
            String userPass = sc.nextLine();
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
            Pattern passwordPattern = Pattern.compile(passwordRegex);
            Matcher passwordMatcher = passwordPattern.matcher(userPass);
            if (passwordMatcher.matches()) {
                password.add(userPass);
                saveUserData(); // Save new user data after sign up
                return;
            } else {
                System.out.println("Password must be 8-20 characters and include uppercase, lowercase, and a number.");
            }
        }
    }

    public static boolean login() {
        System.out.println("\n--- Log In ---");
        while (true) {
            System.out.print("Enter your username: ");
            String enteredUsername = sc.nextLine();
            Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
            Matcher usernameMatcher = usernamePattern.matcher(enteredUsername);
            if (!usernameMatcher.matches()) {
                System.out.println("Use only alphanumeric characters.");
                continue;
            }

            System.out.print("Enter your password: ");
            String enteredPassword = sc.nextLine();

            for (int i = 0; i < username.size(); i++) {
                if (enteredUsername.equals(username.get(i))) {
                    if (enteredPassword.equals(password.get(i))) {
                        System.out.println("Welcome, " + enteredUsername + "!");
                        currentLoggedInUser = enteredUsername;
                        return true;
                    } else {
                        System.out.println("Incorrect password.");
                        break;
                    }
                }
            }

            System.out.println("Username not found or incorrect password.");
            System.out.print("Do you want to try logging in again? (yes/no): ");
            String tryAgain = sc.nextLine();
            if (tryAgain.equalsIgnoreCase("no")) {
                System.out.println("Exiting login.");
                return false;
            }
        }
    }

    public static void cashierProcess() {
        boolean continueCashierSession = true;
        while (continueCashierSession) {
            int choice = displayCashierOptions();
            switch (choice) {
                case 1:
                    addItem();
                    break;
                case 2:
                    updateItem();
                    break;
                case 3:
                    displayItems();
                    break;
                case 4:
                    removeItem();
                    break;
                case 5:
                    if (item.isEmpty()) {
                        System.out.println("Your cart is empty. Please add items before proceeding to payment.");
                    } else {
                        ZoneId transactionZone = ZoneId.of("Asia/Manila");
                        ZonedDateTime transactionDateTime = ZonedDateTime.now(transactionZone);
                        double total = generateTransaction();
                        processPayment(total, currentLoggedInUser, transactionDateTime);
                        continueCashierSession = false;
                    }
                    break;
                case 6:
                    cancelCurrentOrder();
                    continueCashierSession = false;
                    break;
                case 7:
                    logOut();
                    continueCashierSession = false;
                    break;
                default:
                    System.out.println("That's not a valid option. Please try again.");
            }
        }
    }

    private static int displayCashierOptions() {
        System.out.println("\n--- Cashier Options ---");
        System.out.println("1. Add Product to Cart");
        System.out.println("2. Update Product in Cart");
        System.out.println("3. View Cart");
        System.out.println("4. Remove Product from Cart");
        System.out.println("5. Proceed to Checkout");
        System.out.println("6. Cancel Current Order");
        System.out.println("7. Log Out");
        System.out.print("Enter your option: ");
        int choice = -1;
        try {
            choice = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); 
        }
        return choice;
    }

    private static void addItem() {
        sc.nextLine(); 
        System.out.print("Enter item name: ");
        item.add(sc.nextLine());
        System.out.print("Enter quantity: ");
        int qty = -1;
        while (true) {
            try {
                qty = sc.nextInt();
                if (qty <= 0) {
                    System.out.println("Try again:");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println(" Please enter a number for quantity.");
                sc.nextLine(); 
            }
        }
        quantities.add(qty);

        System.out.print("Enter price: ");
        double price = -1.0;
        while (true) {
            try {
                price = sc.nextDouble();
                if (price <= 0) {
                    System.out.println("Price must be number.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number for price.");
                sc.nextLine(); 
            }
        }
        prices.add(price);
        System.out.println("Item added successfully!");
    }

    private static void removeItem() {
        sc.nextLine(); 
        System.out.print("Enter item name to remove: ");
        String productToRemove = sc.nextLine();
        int index = item.indexOf(productToRemove);
        if (index != -1) {
            item.remove(index);
            quantities.remove(index);
            prices.remove(index);
            System.out.println("Item '" + productToRemove + "' deleted from cart.");
        } else {
            System.out.println("Item '" + productToRemove + "' not found in cart.");
        }
    }

    private static void updateItem() {
        sc.nextLine(); // Consume newline
        System.out.print("Enter item name to update: ");
        String itemToUpdate = sc.nextLine();
        int index = item.indexOf(itemToUpdate);

        if (index != -1) {
            System.out.println("Found '" + itemToUpdate + "'. Current Quantity: " + quantities.get(index) + ", Price: Php " + prices.get(index));
            System.out.print("What would you like to update? (quantity/price/both): ");
            String updateChoice = sc.nextLine().toLowerCase();

            if (updateChoice.equals("quantity") || updateChoice.equals("both")) {
                System.out.print("Enter new quantity for '" + itemToUpdate + "': ");
                int newQty = -1;
                while (true) {
                    try {
                        newQty = sc.nextInt();
                        if (newQty <= 0) {
                            System.out.println("Quantity is invalid.");
                        } else {
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter a number for quantity. Try again:");
                        sc.nextLine(); 
                    }
                }
                quantities.set(index, newQty);
                System.out.println("Quantity updated.");
            }

            if (updateChoice.equals("price") || updateChoice.equals("both")) {
                System.out.print("Enter new price for '" + itemToUpdate + "': ");
                double newPrice = -1.0;
                while (true) {
                    try {
                        newPrice = sc.nextDouble();
                        if (newPrice <= 0) {
                            System.out.println("Price is invalid.");
                        } else {
                            break;
                        }
                    } catch (InputMismatchException e) {
                        System.out.println(" Please enter a number for price. Try again:");
                        sc.nextLine(); 
                    }
                }
                prices.set(index, newPrice);
                System.out.println("Price updated.");
            }
            System.out.println("Item '" + itemToUpdate + "' updated successfully!");
        } else {
            System.out.println("Item '" + itemToUpdate + "' not found in cart.");
        }
    }

    private static void displayItems() {
        if (item.isEmpty()) {
            System.out.println("\nYour cart is currently empty.");
            return;
        }

        System.out.println("\n----- Your Current Cart -----");
        System.out.printf("   %-15s %-10s %-10s %-10s%n", "Product", "Qty", "Price/Unit", "Total");
        System.out.println("---------------------------------");
        double currentSubtotal = 0.0;
        for (int i = 0; i < item.size(); i++) {
            String itemName = item.get(i);
            int quantity = quantities.get(i);
            double pricePerItem = prices.get(i);
            double itemTotal = quantity * pricePerItem;
            currentSubtotal += itemTotal;
            System.out.printf("   %-15s %-10d Php %-6.2f Php %.2f%n", itemName, quantity, pricePerItem, itemTotal);
        }
        System.out.println("---------------------------------");
        System.out.printf("Subtotal:                   Php %.2f%n", currentSubtotal);
        System.out.println("---------------------------------");
    }

    private static double generateTransaction() {
        double total = 0;
        for (int i = 0; i < item.size(); i++) {
            total += quantities.get(i) * prices.get(i);
        }
        return total;
    }

    private static void processPayment(double total, String cashier, ZonedDateTime transactionDateTime) {
        double payment;
        System.out.println("\n--- Proceed to Payment ---");
        System.out.println("Total Amount Due: Php " + String.format("%.2f", total));
        System.out.println("Payment Options:");
        System.out.println("1. Card");
        System.out.println("2. Cash");
        System.out.print("Choose option: ");
        int paymentOption = -1;
        try {
            paymentOption = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to Cash.");
            sc.nextLine(); 
            paymentOption = 2; 
        }
        sc.nextLine(); 
        switch (paymentOption) {
            case 1:
                System.out.println("You chose Card payment.");
                break;
            case 2:
                System.out.println("You chose Cash payment.");
                break;
            default:
                System.out.println("Invalid option. Defaulting to Cash.");
                break;
        }

        do {
            System.out.print("\nEnter amount paid: ");
            try {
                payment = sc.nextDouble();
                if (payment < total) {
                    System.out.println("Insufficient amount, try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                sc.nextLine(); 
                payment = 0; 
            }
        } while (payment < total);

        double change = payment - total;

        // printing receipt
        DateTimeFormatter transactionFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd,yyyy hh:mm:ss a ");
        String formattedTransactionDateTime = transactionDateTime.format(transactionFormatter);

        System.out.println("\n----- TRANSACTION RECEIPT -----");
        System.out.println("Date & Time: " + formattedTransactionDateTime);
        System.out.println("Customer:    " + cashier);
        System.out.println("---------------------------------");
        System.out.println("Items:");
        System.out.printf("   %-15s %-10s %-10s %-10s%n", "Product", "Qty", "Price/Unit", "Total");
        System.out.println("---------------------------------");

        for (int i = 0; i < item.size(); i++) {
            String itemName = item.get(i);
            int quantity = quantities.get(i);
            double pricePerItem = prices.get(i);
            double itemTotal = quantity * pricePerItem;
            System.out.printf("   %-15s %-10d Php %-6.2f Php %.2f%n", itemName, quantity, pricePerItem, itemTotal);
        }
        System.out.println("---------------------------------");
        System.out.printf("Subtotal:                   Php %.2f%n", total);
        System.out.printf("Payment:                    Php %.2f%n", payment);
        System.out.printf("Change:                     Php %.2f%n", change);
        System.out.println("---------------------------------");
        System.out.println("Thank you for your purchase!");


        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            writer.write("\n----- TRANSACTION RECEIPT -----\n");
            writer.write("Date & Time: " + formattedTransactionDateTime + "\n");
            writer.write("Cashier:     " + cashier + "\n");
            writer.write("Customer:    " + cashier + "\n");
            writer.write("---------------------------------\n");
            writer.write("Items:\n");
            writer.write(String.format("   %-15s %-10s %-10s %-10s%n", "Product", "Qty", "Price/Unit", "Total"));
            writer.write("---------------------------------\n");

            for (int i = 0; i < item.size(); i++) {
                String itemName = item.get(i);
                int quantity = quantities.get(i);
                double pricePerItem = prices.get(i);
                double itemTotal = quantity * pricePerItem;
                writer.write(String.format("   %-15s %-10d Php %-6.2f Php %.2f%n", itemName, quantity, pricePerItem, itemTotal));
            }
            writer.write("---------------------------------\n");
            writer.write(String.format("Subtotal:                   Php %.2f%n", total));
            writer.write(String.format("Payment:                    Php %.2f%n", payment));
            writer.write(String.format("Change:                     Php %.2f%n", change));
            writer.write("---------------------------------\n");
            writer.write("Thank you for your purchase!\n");
            writer.write("=================================================================\n\n");
            System.out.println("Transaction saved to transactions.txt");
        } catch (IOException e) {
            System.err.println("Error saving transaction to file: " + e.getMessage());
        }

        item.clear();
        quantities.clear();
        prices.clear();
    }

    private static void cancelCurrentOrder() {
        if (item.isEmpty()) {
            System.out.println("\nThere is no order to cancel.");
            return;
        }
        sc.nextLine(); // Consume newline
        System.out.print("Are you sure you want to cancel the current order? (yes/no): ");
        String confirmation = sc.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            item.clear();
            quantities.clear();
            prices.clear();
            System.out.println("Current order has been cancelled.");
        } else {
            System.out.println("Order cancellation.");
        }
    }

    private static void logOut() {
        item.clear();
        quantities.clear();
        prices.clear();
        currentLoggedInUser = "Guest";
        System.out.println("\nSuccessfully logged out. Redirecting to main menu.");
    }

    // saving and loading user data
    private static void saveUserData() {
        try (FileWriter writer = new FileWriter(USER_DATA_FILE)) {
            for (int i = 0; i < username.size(); i++) {
                writer.write(username.get(i) + "," + password.get(i) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private static void loadUserData() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            System.out.println("User data file not found. Starting with no registered users.");
            return;
        }
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    username.add(parts[0]);
                    password.add(parts[1]);
                }
            }
            System.out.println("User data loaded successfully.");
        } catch (FileNotFoundException e) {
            // This catch block might not be strictly necessary if we check file.exists()
            System.err.println("User data file not found (should not happen after check): " + e.getMessage());
        }
    }
}


