import java.io.*;
import java.util.*;

public class SupermarketBilling {

    static class Item {
        String name;
        int qty;
        int cost;

        Item(String name, int qty, int cost) {
            this.name = name;
            this.qty = qty;
            this.cost = cost;
        }
    }

    static HashMap<String, Integer> prices = new HashMap<>();
    static Queue<String> queue = new LinkedList<>();
    static ArrayList<String> bills = new ArrayList<>();
    static final String FILE_NAME = "bills.txt";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        // Price list (hashing)
        prices.put("Rice", 60);
        prices.put("Milk", 30);
        prices.put("Bread", 40);

        loadBills();

        while (true) {
            System.out.println("\n===== SUPERMARKET BILLING SYSTEM =====");
            System.out.println("1. Add customer");
            System.out.println("2. Bill next customer");
            System.out.println("3. View queue");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    billNextCustomer();
                    break;
                case 3:
                    viewQueue();
                    break;
                case 4:
                    saveBills();
                    System.out.println("Bills saved to " + FILE_NAME + ". Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter 1-4.");
            }
        }
    }

    // Adds a customer to the end of the queue (FIFO)
    static void addCustomer() {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        queue.add(name);
        System.out.println(name + " added to billing queue. Position: " + queue.size());
    }

    // Bills the customer at the front of the queue
    static void billNextCustomer() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No customer to bill.");
            return;
        }

        String customer = queue.poll();
        Stack<Item> stack = new Stack<>();

        System.out.println("\nBilling customer: " + customer);
        System.out.println("Available items: " + prices);
        System.out.println("Enter item name, 'undo' to remove last item, or 'done' to finish.");

        while (true) {
            System.out.print("Item: ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("done")) {
                break;
            } else if (input.equalsIgnoreCase("undo")) {
                if (stack.isEmpty()) {
                    System.out.println("Nothing to undo.");
                } else {
                    Item removed = stack.pop();
                    System.out.println("Last item " + removed.name + " removed.");
                }
            } else if (input.isEmpty()) {
                continue;
            } else {
                String key = findItem(input);
                if (key == null) {
                    System.out.println("Item " + input + " not found in price list.");
                    continue;
                }
                System.out.print("Quantity: ");
                int qty = readInt();
                if (qty <= 0) {
                    System.out.println("Invalid quantity. Item not added.");
                    continue;
                }
                int cost = prices.get(key) * qty;
                stack.push(new Item(key, qty, cost));
                System.out.println(key + " x" + qty + " = " + cost);
            }
        }

        printAndSaveBill(customer, stack);

        if (queue.isEmpty()) {
            System.out.println("Queue is now empty.");
        }
    }

    // Builds the bill text, prints it, stores it in the list and the file
    static void printAndSaveBill(String customer, Stack<Item> stack) {
        if (stack.isEmpty()) {
            System.out.println("No items billed for " + customer + ".");
            return;
        }

        StringBuilder sb = new StringBuilder("Bill for " + customer + ": ");
        int total = 0;
        for (int i = 0; i < stack.size(); i++) {
            Item it = stack.get(i); // bottom -> top = scan order
            sb.append(it.name).append(" x").append(it.qty)
              .append(" = ").append(it.cost);
            if (i < stack.size() - 1) {
                sb.append(", ");
            }
            total += it.cost;
        }
        sb.append(" | Total ").append(total);

        String bill = sb.toString();
        System.out.println(bill);
        bills.add(bill);
        saveBills();
    }

    // Shows waiting customers with positions
    static void viewQueue() {
        if (queue.isEmpty()) {
            System.out.println("No customers are waiting.");
            return;
        }
        System.out.println("Customers waiting:");
        int pos = 1;
        for (String name : queue) {
            System.out.println(pos++ + ". " + name);
        }
    }

    // Case-insensitive lookup in the HashMap; returns the stored key or null
    static String findItem(String input) {
        if (prices.containsKey(input)) {
            return input;
        }
        for (String key : prices.keySet()) {
            if (key.equalsIgnoreCase(input)) {
                return key;
            }
        }
        return null;
    }

    // Loads previously saved bills from bills.txt
    static void loadBills() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    bills.add(line);
                }
            }
            System.out.println(bills.size() + " previous bill(s) loaded from " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Writes all bills to bills.txt
    static void saveBills() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (String bill : bills) {
                pw.println(bill);
            }
        } catch (IOException e) {
            System.out.println("Could not write " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Safely reads an integer from the console
    static int readInt() {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}



// ===== SUPERMARKET BILLING SYSTEM =====
// 1. Add customer
// 2. Bill next customer
// 3. View queue
// 4. Exit
// Enter your choice: 1
// Enter customer name: Atul Kumar
// Atul Kumar added to billing queue. Position: 1

// ===== SUPERMARKET BILLING SYSTEM =====
// 1. Add customer
// 2. Bill next customer
// 3. View queue
// 4. Exit
// Enter your choice: Anuj Kumar
// Invalid choice. Please enter 1-4.

// ===== SUPERMARKET BILLING SYSTEM =====
// 1. Add customer
// 2. Bill next customer
// 3. View queue
// 4. Exit
// Enter your choice: 2 

// Billing customer: Atul Kumar
// Available items: {Rice=60, Milk=30, Bread=40}
// Enter item name, 'undo' to remove last item, or 'done' to finish.
// Item: Rice
// Quantity: 20
// Rice x20 = 1200
// Item: done
// Bill for Atul Kumar: Rice x20 = 1200 | Total 1200
// Queue is now empty.

// ===== SUPERMARKET BILLING SYSTEM =====
// 1. Add customer
// 2. Bill next customer
// 3. View queue
// 4. Exit
// Enter your choice: 4
// Bills saved to bills.txt. Goodbye!