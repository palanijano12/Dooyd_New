package datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DescItem {
    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;
    @SerializedName("maxQuantity")
    @Expose
    private Integer maxQuantity;
    @SerializedName("productImage")
    @Expose
    private List<ProductImage> productImage = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("dicipline")
    @Expose
    private String dicipline;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("price")
    @Expose
    private Double  price;
    @SerializedName("cutPrice")
    @Expose
    private Double  cutPrice;
    @SerializedName("shortDescription")
    @Expose
    private String shortDescription;

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public List<ProductImage> getProductImage() {
        return productImage;
    }

    public void setProductImage(List<ProductImage> productImage) {
        this.productImage = productImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDicipline() {
        return dicipline;
    }

    public void setDicipline(String dicipline) {
        this.dicipline = dicipline;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Double  getPrice() {
        return price;
    }

    public void setPrice(Double  price) {
        this.price = price;
    }

    public Double  getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(Double  cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public class ProductImage {

        @SerializedName("fileUrl")
        @Expose
        private String fileUrl;

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}
