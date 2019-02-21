package datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlideItem {

    @SerializedName("id")
    @Expose
    private String itemId;

    @SerializedName("url")
    @Expose
    private String itemImageUrl;

    @SerializedName("panelType")
    @Expose
    private int itemPanelType;

    @SerializedName("isactive")
    @Expose
    private int itemActive;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public int getItemPanelType() {
        return itemPanelType;
    }

    public void setItemPanelType(int itemPanelType) {
        this.itemPanelType = itemPanelType;
    }

    public int getItemActive() {
        return itemActive;
    }

    public void setItemActive(int itemActive) {
        this.itemActive = itemActive;
    }
}
