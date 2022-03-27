package de.felix_kurz.toggleprefix.utils;

import de.felix_kurz.toggleprefix.main.Main;
import org.bukkit.entity.Player;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Utils {
    public static byte[] UUIDtoBytes(UUID id) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID BytestoUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
    public static void error(Player p) {
        p.sendMessage(Main.PRE + "§cEtwas ist schiefgelaufen");
    }

    public static String convertToLetters(String s) {
        return s.substring(0, 3).replace("9", "a")
                .replace("8", "b")
                .replace("7", "c")
                .replace("6", "d")
                .replace("5", "e")
                .replace("4", "f")
                .replace("3", "g")
                .replace("2", "h")
                .replace("1", "i")
                .replace("0", "j") +
                s.substring(3);
    }

    public static String colorTranslate(String s) {
        return s.replace("&", "§");
    }

    public static String joinPrefixes(String newPrefixes, String oldPrefixes) {
        StringBuilder prefixes = new StringBuilder(oldPrefixes);
        String[] oldPrefixesArray = oldPrefixes.split(",");
        String[] newPrefixesArray = newPrefixes.split(",");
        mainloop: for (String newPrefix : newPrefixesArray) {
            for (String oldPrefix : oldPrefixesArray) {
                if (newPrefix.equals(oldPrefix)) {
                    continue mainloop;
                }
            }
            prefixes.append(",").append(newPrefix);
        }
        return prefixes.toString();
    }

}
