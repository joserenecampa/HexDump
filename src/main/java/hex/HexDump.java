package hex;

import java.nio.charset.Charset;

public class HexDump {

    public static boolean HEXA_UPPERCASE = true;
    public static String HEXA_PATTERN = HEXA_UPPERCASE ? "%02X" : "%02x";
    public static String LINE_NUMBER_PATTERN = "%06d";
    public static int HEX_BLOCKS = 5;
    public static int HEX_CHAR_BLOCKS = 5;
    public static Charset CHARSET = Charset.forName("UTF-8");
    public static boolean SHOW_TEXT = true;
    public static boolean SHOW_LINE_NUMBERS = true;

    private static int HEX_TOTAL_CHARS = 0;
    private static byte[] ARRAY = null;

    public static String dump(byte[] array, int start, int length) {
        if (array == null || start > array.length) return null;
        HEX_TOTAL_CHARS = HEX_BLOCKS * HEX_CHAR_BLOCKS;
        ARRAY = array;
        length = Math.min(start + length, ARRAY.length);
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer lineBuffer = new StringBuffer();
        int pos = start;
        while (pos < length) {
            byte b = ARRAY[pos];
            lineBuffer.append(String.format(HEXA_PATTERN, b));
            pos++;
            if ((pos % HEX_TOTAL_CHARS) == 0 || pos == length) {
                resultBuffer.append(HexDump.formatDumpLine(lineBuffer.toString(), pos - 1));
                lineBuffer = new StringBuffer();
            }
        }
        return resultBuffer.toString();
    }

    private static String formatDumpLine(String hexDump, int pos) {
        int lineNumber = pos - (pos % HEX_TOTAL_CHARS);
        hexDump = String.format("%" + ((pos - lineNumber + 1) * 2) + "s", hexDump);
        hexDump = String.format("%-" + (HEX_TOTAL_CHARS * 2) + "s", hexDump);
        return (SHOW_LINE_NUMBERS ? HexDump.formatNumberLine(lineNumber) + " | " : "") + HexDump.formatHexLine(hexDump)
                + (SHOW_TEXT ? " | " + HexDump.formatTextLine(hexDump) + " |\n" : "\n");
    }

    private static String formatNumberLine(int number) {
        return String.format(LINE_NUMBER_PATTERN, number);
    }

    private static String formatTextLine(String hexDump) {

        String text = new String(hexStringToByteArray(hexDump), CHARSET);
        if (CHARSET.name().equalsIgnoreCase("UTF-8")) {
            byte[] utf8ByteArray = null;
        }

        return String.format("%-" + HEX_TOTAL_CHARS + "s", text);
    }

    private static String formatHexLine(String hexaLine) {
        if (HEX_BLOCKS == 1)
            return hexaLine;
        String result = "";
        for (int i = 0; i < HEX_BLOCKS; i++) {
            result = result
                    + hexaLine.substring(i * (HEX_CHAR_BLOCKS * 2), i * (HEX_CHAR_BLOCKS * 2) + (HEX_CHAR_BLOCKS * 2));
            if (i < HEX_BLOCKS - 1)
                result = result + " ";
        }
        return result;
    }

    public static String dump(byte[] array) {
        return HexDump.dump(array, 0, array.length);
    }

    private static byte[] hexStringToByteArray(String s) {
        s = s.replace("  ", String.format(HexDump.HEXA_PATTERN, " ".getBytes(HexDump.CHARSET)[0])).replace(":", "")
                .replace("\n", "").replace("\r", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        return data;
    }
}
