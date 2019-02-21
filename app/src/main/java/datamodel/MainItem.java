package datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MainItem {

    @SerializedName("id")
    @Expose
    private String itemId;

    @SerializedName("productid")
    @Expose
    private String productid;

    @SerializedName("customerId")
    @Expose
    private String customerId;

    @SerializedName("name")
    @Expose
    private String itemName;

    @SerializedName("cost")
    @Expose
    private float itemPrice;

    @SerializedName("cutcost")
    @Expose
    private float itemCutPrice;

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    private float total;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("productquantity")
    @Expose
    private int productquantity;
    @SerializedName("isactive")
    @Expose
    private int itemActive;
    @SerializedName("imageUrl")
    @Expose
    private String itemImageUrl;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getProductquantity() {
        return productquantity;
    }

    public void setProductquantity(int productquantity) {
        this.productquantity = productquantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public float getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(float itemPrice) {
        this.itemPrice = itemPrice;
    }

    public float getItemCutPrice() {
        return itemCutPrice;
    }

    public void setItemCutPrice(float itemCutPrice) {
        this.itemCutPrice = itemCutPrice;
    }

    public int getItemActive() {
        return itemActive;
    }

    public void setItemActive(int itemActive) {
        this.itemActive = itemActive;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
