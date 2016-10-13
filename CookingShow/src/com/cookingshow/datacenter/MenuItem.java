package com.cookingshow.datacenter;

import java.io.Serializable;

public class MenuItem implements Serializable {
	
    private static final long serialVersionUID = 4805339202395785294L;
    private int id;
    private String type;
    private String name;
    private String code;
    private String enName;
    private int orderNum;

	public MenuItem() {
		// TODO Auto-generated constructor stub
		id = 0;
		type = "";
        name = "";
        code = "";
        enName = "";
        orderNum = 0;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = "  " + name + "  ";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", enName='" + enName + '\'' +
                '}';
    }
}
