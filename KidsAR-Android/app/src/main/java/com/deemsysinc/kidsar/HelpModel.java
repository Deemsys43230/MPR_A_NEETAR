package com.deemsysinc.kidsar;

public class HelpModel {
    private boolean bold;
    private boolean icon;
    public int id;
    public String name;

    public HelpModel(int id, String name, boolean bold, boolean icon) {
        this.id = id;
        this.name = name;
        this.bold = bold;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public boolean isIcon() {
        return icon;
    }

    public void setIcon(boolean icon) {
        this.icon = icon;
    }
}
