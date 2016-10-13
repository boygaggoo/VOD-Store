package com.cookingshow.navigation;

public class NavigationData {

    public int id;
    public String title;
    public Object tag;
    public int iconId;

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "NavigationData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tag=" + tag +
                '}';
    }
}
