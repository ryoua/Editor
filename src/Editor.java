import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import java.util.*;
import java.text.*;
import org.fife.ui.rsyntaxtextarea.*;


public class Editor extends JFrame {


    private String content;
    //设置组件
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu searchMenu;
    private JMenu helpMenu;
    private JMenu timeMenu;
    private RSyntaxTextArea textArea;
    private JScrollPane jScrollPane;
    private JMenuItem openItem, closeItem, saveItem, searchItem, aboutItem, timeItem;
    private FileDialog open, save;
    private File file;


    Editor() {
        Init();
    }

    // 定义
    public void setting(){
        //设置应用的大小等设置
        JFrame frame = new JFrame("记事本");
        frame.setBounds(300, 300, 1280, 720);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        searchMenu = new JMenu("搜索");
        fileMenu = new JMenu("文件");
        helpMenu = new JMenu("帮助");
        timeMenu = new JMenu("时间");

        // 记事本页面的设置
        textArea = new RSyntaxTextArea(20, 60);
        jScrollPane = new JScrollPane(textArea);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setViewportView(textArea);

        openItem = new JMenuItem("打开");
        saveItem = new JMenuItem("保存");
        closeItem = new JMenuItem("关闭");
        searchItem = new JMenuItem("搜索");
        timeItem = new JMenuItem("插入当前时间");
        aboutItem = new JMenuItem("关于");

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        menuBar.add(helpMenu);
        menuBar.add(timeMenu);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(closeItem);
        searchMenu.add(searchItem);
        helpMenu.add(aboutItem);
        timeMenu.add(timeItem);

        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(jScrollPane, BorderLayout.CENTER);

        open = new FileDialog(frame, "打开文档", FileDialog.LOAD);
        save = new FileDialog(frame, "保存文档", FileDialog.SAVE);
        frame.setVisible(true);
    }

    // 初始化
    public void Init() {
        setting();
        close();
        open();
        save();
        search();
        about();
        time();
    }

    // "关闭"
    public void close() {
        closeItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // "打开"
    public void open() {
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                open.setVisible(true);
                String path = open.getDirectory();
                String fileName = open.getFile();
                if (path == null || fileName == null) {
                    return;
                }
                textArea.setText("");
                file = new File(path, fileName);
                try {
                    if (fileName.indexOf(".java") != -1) {
                        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                    } else {
                        textArea.setSyntaxEditingStyle(null);
                    }
                    FileInputStream fr = new FileInputStream(file);
                    InputStreamReader b = new InputStreamReader(fr, "gb2312");
                    BufferedReader br = new BufferedReader(b);
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        textArea.append(line + "\r\n");
                    }
                } catch (IOException ie) {
                    throw new RuntimeException("读取失败！");
                }
                content = textArea.getText();
            }
        });
    }

    // "保存"
    public void save(){
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (file == null) {
                    save.setVisible(true);
                    String path = save.getDirectory();
                    String fileName = save.getFile();
                    if (path == null || fileName == null) {
                        return;
                    }
                    file = new File(path, fileName);
                }
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                    String text = textArea.getText();
                    bw.write(text);
                    bw.close();
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        });
    }

    // "搜索"
    public void search(){
        searchItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("输入查找的内容", JOptionPane.YES_NO_CANCEL_OPTION);
                String z = JOptionPane.showInputDialog("输入替换的内容", JOptionPane.YES_NO_CANCEL_OPTION);
                int yes = JOptionPane.showConfirmDialog(null, "确定替换","取消", JOptionPane.YES_NO_CANCEL_OPTION);
                content = textArea.getText();
                if (yes == JOptionPane.YES_OPTION){
                    if (content.indexOf(s) != -1){
                        content = content.replace(s, z);
                        textArea.setText(content);
                    }
                }
            }
        });
    }

    // "关于"
    public void about() {
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "记事本");
            }
        });
    }

    // "插入时间"
    public void time(){
        timeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat ("yyyy.MM.dd'/'hh:mm:ss");
                content = textArea.getText() + ft.format(date);
                textArea.setText(content);
            }
        });
    }

    public static void main(String[] args) {
        new Editor();
    }
}
