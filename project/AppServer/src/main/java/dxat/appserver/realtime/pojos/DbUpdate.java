package dxat.appserver.realtime.pojos;

public class DbUpdate {
    private String inventoryId = "";
    private String propertyId = "";
    private String legacyValue = "";
    private String newValue = "";
    private String message = "";

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getLegacyValue() {
        return legacyValue;
    }

    public void setLegacyValue(String legacyValue) {
        this.legacyValue = legacyValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
