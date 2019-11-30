package dev.tty.nfcv.lottery.draw;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashSet;

import static dev.tty.nfcv.lottery.draw.Config.APPLICATION_NAME;

class LotteryDrawFrame extends JFrame implements FunctionPanel.OnStartPressedListener {

    interface OnResultGenListener {
        void onResult(ArrayList<Model.Result> results);
    }

    OnResultGenListener listener;

    LotteryDrawFrame() {
        super(APPLICATION_NAME);
        setSize(Config.WIDTH + 10, Config.HEIGHT);
        setResizable(false);
        setLayout(null);
        setBackground(Color.LIGHT_GRAY);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        MainMenuBar mainMenuBar = new MainMenuBar(this);
        FunctionPanel functionPanel = new FunctionPanel(this);
        ResultListPanel userListPanel = new ResultListPanel(this);
        listener = userListPanel;
    }

    void display() {
        this.setVisible(true);
    }

    @Override
    public void onStartPressed(int first, int second, int third, ArrayList<Model.User> users) {
        System.out.println("first: " + first);
        System.out.println("second: " + second);
        System.out.println("third: " + third);
        System.out.println("观众名单");
        for (Model.User user : users) {
            System.out.println(user);
        }
        System.out.println();
        listener.onResult(Model.Result.getResult(first, second, third, users));
    }
}

class MainMenuBar extends JMenuBar {
    MainMenuBar(JFrame parent) {
        setBackground(Color.WHITE);
        JMenu recordMenu = new JMenu("记录");
        JMenuItem recordMenuOpen = new JMenuItem("打开");
        recordMenu.add(recordMenuOpen);
        JMenuItem recordMenuSave = new JMenuItem("保存");
        recordMenu.add(recordMenuSave);
        add(recordMenu);
        parent.setJMenuBar(this);

        recordMenuOpen.addActionListener(e1 -> {

        });
    }
}

class FunctionPanel extends JPanel {
    interface OnStartPressedListener {
        void onStartPressed(int first, int second, int third, ArrayList<Model.User> users);
    }
    private OnStartPressedListener listener;
    FunctionPanel(JFrame parent) {
        int w = Config.WIDTH / 3;
        int h = Config.HEIGHT;
        setBounds(0, 0, w, h);
        setBackground(Color.WHITE);
        parent.add(this);
        setLayout(null);
        if (parent instanceof OnStartPressedListener) {
            listener = (OnStartPressedListener) parent;
        } else {
            throw new IllegalArgumentException("the parent should implements OnStartPressedListener");
        }
        JLabel themeLabel = new JLabel("主题");
        themeLabel.setBounds(4, 4, 40, 24);
        JTextField themeField = new JTextField(16);
        themeField.setBounds(48, 4, w - 60, 24);
        add(themeLabel);
        add(themeField);
        PrizeItemPanel firstPrize = new PrizeItemPanel(this, "一等奖");
        firstPrize.setBounds(0, 48, w, 32);
        PrizeItemPanel secondPrize = new PrizeItemPanel(this, "二等奖");
        secondPrize.setBounds(0, 80, w, 32);
        PrizeItemPanel thirdPrize = new PrizeItemPanel(this, "三等奖");
        thirdPrize.setBounds(0, 112, w, 32);

        JButton start = new JButton("开始抽奖");
        start.setBounds(4, 148, w - 8, 28);
        start.setBorderPainted(false);
        start.setBackground(Color.WHITE);
        add(start);

        UserTable userTable = new UserTable(this);

        start.addActionListener(e -> {
            listener.onStartPressed(firstPrize.size, secondPrize.size, thirdPrize.size, userTable.getUsers());
        });
    }
}

