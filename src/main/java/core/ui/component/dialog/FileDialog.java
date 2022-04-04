package core.ui.component.dialog;

import core.ui.component.GBC;
import core.ui.component.model.FileOpertionInfo;
import util.automaticBindClick;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

public class FileDialog extends JDialog {
    private final JTextField srcFileTextField = new JTextField("srcFileText", 30);
    private final JTextField destFileTextField = new JTextField("destText", 30);
    private final JLabel srcFileLabel = new JLabel("srcFile");
    private final JLabel destFileLabel = new JLabel("destFile");
    private final JButton okButton = new JButton("ok");
    private final JButton cancelButton = new JButton("cancel");
    private final FileOpertionInfo fileOpertionInfo = new FileOpertionInfo();
    private final JButton srcSelectdFileButton = new JButton("select File");
    private final JButton destSelectdFileButton = new JButton("select File");
    Dimension TextFieldDim = new Dimension(500, 23);
    private boolean state;

    private FileDialog(Frame frame, String tipString, String srcFileString, String destString) {
        super(frame, tipString, true);
        Dimension TextFieldDim = new Dimension(200, 23);
        GBC gbcLSrcFile = (new GBC(0, 0)).setInsets(5, -40, 0, 0);
        GBC gbcSrcFile = (new GBC(1, 0, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcSrcSelectdFie = (new GBC(4, 0, 7, 1)).setInsets(5, 50, 0, 10);
        GBC gbcDestSelectdFie = (new GBC(4, 1, 7, 1)).setInsets(5, 50, 0, 10);
        GBC gbcLDestFile = (new GBC(0, 1)).setInsets(5, -40, 0, 0);
        GBC gbcDestFile = (new GBC(1, 1, 3, 1)).setInsets(5, 20, 0, 0);
        GBC gbcOkButton = (new GBC(0, 2, 2, 1)).setInsets(5, 20, 0, 0);
        GBC gbcCancelButton = (new GBC(2, 2, 1, 1)).setInsets(5, 20, 0, 0);
        this.srcFileTextField.setPreferredSize(TextFieldDim);
        this.destFileTextField.setPreferredSize(TextFieldDim);
        this.setLayout(new GridBagLayout());
        this.add(this.srcFileLabel, gbcLSrcFile);
        this.add(this.srcFileTextField, gbcSrcFile);
        this.add(this.srcSelectdFileButton, gbcSrcSelectdFie);
        this.add(this.destSelectdFileButton, gbcDestSelectdFie);
        this.add(this.destFileLabel, gbcLDestFile);
        this.add(this.destFileTextField, gbcDestFile);
        this.add(this.okButton, gbcOkButton);
        this.add(this.cancelButton, gbcCancelButton);
        automaticBindClick.bindJButtonClick(this, this);
        this.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent paramWindowEvent) {
            }

            public void windowIconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeiconified(WindowEvent paramWindowEvent) {
            }

            public void windowDeactivated(WindowEvent paramWindowEvent) {
            }

            public void windowClosing(WindowEvent paramWindowEvent) {
                FileDialog.this.cancelButtonClick(null);
            }

            public void windowClosed(WindowEvent paramWindowEvent) {
            }

            public void windowActivated(WindowEvent paramWindowEvent) {
            }
        });
        this.srcFileTextField.setText(srcFileString);
        this.destFileTextField.setText(destString);
        this.setSize(650, 180);
        this.setLocationRelativeTo(frame);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public static FileOpertionInfo showFileOpertion(Frame frame, String title, String srcFileString, String destString) {
        return (new FileDialog(frame, title, srcFileString, destString)).getResult();
    }

    public FileOpertionInfo getResult() {
        return this.fileOpertionInfo;
    }

    private void okButtonClick(ActionEvent actionEvent) {
        this.fileOpertionInfo.setOpertionStatus(true);
        this.changeFileInfo();
    }

    private void cancelButtonClick(ActionEvent actionEvent) {
        this.fileOpertionInfo.setOpertionStatus(false);
        this.changeFileInfo();
    }

    private void changeFileInfo() {
        this.fileOpertionInfo.setSrcFileName(this.srcFileTextField.getText());
        this.fileOpertionInfo.setDestFileName(this.destFileTextField.getText());
        this.state = true;
        this.dispose();
    }

    private void srcSelectdFileButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "ѡ��");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            String fileString = selectdFile.getAbsolutePath();
            this.srcFileTextField.setText(fileString);
        }

    }

    private void destSelectdFileButtonClick(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.showDialog(new JLabel(), "ѡ��");
        File selectdFile = chooser.getSelectedFile();
        if (selectdFile != null) {
            String fileString = selectdFile.getAbsolutePath();
            this.destFileTextField.setText(fileString);
        }

    }
}
