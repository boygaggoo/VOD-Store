package com.cookingshow.datacenter;

public class ListItem {

	private int itemId;
    private String itemTitle1;
    private String itemTitle2;
    private String itemText;

	public ListItem() {
		// TODO Auto-generated constructor stub
		itemId = 0;
		itemTitle1 = "";
		itemTitle2 = "";
		itemText = "";
	}

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
    
    public String getItemTitle1() {
        return itemTitle1;
    }

    public void setItemTitle1(String itemTitle1) {
        this.itemTitle1 = itemTitle1;
    }

    public String getItemTitle2() {
        return itemTitle2;
    }

    public void setItemTitle2(String itemTitle2) {
        this.itemTitle2 = itemTitle2;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

}
