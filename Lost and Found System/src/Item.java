import java.time.LocalDate;

/**
 * Item (ABSTRACT CLASS - Abstraction and Inheritance parent)
 * ----------------------------------------------------------
 * Holds everything LostItem and FoundItem have in common.
 * You cannot write "new Item()" - only the children can be created.
 *
 * ENCAPSULATION : every field is private; access is through getters/setters,
 *                 and setters validate their input.
 * ABSTRACTION   : abstract methods below MUST be written by each child.
 * POLYMORPHISM  : displayRow() is OVERLOADED (two versions here);
 *                 getLocation()/getDetails() are OVERRIDDEN in each child.
 */
public abstract class Item implements Searchable {

    // ---------- private fields (Encapsulation) ----------
    private int id;
    private int userId;
    private String itemName;
    private String category;
    private String description;
    private LocalDate date;
    private String status;
    private String reporterName;      // filled when we JOIN with the users table
    private String reporterContact;   // filled when we JOIN with the users table

    // ---------- constructors ----------
    protected Item() {
    }

    protected Item(int id, int userId, String itemName, String category,
                   String description, LocalDate date, String status) {
        this.id = id;
        this.userId = userId;
        this.itemName = itemName;
        this.category = category;
        this.description = description;
        this.date = date;
        this.status = status;
    }

    // ---------- abstract methods (children must implement) ----------
    public abstract String getLocation();
    public abstract void setLocation(String location);
    public abstract String getType();               // "LOST" or "FOUND"
    public abstract String[] getAllowedStatuses();  // valid status values for this type
    public abstract String getDetails();            // full multi-line description

    // ---------- Searchable (interface method implemented here) ----------
    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String k = keyword.trim().toLowerCase();
        return contains(itemName, k) || contains(category, k) || contains(getLocation(), k);
    }

    private boolean contains(String text, String keywordLower) {
        return text != null && text.toLowerCase().contains(keywordLower);
    }

    // ---------- table formatting ----------
    private static final String ROW_FORMAT = "%-5s%-16s%-12s%-22s%-16s%-11s%-10s";

    /** Column headings plus a divider line, shared by lost and found tables. */
    public static String tableHeader() {
        String head = String.format(ROW_FORMAT,
                "ID", "ITEM NAME", "CATEGORY", "DESCRIPTION", "LOCATION", "DATE", "STATUS");
        return head + "\n" + "-".repeat(head.length());
    }

    /** Shortens text so it never breaks the table layout. */
    public static String fit(String text, int width) {
        if (text == null) {
            return "";
        }
        if (text.length() > width - 1) {
            return text.substring(0, width - 4) + "...";
        }
        return text;
    }

    /** OVERLOADING #1 - one table row. */
    public String displayRow() {
        return String.format(ROW_FORMAT,
                id, fit(itemName, 16), fit(category, 12), fit(description, 22),
                fit(getLocation(), 16), date, status);
    }

    /** OVERLOADING #2 - same name, different parameter: optionally add reporter line. */
    public String displayRow(boolean showReporter) {
        String row = displayRow();
        if (showReporter && reporterName != null) {
            row += "\n     Reported by: " + reporterName + " (" + reporterContact + ")";
        }
        return row;
    }

    @Override
    public String toString() {
        return displayRow();
    }

    // ---------- validation helper used by setters ----------
    protected static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    // ---------- getters and setters ----------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) {
        this.itemName = requireText(itemName, "Item name");
    }

    public String getCategory() { return category; }
    public void setCategory(String category) {
        this.category = requireText(category, "Category");
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = requireText(description, "Description");
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be empty.");
        }
        this.date = date;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        String s = requireText(status, "Status").toUpperCase();
        for (String allowed : getAllowedStatuses()) {   // polymorphic call
            if (allowed.equals(s)) {
                this.status = s;
                return;
            }
        }
        throw new IllegalArgumentException("Invalid status '" + status + "' for a " + getType() + " item.");
    }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterContact() { return reporterContact; }
    public void setReporterContact(String reporterContact) { this.reporterContact = reporterContact; }
}
