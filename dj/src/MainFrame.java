import exception.BoundException;
import exception.IndexException;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.*;

import java.io.*;
import java.util.List;

public class MainFrame extends JFrame {
    private GraphPanel graphPanel = new GraphPanel();
    private JPanel mainPart = new JPanel(new BorderLayout());
    private JPanel title = new JPanel();
    private JPanel menu = new JPanel(new GridLayout(20,1,5,5));//菜单
    private JScrollPane menuB = new JScrollPane(menu);
    private JTextArea logArea = new JTextArea();
    private JPanel logPart = new JPanel(new BorderLayout());
    private JScrollPane log = new JScrollPane();//日志
    private  PreviousTablePanel table = new PreviousTablePanel();
    private JMenuBar topMenu = new JMenuBar();
    private Timer timer;
    private int speed = 1000;
    private List<Dijkstra.Step> steps;
    private int currentStep = 0;
    private Graph_interface graphInterface ;
    private Color menuColor = Color.LIGHT_GRAY;
    private Color mainColor = Color.WHITE;
    int V;
    int E = -1;
    int source = -1;
    int end = -1;
    String path;
    Dijkstra dijkstra;

    private JPanel jPanelV = new JPanel();
    private JPanel jPanelE = new JPanel();
    private JPanel jPanelS = new JPanel();
    private JPanel jPanelEnd = new JPanel();
    private JPanel jPanelAddES = new JPanel();
    private JPanel jPanelAddED = new JPanel();
    private JPanel jPanelAddEW = new JPanel();
    private JPanel jPanelRemoveES = new JPanel();
    private JPanel jPanelRemoveED = new JPanel();

    public MainFrame() {
        setTitle("Dijkstra Algorithm Visualizer");
        setTitle();
        setSize(1200, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        table.setVisible(false);
        table.setPreferredSize(new Dimension(150,600));

        graphPanel.setBackground(mainColor);

        mainPart.add(title,BorderLayout.NORTH);
        mainPart.add(graphPanel, BorderLayout.CENTER);
        mainPart.add(menuB,BorderLayout.EAST);
        mainPart.add(table,BorderLayout.WEST);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,mainPart,logPart);
        splitPane.setDividerLocation(600);
        splitPane.setContinuousLayout(true);

        add(splitPane,BorderLayout.CENTER);

        setMenu();
        setLog();
        setTopMenu();


    }

    private void baseRefresh(){
        if (source >= 0) {
            dijkstra = new Dijkstra(graphInterface, source);
            dijkstra.execute();
            steps = null;
        }

        graphPanel.setGraph(graphInterface, source);
    }

    private void refreshGraph(){
        graphInterface = graphInterface.refresh(V);
//        baseRefresh();
    }

    private int setSpeed(){
        String input = JOptionPane.showInputDialog(mainPart,"请输入每步演示间隔时间/ms");
        if (input == null)
            return -1;
        else if (input.isEmpty())
            return -1;
        else
            return Integer.parseInt(input);
    }

