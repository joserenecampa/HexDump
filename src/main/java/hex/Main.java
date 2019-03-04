package hex;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) {
        
        String fileName = System.getProperty("file");
        if (fileName == null || fileName.isEmpty()) { System.out.println("No file."); System.exit(0); }
        File file = new File(fileName);
        if (!file.exists()) { System.out.println("File not found."); System.exit(0); }
        if (!file.isFile()) { System.out.println("This isn't a file."); System.exit(0); }
        if (!file.canRead()) { System.out.println("I can't read the file."); System.exit(0); }
        long fileLength = file.length();
        if (fileLength >= 999999) { System.out.println("This file is big. Max 1mb."); System.exit(0); }

        String charsetName = System.getProperty("charset");
        if (charsetName != null && !charsetName.isEmpty()) {
            try {   
                HexDump.CHARSET = Charset.forName(charsetName);
            } catch (Throwable error) {
                System.out.println("Charset isn't suported.");
                System.exit(0);
            }
        }
        
        HexDump.SHOW_TEXT = false;
        String showText = System.getProperty("showText");
        if (showText != null && !showText.isEmpty()) {
            boolean showAscii = Boolean.parseBoolean(showText);
            HexDump.SHOW_TEXT = showAscii;
        }

        HexDump.SHOW_LINE_NUMBERS = false;
        String showLineNumbers = System.getProperty("showLineNumbers");
        if (showLineNumbers != null && !showLineNumbers.isEmpty()) {
            boolean showLineNumber = Boolean.parseBoolean(showLineNumbers);
            HexDump.SHOW_LINE_NUMBERS = showLineNumber;
        }

        String startParam = System.getProperty("start");
        int start = 0;
        if (startParam != null && !startParam.isEmpty()) {
            try {
                start = Integer.parseInt(startParam);
            } catch (Throwable error) {
                System.out.println("Start param MUST be a integer value.");
                System.exit(0);
            }
        }

        String lengthParam = System.getProperty("length");
        int length = (int)fileLength;
        if (lengthParam != null && !lengthParam.isEmpty()) {
            try {
                length = Integer.parseInt(lengthParam);
            } catch (Throwable error) {
                System.out.println("Length param MUST be a integer value.");
                System.exit(0);
            }
        }

        byte[] content = new byte[(int)fileLength];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fis.read(content);
        } catch (Throwable error) {
            System.out.println("I can't read the file.");
            System.exit(0);
        }

        System.out.println(HexDump.dump(content, start, length));

    }

}