class PrizeItemPanel extends JPanel {
    int size = 0;
    PrizeItemPanel(JPanel parent, String prizeName) {
        setSize(parent.getWidth(), 32);
        setBackground(Color.WHITE);
        setLayout(null);
        JLabel name = new JLabel(prizeName);
        name.setBounds(4, 4, 96, 24);
        add(name);
        JButton sub = new JButton("-");
        sub.setBorderPainted(false);
        sub.setMargin(new Insets(0, 0, 0, 0));
        sub.setBackground(Color.WHITE);
        JButton add = new JButton("+");
        add.setBorderPainted(false);
        add.setMargin(new Insets(0, 0, 0, 0));
        add.setBackground(Color.WHITE);
        sub.setBounds(152, 4, 24, 24);
        JLabel sizeLabel = new JLabel(String.valueOf(size));
        sizeLabel.setHorizontalAlignment(JLabel.CENTER);
        sizeLabel.setBounds(176, 4, 24, 24);
        add.setBounds(200, 4, 24, 24);
        add(sub);
        add(sizeLabel);
        add(add);
        add.addActionListener(e -> {
            if (size <= 0) {
                size=1;
            } else {
                size++;
            }
            sizeLabel.setText(String.valueOf(size));
        });
        sub.addActionListener(e -> {
            if (size <= 0) {
                size = 0;
            } else {
                size--;
            }
            sizeLabel.setText(String.valueOf(size));
        });
        parent.add(this);
    }
}

class ResultListPanel extends JPanel implements LotteryDrawFrame.OnResultGenListener {
    ResultListPanel(JFrame parent) {
        int w = Config.WIDTH * 2 / 3;
        int h = Config.HEIGHT;
        setBounds(w / 2 + 10, 0, w, h);
        setBackground(Color.WHITE);
        ResultTable resultTable = new ResultTable(this);
        parent.add(this);
    }

    @Override
    public void onResult(ArrayList<Model.Result> results) {
        System.out.println("获奖名单");
        for (Model.Result result : results) {
            System.out.println(result);
        }
        System.out.println();
    }
}

class UserTable extends JTable {
    DefaultTableModel model;
    UserTable(JPanel parent) {
        super(0, 2);
        setBackground(Color.LIGHT_GRAY);
        JScrollPane pane = new JScrollPane(this);
        pane.setBounds(0, 180, 256, 250);
        getColumnModel().getColumn(0).setHeaderValue("姓名");
        getColumnModel().getColumn(1).setHeaderValue("电话");
        parent.add(pane);
        setRowHeight(25);
        model = (DefaultTableModel)getModel();
        model.addRow(new String[] {"", ""});
        model.addTableModelListener(e -> {
            int rows = getRowCount();
            int row = getSelectedRow();
            int column = getSelectedColumn();
            if (row == rows - 1 && column == 1 && !getValueAt(row, 0).equals("")) {
                model.addRow(new String[] {"", ""});
            } else if (row == rows - 1 && column == 0 && !getValueAt(row, 1).equals("")) {
                model.addRow(new String[] {"", ""});
            } else if (row < rows - 1 && getValueAt(row, 0).equals("") && getValueAt(row, 1).equals("")) {
                model.removeRow(row);
            }
        });
    }

    ArrayList<Model.User> getUsers() {
        HashSet<Model.User> users = new HashSet<>();
        int rows = model.getRowCount();
        for (int i = 0; i < rows; i++) {
            String name = (String) model.getValueAt(i, 0);
            String phone = (String) model.getValueAt(i, 1);
            if (name.equals("") || phone.equals("")) {
                continue;
            }
            users.add(new Model.User(name, phone));
        }
        return new ArrayList<>(users);
    }
}

class ResultTable extends JTable {
    ResultTable(JPanel parent) {
        super(0, 3);
        JScrollPane pane = new JScrollPane(this);
        setEnabled(false);
        setRowHeight(25);
        pane.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        getColumnModel().getColumn(0).setHeaderValue("姓名");
        getColumnModel().getColumn(1).setHeaderValue("电话");
        getColumnModel().getColumn(2).setHeaderValue("奖项");
        parent.add(pane);
    }
}