package model;

public class Resource {
    private int id;
    private String toolName;
    private int totalQty;
    private int availableQty;
    private String status; // AVAILABLE, LOW_STOCK, OUT_OF_STOCK

    public Resource() {
    }

    public Resource(int id, String toolName, int totalQty, int availableQty, String status) {
        this.id = id;
        this.toolName = toolName;
        this.totalQty = totalQty;
        this.availableQty = availableQty;
        this.status = status;
    }

    public Resource(String toolName, int totalQty, int availableQty) {
        this.toolName = toolName;
        this.totalQty = totalQty;
        this.availableQty = availableQty;
        this.status = calculateStatus(availableQty, totalQty);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
        this.status = calculateStatus(this.availableQty, this.totalQty);
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
        this.status = calculateStatus(this.availableQty, this.totalQty);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static String calculateStatus(int availableQty, int totalQty) {
        if (availableQty <= 0) {
            return "OUT_OF_STOCK";
        } else if (availableQty <= 0.2 * totalQty || availableQty < 5) {
            return "LOW_STOCK";
        } else {
            return "AVAILABLE";
        }
    }

    @Override
    public String toString() {
        return toolName + " (" + availableQty + "/" + totalQty + " - " + status + ")";
    }
}