    private void startAnimation() {
        mark = false;
        EC = true;
        table.setVisible(true);
        graphPanel.setIfDrawWeight(true);
        graphPanel.setVisited();
        graphPanel.setCurrentDist();
        steps = dijkstra.getSteps();

        currentStep = 0;
        timer = new Timer(speed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < steps.size() && currentStep >= 0) {
                    graphPanel.applyStep(steps.get(currentStep));
                    table.updateTable(graphPanel);
                    currentStep++;
                } else {
                    graphPanel.setCurrentNode(-1);
                    graphPanel.setDestNode(-1);
                    graphPanel.repaint();
                    timer.stop();
                    timer = null;
                    EC = false;
                    log("动态演示结束！");
                }
            }
        });
        timer.start();
    }
    private void timeStop(){
        if (timer != null) {
            EC = false;
            timer.stop();
        }
    }

    private boolean EC;
    private void timeSuspend(){
        if (timer == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (steps == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (!EC){
            showError("动态演示已暂停！");
            return;
        }
        EC = false;
        timer.stop();
        log("动态演示已暂停！");
    }
    private void timeContinue(){
        if (steps == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (EC){
            showError("动态演示已继续！");
            return;
        }
        if (currentStep == steps.size()){
            showError("动态演示已结束！");
            log("动态演示已结束！");
            return;
        }
        mark = false;
        EC = true;
        log("动态演示继续！");
        timer = new Timer(speed, e -> {
            if (currentStep < steps.size() && currentStep >= 0) {
                graphPanel.applyStep(steps.get(currentStep));
                table.updateTable(graphPanel);
                currentStep++;
            } else {
                graphPanel.setCurrentNode(-1);
                graphPanel.setDestNode(-1);
                graphPanel.repaint();
                timer.stop();
                timer = null;
                EC = false;
                log("动态演示结束！");
            }
        });
        timer.start();
    }
    private void timeEnd(){
        if (steps == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (timer != null) {
            timer.stop();
            timer = null;
            currentStep = 0;
            graphPanel.setIfDrawWeight(false);
            table.setVisible(false);
            baseRefresh();
            log("动态演示已结束！");
            return;
        }
        currentStep = 0;
        graphPanel.setIfDrawWeight(false);
        table.setVisible(false);
        baseRefresh();
    }
    private boolean timeSpeed(){
        mark = false;
        if (timer != null) {
            EC = false;
            timer.stop();
            return true;
        }
        return false;
    }
    private boolean mark;
    private void timeStepNext(){
        timeStop();
        if (steps == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (mark) {
            currentStep++;
        }
        mark = false;
        EC = false;
        if (currentStep < steps.size() && currentStep >= 0) {
            graphPanel.applyStep(steps.get(currentStep));
            table.updateTable(graphPanel);
            currentStep++;
        } else {
            mark = true;
            graphPanel.setCurrentNode(-1);
            graphPanel.setDestNode(-1);
            graphPanel.repaint();
            showError("没有下一步了！");
            log("没有下一步了！");
        }
    }
    private void timeStepPrev(){
        timeStop();
        if (steps == null){
            showError("请先开始动态演示！");
            log("请先开始动态演示！");
            return;
        }
        if (!mark) {
            currentStep--;
            mark = true;
        }
        currentStep--;
        EC = false;
        if (currentStep < steps.size() && currentStep >= 0) {
            graphPanel.applyStep(steps.get(currentStep));
            table.updateTable(graphPanel);

            if (currentStep < (steps.size()-1)) {
                if (steps.get(currentStep+1).getType() == Dijkstra.StepType.NODE_SELECTED) {
                    graphPanel.setVisited(steps.get(currentStep+1).getU());
                } else if (steps.get(currentStep+1).getType() == Dijkstra.StepType.DISTANCE_UPDATED) {
                    graphPanel.setCurrentDist(steps.get(currentStep+1).getV());
                    graphPanel.setPreNode(steps.get(currentStep+1).getV());
                }
            }

//      applyStep()中的repaint()方法实际会在此处起作用
        } else {
            mark = false;
            currentStep = 0;
            graphPanel.setCurrentNode(-1);
            graphPanel.setDestNode(-1);
            graphPanel.repaint();
            showError("没有上一步了！");
            log("没有上一步了！");
        }
    }

    private void setTitle(){
        title.setBackground(menuColor);
        JLabel t = new JLabel("Dijkstra");
        t.setFont(new Font("Times New Romen",Font.BOLD,20));
        title.add(t,BorderLayout.CENTER);

    }

    private void setMenu(){
        menu.setBackground(menuColor);

        //设置V，E
        JTextField setV = new JTextField(10);
        setText(jPanelV, setV, "请填写顶点数量", "顶点数量：");

        //选择数据结构
        setDataStructure();

        //初始化
        JButton generateButton = new JButton("初始化");
        generateButton.addActionListener(e -> {
            if (graphInterface == null){
                showError("请先选择存储结构！");
                log("请先选择存储结构！");
                return;
            }
            source = -1;
            if (setGenerateV(setV)) {
                timeStop();
                table.setVisible(false);
                graphPanel.setIfDrawWeight(false);
                baseRefresh();
                log("初始化成功！");
            }
            else
                log("初始化失败！");
        });
        menu.add(generateButton);

        JTextField setE = new JTextField(10);
        setText(jPanelE, setE, "请填写边的数量", "边的数量：");

        //随机生成
        JButton randomG = new JButton("随机生成");
        randomG.addActionListener(e -> {
            if (graphInterface == null){
                showError("请先选择存储结构！");
                log("请先选择存储结构！");
                return;
            }

            source = -1;
            if (randomGraph(setV, setE)) {
                timeStop();
                table.setVisible(false);
                graphPanel.setIfDrawWeight(false);
                log("随机图生成成功！");
            }
            else
                log("随机图生成失败！");

        });
        menu.add(randomG);

        //设置源点
        JTextField setI = new JTextField(10);
        setText(jPanelS, setI,"请填写源点" ,"填写源点:");
        JButton sourceB = new JButton("设置源点");
        sourceB.addActionListener(e -> {
            if (graphInterface == null){
                showError("请先生成图！");
                log("设置源点失败！请先生成图！");
                return;
            }
            if (setSource(setI)) {
                currentStep = 0;
                log("设置源点成功！源点为" + source);
            }
        });
        menu.add(sourceB);

        //查询最短路径
        JTextField setO = new JTextField(10);
        setText(jPanelEnd, setO,"请填写终点" ,"填写终点:");
        JButton pathB = new JButton("查询最短路径");
        pathB.addActionListener(e -> {
            if (source >= 0) {
                setEnd(setO);
                if (end >= 0)
                    log(path + showDistance());
            }else {
                showError("请先设置源点！");
                log("查询最短路径失败！");
            }
        });
        menu.add(pathB);

        //动态演示
        JButton startButton = new JButton("动态演示");
        startButton.addActionListener(e -> {
            timeStop();
            if (source >= 0) {
                startAnimation();
                log("动态演示开始！");
            }else {
                showError("请先设置源点！");
                log("未设置源点！");
            }
        });
        menu.add(startButton);

        JPanel control = new JPanel(new BorderLayout());
        JPanel controlShow = new JPanel();
        JPanel stepShow = new JPanel();
        menu.add(control);
        control.add(controlShow,BorderLayout.NORTH);
        control.add(stepShow,BorderLayout.SOUTH);
        JButton suspend = new JButton("暂停");
        JButton go_on = new JButton("继续");
        JButton stop = new JButton("结束");
        JButton stepPrev = new JButton("上一步");
        JButton stepNext = new JButton("下一步");
        setButtonSize(suspend);
        setButtonSize(go_on);
        setButtonSize(stop);
        stepPrev.setPreferredSize(new Dimension(85,15));
        stepNext.setPreferredSize(new Dimension(85,15));
        controlShow.add(suspend);
        controlShow.add(go_on);
        controlShow.add(stop);
        stepShow.add(stepPrev);
        stepShow.add(stepNext);
        suspend.addActionListener(e -> {
            timeSuspend();
        });
        go_on.addActionListener(e -> {
            timeContinue();
        });
        stop.addActionListener(e -> {
            timeEnd();
        });
        stepPrev.addActionListener(e -> {
            timeStepPrev();
        });
        stepNext.addActionListener(e -> {
            timeStepNext();
        });

        //加边
        addE();

        //删边
        removeE();

    }
    private void setButtonSize(JButton jb){
        jb.setPreferredSize(new Dimension(59,15));
    }

    private void addE(){
        JTextField scr = new JTextField(10);
        JTextField dest = new JTextField(10);
        JTextField weight = new JTextField(10);

        setText(jPanelAddES, scr,"请填写加边起点","加边起点:");
        setText(jPanelAddED, dest,"请填写加边终点","加边终点:");
        setText(jPanelAddEW, weight,"请填写权值","设置权值:");

        JButton jb = new JButton("加边");
        menu.add(jb);
        jb.addActionListener(e -> {
            if (graphInterface == null){
                showError("请先生成图！");
                log("请先生成图！");
                return;
            }
            try {
                int a = Integer.parseInt(scr.getText());
                int b = Integer.parseInt(dest.getText());
                int c = Integer.parseInt(weight.getText());

                judge(a);
                judge(b);
                judgeW(c);
                int mark = graphInterface.checkE(a,b);
                if (mark == 1){
                    int r = JOptionPane.showConfirmDialog(null,"原边存在，是否覆盖？", "原边存在", JOptionPane.YES_NO_OPTION);
                    if (r == JOptionPane.YES_OPTION) {
                        graphInterface.addEdge(a, b, c);
                        log("已覆盖原边" + a +"->" + b + "，权值更新为" +c);
                    }
                    else
                        log("取消覆盖");
                }
//                if (mark == -1){
//                    int r = JOptionPane.showConfirmDialog(null,"反向边存在，是否覆盖？", "反向边存在", JOptionPane.YES_NO_OPTION);
//                    if (r == JOptionPane.YES_OPTION) {
//                        graphInterface.addEdge(a, b, c);
//                        graphInterface.removeEdge(b,a);
//                        log("已覆盖反向边" + b +"->" + a + "；覆盖为边" + a + "->" + b + "，权值为" +c);
//                    }
//                    else
//                        log("取消覆盖");
//                }
//                if (mark == 0)
                else
                    graphInterface.addEdge(a,b,c);
                log("成功添加边：" + a + "->" + b + "，权值为" +c);

                graphPanel.setIfDrawWeight(false);
                table.setVisible(false);
                timeStop();

            }catch (NumberFormatException exception){
                showError("请填写有效数字！");
                log("请填写有效数字！");
            }catch (IndexException exception){
                showError("请填写非负整数！");
                log("请填写非负整数！");
            }catch (BoundException exception){
                showError("顶点序数非法！");
                log("设置源点顶点序数应小于顶点数量！");
            }

            baseRefresh();
        });
    }

    private void removeE(){
        JTextField scr = new JTextField(10);
        JTextField dest = new JTextField(10);

        setText(jPanelRemoveES, scr,"请填写删边起点","删边起点:");
        setText(jPanelRemoveED, dest,"请填写删边终点","删边终点:");

        JButton jb = new JButton("删边");
        menu.add(jb);
        jb.addActionListener(e -> {
            if (graphInterface == null){
                showError("请先生成图！");
                log("请先生成图！");
                return;
            }
            try {
                int a = Integer.parseInt(scr.getText());
                int b = Integer.parseInt(dest.getText());

                judge(a);
                judge(b);

                int mark = graphInterface.checkE(a,b);
                if (mark == 1){
                    graphInterface.removeEdge(a,b);
                    log("删除边" + a + "->" + b +"成功！");
                }
                else {
                    showError("该边不存在！");
                    log("删除边失败！");
                }

                graphPanel.setIfDrawWeight(false);
                table.setVisible(false);
                timeStop();

            }catch (NumberFormatException exception){
                showError("请填写有效数字！");
                log("请填写有效数字！");
            }catch (IndexException exception){
                showError("请填写非负整数！");
                log("请填写非负整数！");
            }catch (BoundException exception){
                showError("顶点序数非法！");
                log("设置源点顶点序数应小于顶点数量！");
            }

            baseRefresh();
        });
    }

    private String showDistance(){
        if (dijkstra.getDistances()[end] == Integer.MAX_VALUE)
            return "";
        else {
            return "，长度为" + dijkstra.getDistances()[end];
        }
    }

    private boolean randomGraph(JTextField setV, JTextField setE){
        if (setGenerate(setV,setE)) {
            graphInterface.generateRandomGraph(V, E);
            baseRefresh();
            return true;
        }else
            return false;
    }

    private boolean setGenerate(JTextField setV, JTextField setE){
        if (setV.getText().equals("请填写顶点数量") && setE.getText().equals("请填写边的数量")){
            showError("请填写顶点数量和边的数量！");
            log("请填写顶点数量和边的数量！");
            return false;
        }
        if (setV.getText().equals("请填写顶点数量")){
            showError("请填写顶点数量！");
            log("请填写顶点数量！");
            return false;
        }
        if (setE.getText().equals("请填写边的数量")){
            showError("请填写边的数量！");
            log("请填写边的数量！");
            return false;
        }

        try {
            int v = Integer.parseInt(setV.getText());
            int e = Integer.parseInt(setE.getText());

            judgeV(v);
            V = v;
            judgeE(e);
            E = e;

        }catch (NumberFormatException exception){
            showError("请填写有效数字！");
            log("请填写有效数字！");
            return false;
        }catch (IndexException exception){
            showError("请填写非负整数！");
            log("请填写非负整数！");
            return false;
        }catch (BoundException exception){
            showError("边数超过最大边数！");
            log("设置边数应不超过"+V*(V-1)+"！");
            return false;
        }
        refreshGraph();
        return true;
    }
    private boolean setGenerateV(JTextField setV){
        if (setV.getText().equals("请填写顶点数量")){
            showError("请填写顶点数量！");
            log("请填写顶点数量！");
            return false;
        }

        try {
            int v = Integer.parseInt(setV.getText());

            judgeV(v);
            V = v;

        }catch (NumberFormatException exception){
            showError("请填写有效数字！");
            log("请填写有效数字！");
            return false;
        }catch (IndexException exception){
            showError("请填写非负整数！");
            log("请填写非负整数！");
            return false;
        }

        refreshGraph();
        return true;
    }

    private boolean setSource(JTextField setI){
        try {
            if (setI.getText().equals("请填写源点")){
                showError("请先设置源点！");
                log("请先设置源点！");
                return false;
            }
            int s = Integer.parseInt(setI.getText());
            judge(s);
            source = s;
            timeStop();
            graphPanel.setIfDrawWeight(false);
            table.setVisible(false);
            baseRefresh();
            return true;
        }catch (NumberFormatException exception){
            showError("请重新填写！");
            log("请填写有效数字！");
            return false;
        }catch (IndexException exception){
            showError("请填写非负整数！");
            log("请填写非负整数！");
            return false;
        }catch (BoundException exception){
            showError("顶点序数非法！");
            log("设置源点顶点序数应小于顶点数量！");
            return false;
        }
    }

    private void setEnd(JTextField set){
        try {
            if (set.getText().equals("请填写终点")){
                showError("请先设置终点！");
                log("请先设置终点！");
                return;
            }

            int s = Integer.parseInt(set.getText());
            judge(s);
            end = s;
            path = dijkstra.getPath(end);
        }catch (NumberFormatException exception){
            showError("请重新填写！");
            log("请填写有效数字！");
        }catch (IndexException exception){
            showError("请填写非负整数！");
            log("请填写非负整数！");
        }catch (BoundException exception){
            showError("顶点序数非法！");
            log("设置源点顶点序数应小于顶点数量！");
        }

    }

    private void setLog(){
        log.setViewportView(logArea);
        log.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        TitledBorder border = new TitledBorder("————> 日志 <————");
        border.setTitleFont(new Font("宋体",Font.BOLD,14));
        border.setTitleColor(new Color(37, 48, 64));

        log.setBorder(border);

        logPart.add(log);


//        Color color = new Color(92, 153, 129);
//
//        log.getViewport().setBackground(color);

        logArea.setText("");
        logArea.setEditable(false);
    }

    private void log(String msg) {
        logArea.append(new Date() + " " + msg + "\n");
    }

    private void setTopMenu(){
        setJMenuBar(topMenu);

        JMenu file = new JMenu("文件(Alt+F)");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem save = new JMenuItem("保存");
        save.setAccelerator(KeyStroke.getKeyStroke('S'));
        JMenuItem load = new JMenuItem("导入");
        load.setAccelerator(KeyStroke.getKeyStroke('L'));
        JMenuItem downLoad = new JMenuItem("下载");
        downLoad.setAccelerator(KeyStroke.getKeyStroke('D'));
        file.add(save);
        file.add(load);
        file.add(downLoad);
        save.addActionListener(e -> {
            saveFile();
        });
        load.addActionListener(e -> {
            loadFile();
        });
        downLoad.addActionListener(e -> {
            FileChooserPane fileChooserPane = new FileChooserPane();
            fileChooserPane.setVisible(true);
            String fileName = fileChooserPane.getFileName();
            downLoadFile(fileName);
        });

        JMenu help = new JMenu("帮助(Alt+H)");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem about = new JMenuItem("关于");
        about.setAccelerator(KeyStroke.getKeyStroke('A'));
        JMenuItem introduce = new JMenuItem("功能介绍");
        introduce.setAccelerator(KeyStroke.getKeyStroke('I'));
        JMenuItem introduceDJ = new JMenuItem("算法介绍");
        introduceDJ.setAccelerator(KeyStroke.getKeyStroke('N'));
        help.add(about);
        help.add(introduce);
        help.add(introduceDJ);
        about.addActionListener(e -> JOptionPane.showMessageDialog(null,"\\^o^/伟大的\\^o^/\n山东大学软件学院23级李世俗制作的\nDijkstra算法可视化数据结构课设"));
        introduce.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "一、有向边的权重标注靠近该边的起点\n" +
                        "二、动态演示：" +
                "\n1.源点标记为蓝色" +
                "\n2.当前节点标记为红色" +
                "\n3.已访问节点标记为灰色" +
                "\n4.节点旁的红色数字为最短路径" +
                "\n5.正在遍历的边会变红加粗"));
        introduceDJ.addActionListener(e -> JOptionPane.showMessageDialog(null,
                "迪杰斯特拉算法采用贪心策略，通过逐步扩展已知的最短路径集合来求解。\n具体步骤如下：\n" +
                "\n" +
                "1.初始化：\n" +
                "\n" +
                "-设置起点的最短路径为 0，其他节点的最短路径为无穷大（表示尚未访问）。\n" +
                "\n" +
                "-将所有节点标记为未访问。\n" +
                "\n" +
                "2.选择当前距离起点最近的未访问节点，作为当前节点。\n" +
                "\n" +
                "3.对当前节点的所有邻居节点，更新其到起点的最短路径：\n" +
                "\n" +
                "-如果通过当前节点到达邻居节点的路径更短，则更新邻居节点的最短路径。\n" +
                "\n" +
                "4.将当前节点标记为已访问。\n" +
                "\n" +
                "5.重复步骤 2~4，直到所有节点（除与源点不连通的节点）都被访问。"));

        JMenu setSpeed = new JMenu("动态演示速度设置(Alt+S)");
        setSpeed.setMnemonic(KeyEvent.VK_S);
        JMenuItem fast = new JMenuItem("快");
        fast.setAccelerator(KeyStroke.getKeyStroke('F'));
        JMenuItem medium = new JMenuItem("中");
        medium.setAccelerator(KeyStroke.getKeyStroke('M'));
        JMenuItem slow = new JMenuItem("慢");
        slow.setAccelerator(KeyStroke.getKeyStroke('Q'));
        JMenuItem customize = new JMenuItem("自定义");
        customize.setAccelerator(KeyStroke.getKeyStroke('C'));
        setSpeed.add(fast);
        setSpeed.add(medium);
        setSpeed.add(slow);
        setSpeed.add(customize);
        fast.addActionListener(e -> {
            speed = 500;
            if (timeSpeed())
                log("动态演示速度设置为快速！请按继续！");
            else
                log("动态演示速度设置为快速！");
        });
        medium.addActionListener(e -> {
            speed = 1000;
            if (timeSpeed())
                log("动态演示速度设置为中速！请按继续！");
            else
                log("动态演示速度设置为中速！");
        });
        slow.addActionListener(e -> {
            speed = 2000;
            if (timeSpeed())
                log("动态演示速度设置为慢速！请按继续！");
            else
                log("动态演示速度设置为慢速！");
        });
        customize.addActionListener(e -> {
            int temp = setSpeed();
            if (temp != -1) {
                speed = temp;
                if (timeSpeed())
                    log("动态演示每步间隔时间自定义设置为" + speed + "ms！请按继续！");
                else
                    log("动态演示每步间隔时间自定义设置为" + speed + "ms！");
            }
        });

        JMenu setColor = new JMenu("界面颜色(Alt+C)");
        setColor.setMnemonic(KeyEvent.VK_C);
        JMenuItem init = new JMenuItem("默认");
        init.setAccelerator(KeyStroke.getKeyStroke('R'));
        setColor.add(init);
        JMenuItem green = new JMenuItem("绿色");
        green.setAccelerator(KeyStroke.getKeyStroke('G'));
        setColor.add(green);
        JMenuItem blue = new JMenuItem("蓝色");
        blue.setAccelerator(KeyStroke.getKeyStroke('B'));
        setColor.add(blue);
        JMenuItem pink = new JMenuItem("粉色");
        pink.setAccelerator(KeyStroke.getKeyStroke('P'));
        setColor.add(pink);
        JMenuItem orange = new JMenuItem("橙色");
        orange.setAccelerator(KeyStroke.getKeyStroke('O'));
        setColor.add(orange);
        init.addActionListener(e -> {
            menuColor = Color.LIGHT_GRAY;
            mainColor = Color.WHITE;
            setColor();
            log("界面颜色已设置为默认！");
        });
        green.addActionListener(e -> {
            menuColor = new Color(105, 170, 94);
            mainColor = new Color(175, 221, 163);
            setColor();
            log("界面颜色已设置为绿色！");
        });
        blue.addActionListener(e -> {
            menuColor = new Color(133, 185, 250);
            mainColor = new Color(182, 220, 250);
            setColor();
            log("界面颜色已设置为蓝色！");
        });
        pink.addActionListener(e -> {
            menuColor = new Color(246, 157, 186);
            mainColor = new Color(255, 210, 229);
            setColor();
            log("界面颜色已设置为粉色！");
        });
        orange.addActionListener(e -> {
            menuColor = new Color(255, 165, 105);
            mainColor = new Color(248, 195, 158);
            setColor();
            log("界面颜色已设置为橙色！");
        });

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2,20));
        JSeparator separator1 = new JSeparator(SwingConstants.VERTICAL);
        separator1.setPreferredSize(new Dimension(2,20));
        JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
        separator2.setPreferredSize(new Dimension(2,20));
        JSeparator separator3 = new JSeparator(SwingConstants.VERTICAL);
        separator3.setPreferredSize(new Dimension(2,20));

        topMenu.add(file);
        topMenu.add(separator);
        topMenu.add(help);
        topMenu.add(separator1);
        topMenu.add(setSpeed);
        topMenu.add(separator2);
        topMenu.add(setColor);
        topMenu.add(separator3);
    }

    private void setColor(){
        menu.setBackground(menuColor);
        title.setBackground(menuColor);
        graphPanel.setBackground(mainColor);
        jPanelV.setBackground(menuColor);
        jPanelE.setBackground(menuColor);
        jPanelS.setBackground(menuColor);
        jPanelEnd.setBackground(menuColor);
        jPanelAddES.setBackground(menuColor);
        jPanelAddED.setBackground(menuColor);
        jPanelAddEW.setBackground(menuColor);
        jPanelRemoveES.setBackground(menuColor);
        jPanelRemoveED.setBackground(menuColor);
        logArea.setBackground(mainColor);
    }

    private void downLoadFile(String fileName){
        if (fileName == "null"){
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        // 获取程序所在路径
        String programPath = System.getProperty("user.dir");
        File programDirectory = new File(programPath);
//        fileChooser.setSelectedFile(new File("示例文件.txt"));
        // 设置默认路径为程序所在路径
        fileChooser.setCurrentDirectory(programDirectory);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "TXT Files";
            }
        });
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION){
            String savePath = fileChooser.getSelectedFile().getPath();
            sendDownloadRequest(fileName,savePath);
        }

    }

    private void sendDownloadRequest(String fileName, String savePath) {
        try (Socket socket = new Socket("127.0.0.1", 8888)) {
            PrintStream out = new PrintStream(socket.getOutputStream());
            out.println("DOWNLOAD" + fileName);
            // 接收文件数据
            InputStream in = socket.getInputStream();
            FileOutputStream fos = new FileOutputStream(savePath + "/" + fileName);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            in.close();
            JOptionPane.showMessageDialog(this, "文件下载完成！");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "文件下载失败！");
        }
    }


    private void setText(JPanel jp, JTextField jt, String tipIn, String tipOut){
        jp.setBackground(menuColor);
        jt.setText(tipIn);
        jt.setForeground(Color.GRAY);
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(jt.getText().equals(tipIn)){
                    jt.setText("");
                    jt.setForeground(Color.BLACK);
                    jt.setCaretPosition(0);
                }
            }
        });
        jt.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if(jt.getText().isEmpty()){
                    jt.setText(tipIn);
                    jt.setForeground(Color.GRAY);
                }
            }
        });
        jp.add(new JLabel(tipOut));
        jp.add(jt);
        menu.add(jp);
    }

    private void judgeV(int v) throws IndexException{
        if(v < 0)
            throw new IndexException();
    }

    private void judgeE(int e) throws IndexException,BoundException{
        int maxE = V*(V-1);
        if (e < 0)
            throw new IndexException();
        else if (e > maxE)
            throw new BoundException();
    }
    private void judge(int v) throws IndexException,BoundException{
        if(v < 0)
            throw new IndexException();
        else if(v >= V)
            throw new BoundException();
    }
    private void judgeW(int w) throws IndexException{
        if(w < 0)
            throw new IndexException();
    }
    private void setDataStructure(){

        JPanel jp = new JPanel(new BorderLayout(5,5));
        jp.add(new JLabel("请选择存储结构："),BorderLayout.NORTH);

        ButtonGroup group = new ButtonGroup();
        JToggleButton c1 = new JToggleButton("邻接矩阵");
        JToggleButton c2 = new JToggleButton("邻接链表");
        group.add(c1);
        group.add(c2);

        jp.add(c1,BorderLayout.WEST);
        jp.add(c2,BorderLayout.EAST);
        menu.add(jp);

        c1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeStop();
                if (graphInterface == null)
                    graphInterface = new AdjacencyMatrixGraph(V);
                else
                    graphInterface = graphInterface.changeStructure(graphInterface);
                log("存储结构更改为邻接矩阵！");
            }
        });
        c2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeStop();
                if (graphInterface == null)
                    graphInterface = new AdjacencyListGraph(V);
                else
                    graphInterface = graphInterface.changeStructure(graphInterface);
                log("存储结构更改为邻接链表！");
            }
        });
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }

    public void saveFile(){
        if (graphInterface == null){
            showError("请先生成图！");
            log("请先生成图！");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        // 获取程序所在路径
        String programPath = System.getProperty("user.dir");
        File programDirectory = new File(programPath);

        fileChooser.setSelectedFile(new File("默认文件名.txt"));
        //这行代码的作用是为文件选择对话框 (JFileChooser) 设置一个默认选中的文件。具体来说，当文件选择对话框打开时，对话框中会预先填充这个文件名，方便用户快速选择或保存文件。

        // 设置默认路径为程序所在路径
        fileChooser.setCurrentDirectory(programDirectory);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "TXT Files";
            }
        });
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                // 保存节点信息
                    writer.printf("N %s%n", graphInterface.getV());
                // 保存边信息
                saveEdge(writer);

                JOptionPane.showMessageDialog(this, "保存成功!");
                log("保存成功！");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
                log("保存失败！");
            }
        }
    }

    private void saveEdge(PrintWriter writer){
        if (graphInterface instanceof AdjacencyListGraph)
            saveEdgeForAdjacencyListGraph(writer);
        else
            saveEdgeForAdjacencyMatrixGraph(writer);
    }

    private void saveEdgeForAdjacencyListGraph(PrintWriter writer){
        for (int u = 0; u< graphInterface.getV(); u++)
            for (Graph_interface.Edge edge : graphInterface.getAdjList().get(u)){
                writer.printf("E %s %s %d%n", u, edge.dest, edge.weight);
            }
    }
    private void saveEdgeForAdjacencyMatrixGraph(PrintWriter writer){
        for (int u = 0; u < graphInterface.getV(); u++)
            for (int d = 0; d < graphInterface.getV(); d++){
                if (d == u)
                    continue;
                if (graphInterface.getAdjMatrix()[u][d] == Integer.MAX_VALUE)
                    continue;
                writer.printf("E %s %s %d%n", u, d, graphInterface.getAdjMatrix()[u][d]);
            }
    }

    public void loadFile(){
        JFileChooser fileChooser = new JFileChooser();
        // 获取程序所在路径
        String programPath = System.getProperty("user.dir");
        File programDirectory = new File(programPath);

        // 设置默认路径为程序所在路径
        fileChooser.setCurrentDirectory(programDirectory);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "TXT Files";
            }
        });
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Scanner scanner = new Scanner(file)) {
                int numV = 0;
                List<List<Graph_interface.Edge>> list = null;
                if (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    String[] parts = line.split(" ");
                    if (parts[0].equals("N")) {
                        // 解析节点: N numOfV
                        numV = Integer.parseInt(parts[1]);
                        list = new ArrayList<>(numV);
                        for (int i = 0; i < numV; i++)
                            list.add(new ArrayList<>());
                    }
                    else {
                        showError("内容格式错误！");
                        log("内容格式错误！第一行必须为\"‘N’+顶点数量\"！");
                        return;
                    }
                }
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(" ");

                    if (parts[0].equals("E")) {
                        // 解析边: E from to weight
                        int from = Integer.parseInt(parts[1]);
                        int to = Integer.parseInt(parts[2]);
                        int weight = Integer.parseInt(parts[3]);

                        if (from < numV && from >= 0 && to < numV && to >= 0) {
                            list.get(from).add(new Graph_interface.Edge(to, weight));
                        }
                    }
                }
                graphInterface = new AdjacencyListGraph(numV, list);
                V = numV;
                JOptionPane.showMessageDialog(this, "导入成功！");
                log("导入成功！");
                source = -1;
                baseRefresh();
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
                log("导入失败！");
            }
        }
    }
}