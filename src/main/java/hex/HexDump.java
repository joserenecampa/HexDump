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
    public static boolean SHOW_CHARS_CONTROL = true;

    private static int HEX_TOTAL_CHARS = 0;
    private static byte[] ARRAY = null;
    private static String CHAR_CONTROL = null;
    private static final Charset UTF8 = CHARSET;
    private static final String UTF8_HEXA_CHAR_CONTROL = "C2B7";

    public static String dump(byte[] array, int start, int length) {
        if (array == null || start > array.length) return null;
        HEX_TOTAL_CHARS = HEX_BLOCKS * HEX_CHAR_BLOCKS;
        CHAR_CONTROL = new String(new String(HexDump.hexStringToByteArray(UTF8_HEXA_CHAR_CONTROL), UTF8).getBytes(CHARSET), CHARSET);
        ARRAY = array;
        length = Math.min(start + length, ARRAY.length);
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer lineBuffer = new StringBuffer();
        int pos = start;
        while (pos < length) {
            lineBuffer.append(String.format(HEXA_PATTERN, ARRAY[pos]));
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
                + (SHOW_TEXT ? " | " + HexDump.formatTextLine(hexDump, pos) + " |\n" : "\n");
    }

    private static String formatNumberLine(int number) {
        return String.format(LINE_NUMBER_PATTERN, number);
    }

    private static String formatTextLine(String hexDump, int pos) {
        boolean modify = false;
        if (hexDump.substring(hexDump.length()-2).equalsIgnoreCase("c3")) {
            hexDump = hexDump.substring(0, hexDump.length()-2) + byteToHexString(new byte[]{ ARRAY[pos], ARRAY[pos+1]});
            modify = true;
        }
        String text = new String(hexStringToByteArray(hexDump), CHARSET);
        if (SHOW_CHARS_CONTROL && CHARSET.equals(UTF8)) {
            String hexaString = "";
            char[] chars = text.toCharArray();
            for (char c : chars) {
                String h = byteToHexString(Character.toString(c).getBytes(CHARSET));
                short i = (short)c;
                if (i > 31 && i < 127) { hexaString = hexaString + h;
                } else if (i > 160 && i < 256) { hexaString = hexaString + h + (modify ? "" : UTF8_HEXA_CHAR_CONTROL); modify = false;
                } else { hexaString = hexaString + UTF8_HEXA_CHAR_CONTROL; }
            }
            text = new String(hexStringToByteArray(hexaString), UTF8);
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

    private static String byteToHexString(byte... bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) sb.append(String.format(HEXA_PATTERN, b));
        return sb.toString().trim();
    }

    private static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("  ", byteToHexString(" ".getBytes(CHARSET))).replaceAll(":", "")
                .replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        return data;
    }
}
