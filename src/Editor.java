import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.util.*;
import java.text.*;

import org.fife.ui.rsyntaxtextarea.*;
import utils.KMP;


public class Editor extends JFrame {

    private static final int MAX_CONTENT_LENGTH = 10000000;
    private String content;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu searchMenu;
    private JMenu helpMenu;
    private JMenu timeMenu;
    private RSyntaxTextArea textArea;
    private JScrollPane jScrollPane;
    private JMenuItem createItem, openItem, saveItem, closeItem, findItem, replaceItem, timeItem, aboutItem;
    private FileDialog open, save;
    private File file;


    private Editor() {
        init();
    }


    private void setting() {

        JFrame frame = new JFrame("记事本");
        frame.setBounds(300, 300, 1280, 720);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件");
        searchMenu = new JMenu("搜索");
        timeMenu = new JMenu("时间");
        helpMenu = new JMenu("帮助");

        textArea = new RSyntaxTextArea(20, 60);
        jScrollPane = new JScrollPane(textArea);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setViewportView(textArea);

        createItem = new JMenuItem("新建");
        openItem = new JMenuItem("打开");
        saveItem = new JMenuItem("保存");
        closeItem = new JMenuItem("关闭");
        findItem = new JMenuItem("搜索");
        replaceItem = new JMenuItem("替换");
        timeItem = new JMenuItem("插入当前时间");
        aboutItem = new JMenuItem("关于");

        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        menuBar.add(timeMenu);
        menuBar.add(helpMenu);
        fileMenu.add(createItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(closeItem);
        searchMenu.add(findItem);
        searchMenu.add(replaceItem);
        timeMenu.add(timeItem);
        helpMenu.add(aboutItem);

        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(jScrollPane, BorderLayout.CENTER);

        open = new FileDialog(frame, "打开文档", FileDialog.LOAD);
        save = new FileDialog(frame, "保存文档", FileDialog.SAVE);
        frame.setVisible(true);
    }


    private void init() {
        setting();
        create();
        open();
        save();
        close();
        find();
        replace();
        time();
        about();
    }

    // "新建"
    private void create() {
        createItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Editor();
            }
        });
    }

    // "打开"
    private void open() {
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                open.setVisible(true);
                String path = open.getDirectory();
                String fileName = open.getFile();
                if (path == null || fileName == null) return;

                textArea.setText("");
                file = new File(path, fileName);
                try {
                    // .java 文件语法高亮
                    if (fileName.contains(".java")) {
                        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                    } else {
                        textArea.setSyntaxEditingStyle(null);
                    }

                    // 对 .bin 文件进行读操作并格式化
                    if (fileName.contains(".bin")) {
                        FileInputStream in;
                        DataInputStream dis;
                        content = "";
                        try {
                            in = new FileInputStream(path + fileName);
                            dis = new DataInputStream(in);
                            while (dis.available() != 0) {
                                for (int i = 1; i < MAX_CONTENT_LENGTH; i++) {
                                    content += dis.readByte();
                                    if (i % 4 == 0) {
                                        content += "\n";
                                    }
                                }
                            }
                            dis.close();
                        } catch (Exception e1) {
                            System.out.println(e1.toString());
                        }
                        textArea.setText(content);
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
            }
        });
    }

     // 二进制读写的单元测试
    private void test(){
            FileInputStream in;
            DataInputStream dis;
            try {
                DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("C:\\Users\\SARA\\IdeaProjects\\Editor\\src\\test_app.bin")));
                os.writeInt(1001);
                os.flush();
                os.close();


                DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream("C:\\Users\\SARA\\IdeaProjects\\Editor\\src\\test_app.bin")));
                if (is.readInt() == 1001){
                    System.out.println("Test is OK:)");
                } else {
                    assert false;
                }
                is.close();
            } catch (IOException e) {

            }
    }
    
    // "保存"
    private void save() {
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

    // "关闭"
    private void close() {
        closeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // "搜索"
    private void find() {
        findItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int a[][] = new int[99][2];
                String s = JOptionPane.showInputDialog("输入查找的内容", null);
                KMP kmp = new KMP();
                content = textArea.getText();
                int i = 0;
                int offset = 0;
                for (; ; ) {
                    if (kmp.kmp(content, s) == -1) {
                        break;
                    } else {
                        int start = kmp.kmp(content, s);
                        int end = start + s.length();
                        if (end > content.length()) break;
                        if (i == 0) {
                            textArea.setSelectionStart(start);
                            textArea.setSelectionEnd(end);
                        }

                        a[i][0] = start + offset;
                        a[i][1] = end + offset;

                        content = content.substring(end);
                        offset += end;
                        i++;
                    }
                }

                int j = 0;
                while (i > 1) {
                    int yes = JOptionPane.showConfirmDialog(null, "下一个?", "取消", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (yes == JOptionPane.YES_OPTION) {
                        textArea.setSelectionStart(a[j + 1][0]);
                        textArea.setSelectionEnd(a[j + 1][1]);
                        j++;
                    } else {
                        break;
                    }
                    i--;
                }
            }
        });
    }

    // "替换"
    private void replace() {
        replaceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog("输入查找的内容", null);
                if (s != null) {
                    String z = JOptionPane.showInputDialog("输入替换的内容", null);
                    int yes = JOptionPane.showConfirmDialog(null, "确定替换", "取消", JOptionPane.YES_NO_CANCEL_OPTION);
                    content = textArea.getText();
                    if (yes == JOptionPane.YES_OPTION) {
                        if (content.contains(s)) {
                            content = content.replace(s, z);
                            textArea.setText(content);
                        }
                    }
                }
            }
        });
    }

    // "插入时间"
    private void time() {
        timeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date date = new Date();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy.MM.dd'/'HH:mm:ss");
                content = textArea.getText() + ft.format(date);
                textArea.setText(content);
            }
        });
    }

    // "关于"
    private void about() {
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "记事本\nby: ryoua & bgmnbear");
            }
        });
    }

    public static void main(String[] args) {
        new Editor();
    }
}