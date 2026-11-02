import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Stage4Test
 * ----------
 * TEMPORARY mini-menu for testing InputHelper + ItemService against MySQL.
 * In the final stage this logic moves into the real User menu.
 */
public class Stage4Test {

    private static final ItemService itemService = new ItemService();

    public static void main(String[] args) {
        if (!DatabaseConnection.testConnection()) {
            return;
        }
        while (true) {
            InputHelper.printHeader("STAGE 4 TEST MENU");
            System.out.println("1. Report Lost Item");
            System.out.println("2. Report Found Item");
            System.out.println("3. Search for an Item");
            System.out.println("4. View My Reports");
            System.out.println("5. Exit");
            int choice = InputHelper.readChoice("\nEnter your choice: ", 1, 5);

            try {
                switch (choice) {
                    case 1: reportLost();  break;
                    case 2: reportFound(); break;
                    case 3: search();      break;
                    case 4: viewMyReports(); break;
                    default:
                        System.out.println("Goodbye!");
                        return;
                }
            } catch (SQLException e) {
                System.out.println("[ERROR] " + DatabaseConnection.explain(e));
            }
        }
    }

    private static void reportLost() throws SQLException {
        InputHelper.printHeader("REPORT LOST ITEM");
        LostItem item = new LostItem();
        item.setItemName(InputHelper.readRequired("Item name        : ", 100));
        item.setCategory(InputHelper.readRequired("Item category    : ", 50));
        item.setDescription(InputHelper.readRequired("Item description : ", 255));
        item.setLocation(InputHelper.readRequired("Lost location    : ", 100));
        item.setDate(InputHelper.readDate("Lost date (yyyy-MM-dd): "));
        item.setReporterName(InputHelper.readRequired("Your name         : ", 100));
        item.setReporterContact(InputHelper.readContact("Contact number    : "));

        int id = itemService.addLostItem(item);
        System.out.println("\n[SUCCESS] Lost item reported. Your Lost Item ID is " + id + ".");
    }

    private static void reportFound() throws SQLException {
        InputHelper.printHeader("REPORT FOUND ITEM");
        FoundItem item = new FoundItem();
        item.setItemName(InputHelper.readRequired("Item name        : ", 100));
        item.setCategory(InputHelper.readRequired("Item category    : ", 50));
        item.setDescription(InputHelper.readRequired("Item description : ", 255));
        item.setLocation(InputHelper.readRequired("Found location   : ", 100));
        item.setDate(InputHelper.readDate("Found date (yyyy-MM-dd): "));
        item.setReporterName(InputHelper.readRequired("Finder name       : ", 100));
        item.setReporterContact(InputHelper.readContact("Contact number    : "));

        int id = itemService.addFoundItem(item);
        System.out.println("\n[SUCCESS] Found item reported. Its Found Item ID is " + id + ".");
    }

    private static void search() throws SQLException {
        InputHelper.printHeader("SEARCH FOR AN ITEM");
        System.out.println("Search by:");
        System.out.println("1. Item name");
        System.out.println("2. Item category");
        System.out.println("3. Location");
        int choice = InputHelper.readChoice("\nEnter your choice: ", 1, 3);
        String[] criteria = {"NAME", "CATEGORY", "LOCATION"};
        String keyword = InputHelper.readRequired("Enter search keyword: ", 100);

        List<LostItem> lost = itemService.searchLostItems(criteria[choice - 1], keyword);
        List<FoundItem> found = itemService.searchFoundItems(criteria[choice - 1], keyword);

        printItems("LOST ITEMS FOUND", lost);
        printItems("FOUND ITEMS FOUND", found);
    }

    private static void viewMyReports() throws SQLException {
        InputHelper.printHeader("VIEW MY REPORTS");
        String contact = InputHelper.readContact("Enter your contact number: ");
        printItems("MY LOST REPORTS", itemService.getLostItemsByContact(contact));
        printItems("MY FOUND REPORTS", itemService.getFoundItemsByContact(contact));
    }

    /** Works for a list of LostItem OR FoundItem (polymorphism through Item). */
    private static void printItems(String title, List<? extends Item> items) {
        InputHelper.printHeader(title);
        if (items.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println(Item.tableHeader());
        for (Item item : items) {
            System.out.println(item.displayRow());
        }
        System.out.println("\nTotal records: " + items.size());
    }
}
