import java.time.LocalDate;

/**
 * LostItem (INHERITANCE - child of Item)
 * Adds the lost location and overrides the abstract methods.
 */
public class LostItem extends Item {

    private String lostLocation;

    public LostItem() {
        super(0, 0, null, null, null, null, "LOST");   // starts with the default status
    }

    public LostItem(int id, int userId, String itemName, String category,
                    String description, String lostLocation, LocalDate lostDate, String status) {
        super(id, userId, itemName, category, description, lostDate, status);
        this.lostLocation = lostLocation;
    }

    @Override
    public String getLocation() { return lostLocation; }

    @Override
    public void setLocation(String location) {
        this.lostLocation = requireText(location, "Lost location");
    }

    @Override
    public String getType() { return "LOST"; }

    @Override
    public String[] getAllowedStatuses() { return new String[] {"LOST", "RETURNED"}; }

    @Override
    public String getDetails() {
        return "Lost Item ID  : " + getId() + "\n"
             + "Item Name     : " + getItemName() + "\n"
             + "Category      : " + getCategory() + "\n"
             + "Description   : " + getDescription() + "\n"
             + "Lost Location : " + lostLocation + "\n"
             + "Lost Date     : " + getDate() + "\n"
             + "Status        : " + getStatus();
    }
}
