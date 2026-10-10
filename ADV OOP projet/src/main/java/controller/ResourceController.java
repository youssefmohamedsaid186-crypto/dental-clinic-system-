package controller;

import database.ResourceDAO;
import model.Resource;

import java.util.List;

public class ResourceController {

    private final ResourceDAO resourceDAO;

    public ResourceController() {
        this.resourceDAO = new ResourceDAO();
    }

    public List<Resource> getAllResources() {
        return resourceDAO.getAllResources();
    }

    public boolean addResource(String toolName, int totalQty, int availableQty) {
        if (toolName == null || toolName.trim().isEmpty() || totalQty < 0 || availableQty < 0 || availableQty > totalQty) {
            return false;
        }

        Resource resource = new Resource(toolName.trim(), totalQty, availableQty);
        int resourceId = resourceDAO.addResource(resource);
        return resourceId > 0;
    }

    public boolean updateResourceQuantity(int resourceId, int availableQty, int totalQty) {
        if (resourceId <= 0 || totalQty < 0 || availableQty < 0 || availableQty > totalQty) {
            return false;
        }
        return resourceDAO.updateQuantity(resourceId, availableQty, totalQty);
    }
}
