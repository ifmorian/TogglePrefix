package de.felix_kurz.toggleprefix.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

public class Utils {
    public static byte[] UUIDtoByte(UUID id) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(id.getMostSignificantBits());
        buffer.putLong(id.getLeastSignificantBits());
        return buffer.array();
    }

    public static String convertToLetters(String s) {
        return s
                .replace("9", "a")
                .replace("8", "b")
                .replace("7", "c")
                .replace("6", "d")
                .replace("5", "e")
                .replace("4", "f")
                .replace("3", "g")
                .replace("2", "h")
                .replace("1", "i")
                .replace("0", "j");
    }

    public static String colorTranslate(String s) {
        return s.replace("&", "§");
    }

}
