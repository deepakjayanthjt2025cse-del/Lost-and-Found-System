import java.time.LocalDate;

/**
 * FoundItem (INHERITANCE - child of Item)
 * Same parent as LostItem, but with found-specific behaviour.
 */
public class FoundItem extends Item {

    private String foundLocation;

    public FoundItem() {
        super(0, 0, null, null, null, null, "AVAILABLE");   // starts with the default status
    }

    public FoundItem(int id, int userId, String itemName, String category,
                     String description, String foundLocation, LocalDate foundDate, String status) {
        super(id, userId, itemName, category, description, foundDate, status);
        this.foundLocation = foundLocation;
    }

    @Override
    public String getLocation() { return foundLocation; }

    @Override
    public void setLocation(String location) {
        this.foundLocation = requireText(location, "Found location");
    }

    @Override
    public String getType() { return "FOUND"; }

    @Override
    public String[] getAllowedStatuses() { return new String[] {"AVAILABLE", "CLAIMED"}; }

    @Override
    public String getDetails() {
        return "Found Item ID  : " + getId() + "\n"
             + "Item Name      : " + getItemName() + "\n"
             + "Category       : " + getCategory() + "\n"
             + "Description    : " + getDescription() + "\n"
             + "Found Location : " + foundLocation + "\n"
             + "Found Date     : " + getDate() + "\n"
             + "Status         : " + getStatus();
    }
}
