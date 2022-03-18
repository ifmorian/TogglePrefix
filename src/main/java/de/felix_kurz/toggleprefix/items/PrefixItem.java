package de.felix_kurz.toggleprefix.items;

import org.bukkit.Material;

public class PrefixItem extends InventoryItem {

    private String name;
    private int priority;

    public PrefixItem(String name, int priority, String title, Material type, boolean enchanted) {
        super(title, type, enchanted);
        this.name = name;
        this.priority = priority;
    }

}
