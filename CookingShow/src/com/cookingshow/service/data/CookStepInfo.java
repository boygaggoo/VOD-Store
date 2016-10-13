package com.cookingshow.service.data;

public class CookStepInfo {
	private int dishId = 0;
	private int cookSn = 0;
    private String cookText = "";

	public CookStepInfo() {
		// TODO Auto-generated constructor stub
	}

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }
    
    public int getCookSn() {
        return cookSn;
    }

    public void setCookSn(int cookSn) {
        this.cookSn = cookSn;
    }

    public String getCookText() {
        return cookText;
    }

    public void setCookText(String cookText) {
        this.cookText = cookText;
    }
}
