package com.gionee.autotest.field.data.db.model;

/**
 * Created by viking on 11/7/17.
 *
 * Model class for app information
 */

public class App {

    private final int key ;

    private final String label ;

    private final String icon ;

    private final String activity ;

    private boolean installed ;

    public App(int key, String label, String icon, String activity, boolean installed) {
        this.key = key;
        this.label = label;
        this.icon = icon;
        this.activity = activity;
        this.installed = installed ;
    }

    public int getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getActivity() {
        return activity;
    }

    public void setInstalled(boolean isInstalled){
        this.installed = isInstalled ;
    }

    public boolean isInstalled(){
        return installed ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        App app = (App) o;

        if (key != app.key) return false;
        if (label != null ? !label.equals(app.label) : app.label != null) return false;
        if (icon != null ? !icon.equals(app.icon) : app.icon != null) return false;
        return activity != null ? activity.equals(app.activity) : app.activity == null;
    }

    @Override
    public int hashCode() {
        int result = key;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (activity != null ? activity.hashCode() : 0);
        return result;
    }
}
