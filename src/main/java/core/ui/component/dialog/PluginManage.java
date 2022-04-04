package core.ui.component.dialog;

import core.ApplicationContext;
import core.Db;
import core.ui.MainActivity;
import core.ui.component.DataView;
import util.Log;
import util.automaticBindClick;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.Vector;

public class PluginManage extends JDialog {
    private final DataView pluginView;
    private final JButton addPluginButton = new JButton("���");
    private final JButton removeButton = new JButton("�Ƴ�");
    private final JButton cancelButton = new JButton("ȡ��");
    private final JButton refreshButton = new JButton("ˢ��");
    private final Vector<String> columnVector = new Vector<>();
    private final JSplitPane splitPane = new JSplitPane();

    public PluginManage() {
        super(MainActivity.getFrame(), "PluginManage", true);
        this.columnVector.add("pluginJarFile");
        this.pluginView = new DataView(null, this.columnVector, -1, -1);
        this.refreshPluginView();
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(this.addPluginButton);
        bottomPanel.add(this.removeButton);
        bottomPanel.add(this.refreshButton);
        bottomPanel.add(this.cancelButton);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.splitPane.setTopComponent(new JScrollPane(this.pluginView));
        this.splitPane.setBottomComponent(bottomPanel);
        this.splitPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                PluginManage.this.splitPane.setDividerLocation(0.85D);
            }
        });
        automaticBindClick.bindJButtonClick(this, this);
        this.add(this.splitPane);
        this.setSize(420, 420);
        this.setLocationRelativeTo(MainActivity.getFrame());
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    private void refreshPluginView() {
        String[] pluginStrings = Db.getAllPlugin();
        Vector<Vector<String>> rows = new Vector<>();

        for (String string : pluginStrings) {
            Vector<String> rowVector = new Vector<>();
            rowVector.add(string);
            rows.add(rowVector);
        }

        this.pluginView.AddRows(rows);
        this.pluginView.getModel().fireTableDataChanged();
    }

    private void addPluginButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("*.jar", "jar"));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "ѡ��");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            if (Db.addPlugin(selectdFile.getAbsolutePath()) == 1) {
                ApplicationContext.init();
                JOptionPane.showMessageDialog(this, "��Ӳ���ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "��Ӳ��ʧ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            Log.log("�û�ȡ��ѡ��.....");
        }

        this.refreshPluginView();
    }

    private void removeButtonClick(ActionEvent actionEvent) {
        int rowIndex = this.pluginView.getSelectedRow();
        if (rowIndex != -1) {
            Object selectedItem = this.pluginView.getValueAt(rowIndex, 0);
            if (Db.removePlugin((String) selectedItem) == 1) {
                JOptionPane.showMessageDialog(this, "�Ƴ�����ɹ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "�Ƴ����ʧ��", "��ʾ", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "û��ѡ�в��", "��ʾ", JOptionPane.WARNING_MESSAGE);
        }

        this.refreshPluginView();
    }

    private void cancelButtonClick(ActionEvent actionEvent) {
        this.dispose();
    }

    private void refreshButtonClick(ActionEvent actionEvent) {
        this.refreshPluginView();
    }
}
