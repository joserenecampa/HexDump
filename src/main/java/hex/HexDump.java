package hex;

import java.nio.charset.Charset;

public class HexDump {

    public static boolean HEXA_UPPERCASE = true;
    public static String HEXA_PATTERN = HEXA_UPPERCASE ? "%02X" : "%02x";
    public static String LINE_NUMBER_PATTERN = "%07d";
    public static int HEX_BLOCKS = 5;
    public static int HEX_CHAR_BLOCKS = 5;
    public static Charset CHARSET = Charset.forName("UTF-8");
    public static boolean SHOW_TEXT = true;
    public static boolean SHOW_LINE_NUMBERS = true;
    public static boolean RELATIVE = false;

    private static int HEX_TOTAL_CHARS = 0;
    private static byte[] ARRAY = null;
    private static String CHAR_CONTROL = null;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String UTF8_HEXA_CHAR_CONTROL = "E280A2"; //"E280A2";

    public static String dump(byte[] array, int start, int length) {
        if (array == null || start > array.length) return null;
        HEX_TOTAL_CHARS = HEX_BLOCKS * HEX_CHAR_BLOCKS;
        CHAR_CONTROL = byteToHexString(new String(new String(HexDump.hexStringToByteArray(UTF8_HEXA_CHAR_CONTROL), UTF8).getBytes(CHARSET), CHARSET).getBytes());
        ARRAY = array;
        length = Math.min(start + length, ARRAY.length);
        StringBuffer resultBuffer = new StringBuffer();
        StringBuffer lineBuffer = new StringBuffer();
        int pos = start; int byt = 0;
        while (pos < length) {
            lineBuffer.append(String.format(HEXA_PATTERN, ARRAY[pos]));
            pos++; byt++; int control = RELATIVE ? byt : pos;
            if ((control % HEX_TOTAL_CHARS) == 0 || pos == length) {
                resultBuffer.append(HexDump.formatDumpLine(lineBuffer.toString(), pos - 1));
                lineBuffer = new StringBuffer();
                byt = 0;
            }
        }
        return resultBuffer.toString().trim();
    }

    private static String formatDumpLine(String hexDump, int pos) {
        int lineNumber = RELATIVE ? pos - (hexDump.length()/2) + 1: pos - (pos % HEX_TOTAL_CHARS);
        if (lineNumber < 0) lineNumber = lineNumber + pos;
        hexDump = String.format("%" + (RELATIVE?"-":"") + ((pos - lineNumber + 1) * 2) + "s", hexDump);
        hexDump = String.format("%-" + (HEX_TOTAL_CHARS * 2) + "s", hexDump);
        return (SHOW_LINE_NUMBERS ? HexDump.formatNumberLine(lineNumber) + " | " : "") + HexDump.formatHexLine(hexDump)
                + (SHOW_TEXT ? " | " + HexDump.formatTextLine(hexDump, pos) + " |\n" : "\n");
    }

    private static String formatNumberLine(int number) {
        return String.format(LINE_NUMBER_PATTERN, number);
    }

    private static String formatTextLine(String hexDump, int pos) {
        // hexDump = hexDump.replaceAll(" ", new String(hexStringToByteArray(CHAR_CONTROL)));
        if (hexDump.substring(hexDump.length()-2).equalsIgnoreCase("C3") || hexDump.substring(hexDump.length()-2).equalsIgnoreCase("C2")) {
            try { hexDump = hexDump.substring(0, hexDump.length()-2) + byteToHexString(new byte[]{ ARRAY[pos], ARRAY[pos+1]}); } catch (Throwable error) { }
        }
        String text = new String(hexStringToByteArray(hexDump), CHARSET);
        String hexaString = "";
        text = new String(hexStringToByteArray(hexDump.replaceAll("EFBFBD", CHAR_CONTROL)), CHARSET);
        char[] chars = text.toCharArray();
        for (char c : chars) {
            String h = byteToHexString(Character.toString(c).getBytes(CHARSET));
            if (h.equalsIgnoreCase("EFBFBD")) {
                hexaString = hexaString + CHAR_CONTROL;
            } else {
                hexaString = hexaString + h;
                if ((h.length()/2) > 1)
                    for (int j = 0; j < (h.length()/2)-1; j++) 
                        hexaString = hexaString + CHAR_CONTROL;
            }
        }
        return String.format("%-" + HEX_TOTAL_CHARS + "s", new String(hexStringToByteArray(hexaString), CHARSET)).substring(0, HEX_TOTAL_CHARS);
    }

    private static String formatHexLine(String hexaLine) {
        hexaLine = hexaLine.replaceAll(" ", new String(hexStringToByteArray(UTF8_HEXA_CHAR_CONTROL)));
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

    public static String dump(byte[] array, int start) {
        return HexDump.dump(array, start, array.length);
    }

    public static String dump(byte[] array) {
        return HexDump.dump(array, 0, array.length);
    }

    public static String byteToHexString(byte... bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) sb.append(String.format(HEXA_PATTERN, b));
        return sb.toString().trim();
    }

    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("  ", byteToHexString(" ".getBytes(CHARSET))).replaceAll(":", "")
                .replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        return data;
    }
    
    public static void main(String[] args) {
        
        Charset utf = Charset.forName("UTF-8");
        Charset ibm = Charset.forName("IBM037");

        String frase = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA   AAAAAAAAAAAAAAAAAAAAA AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        System.out.println(HexDump.dump(frase.getBytes(utf), 1));
        HexDump.CHARSET = ibm;
        System.out.println(HexDump.dump(frase.getBytes(ibm)));

    }
    
}
