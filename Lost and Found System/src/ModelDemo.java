import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ModelDemo
 * ---------
 * Stage 3 test program. Needs NO database. Shows all four OOP concepts.
 * (Can be deleted before submission.)
 */
public class ModelDemo {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("          LOST AND FOUND SYSTEM");
        System.out.println("            MODEL CLASS DEMO");
        System.out.println("========================================\n");

        // ---- Build objects with setters (Encapsulation) ----
        LostItem lost = new LostItem();
        lost.setId(1);
        lost.setItemName("Black Wallet");
        lost.setCategory("Accessories");
        lost.setDescription("Leather wallet with college ID inside");
        lost.setLocation("Chennai Central");
        lost.setDate(LocalDate.of(2026, 10, 25));
        lost.setReporterName("Arun Kumar");
        lost.setReporterContact("9876543210");

        FoundItem found = new FoundItem();
        found.setId(1);
        found.setItemName("Wallet");
        found.setCategory("Accessories");
        found.setDescription("Black leather wallet found near platform 3");
        found.setLocation("Chennai Central");
        found.setDate(LocalDate.of(2026, 10, 26));

        // ---- 1. ENCAPSULATION: setters protect the data ----
        System.out.println("1. ENCAPSULATION");
        try {
            lost.setItemName("   ");
        } catch (IllegalArgumentException e) {
            System.out.println("   Blocked bad data  -> " + e.getMessage());
        }
        try {
            lost.setStatus("CLAIMED");   // CLAIMED is only valid for FOUND items
        } catch (IllegalArgumentException e) {
            System.out.println("   Blocked bad status -> " + e.getMessage());
        }

        // ---- 2. INHERITANCE + 3. POLYMORPHISM ----
        System.out.println("\n2/3. INHERITANCE AND POLYMORPHISM (one Item[] array, two child types)");
        Item[] items = { lost, found };
        for (Item item : items) {
            System.out.println("   " + item.getType() + " item -> location = " + item.getLocation());
        }

        System.out.println("\n   Table built by the SAME inherited code:");
        System.out.println(Item.tableHeader());
        for (Item item : items) {
            System.out.println(item.displayRow());
        }

        System.out.println("\n   Overloaded displayRow(true) adds the reporter line:");
        System.out.println(lost.displayRow(true));

        System.out.println("\n   Overridden getDetails() - each child prints its own labels:");
        System.out.println(lost.getDetails());
        System.out.println();
        System.out.println(found.getDetails());

        // ---- 4. ABSTRACTION ----
        System.out.println("\n4. ABSTRACTION");
        System.out.println("   Item is abstract, so 'new Item()' will not compile.");
        System.out.println("   Searchable interface: found.matches(\"chennai\") = " + found.matches("chennai"));
        System.out.println("   Searchable interface: found.matches(\"phone\")   = " + found.matches("phone"));

        // ---- Claim, User, Admin ----
        System.out.println("\nCLAIM, USER, ADMIN");
        Claim claim = new Claim(1, 1, "Arun Kumar", "9876543210",
                "My college ID card is inside the wallet", LocalDateTime.of(2026, 11, 1, 14, 30), "PENDING");
        claim.setFoundItemName("Wallet");
        System.out.println(Claim.tableHeader());
        System.out.println(claim.displayRow());
        System.out.println(new User(1, "Arun Kumar", "9876543210"));
        System.out.println(new Admin(1, "admin", "admin123"));

        System.out.println("\nDemo finished.");
    }
}
