package de.felix_kurz.toggleprefix.utils;

import org.bukkit.Material;

public class Prefix {

    public String name;
    public String display;
    public Material item;
    public int priority;

    public Prefix(String name, String display, String item, String priority) {
        this.name = name;
        this.display = display;
        this.item = Material.getMaterial(item);
        this.priority = Integer.parseInt(priority);
    }

}
