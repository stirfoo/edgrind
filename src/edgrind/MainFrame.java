/*
  MainFrame.java
  S. Edward Dolan
  Wednesday, August  9 2023
*/

package edgrind;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
// 
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
// 
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// 
import java.io.File;
//
import edgrind.expert.*;
//
import edgrind.error.IllegalStateError;

/*
  TODO:
  * set the name of the program on New and Open
 */

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    protected Grinder grinder = null;
    protected EMProgram prog;
    // 
    protected OperationPanel opPanel;
    protected ProbePanel probePanel;
    protected GrindPanel[] grindPanels;
    protected ContourPanel contourPanel;
    protected ChuckPanel chuckPanel;
    protected ColletPanel colletPanel;
    protected WheelsPanel wheelsPanel;
    protected SimView simView;
    // 
    protected JMenuItem mitemSave;
    // 
    public MainFrame(boolean fullScreen) throws Exception {
        if (fullScreen)
            setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        prog = new EMProgram();
        setBounds(0, 0, 1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMenu();
        initTabs();
        setVisible(true);
        // grinder.dump();
    }
    private void initTabs() throws Exception {
        JTabbedPane progTP = new JTabbedPane(JTabbedPane.BOTTOM);
        progTP.add("Op Order", opPanel = new OperationPanel(this));
        progTP.add("Probe", probePanel = new ProbePanel(this));
        grindPanels = new GrindPanel[15];
        for (int i=1; i<16; ++i)
            progTP.add("Grind " + i,
                       grindPanels[i-1] = new GrindPanel(i, this));
        progTP.add("Contours", contourPanel = new ContourPanel(this));
        JTabbedPane mainTP = new JTabbedPane(JTabbedPane.BOTTOM);
        mainTP.add("Program", progTP);
        simView = new SimView(this); // must be before wheelsPanel creation
        mainTP.add("Wheels", wheelsPanel = new WheelsPanel(this));
        // the collet panel must be up before the chuck panel loads
        colletPanel = new ColletPanel(this);
        mainTP.add("Chuck", chuckPanel = new ChuckPanel(this));
        mainTP.add("Collet", colletPanel);
        SpindownPanel sdp = new SpindownPanel(this);
        mainTP.add("Spindown", sdp);
        // StepSpinPanel ssp = new StepSpinPanel(this);
        // mainTP.add("Step Spin", ssp);
        WeldonFlatPanel wfp = new WeldonFlatPanel(this);
        mainTP.add("Weldon Flat", wfp);
        mainTP.add("Simulation", simView);
        getContentPane().add(mainTP);
    }
    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        // ========================================================== F I L E
        JMenu menuFile = new JMenu("File");
        menuBar.add(menuFile);
        // ---------------------------------------------------------- O P E N
        JMenuItem mitemOpen = new JMenuItem("Open...");
        mitemOpen.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fc = new JFileChooser();
                    if (fc.showOpenDialog(MainFrame.this)
                        == JFileChooser.APPROVE_OPTION) {
                        try {
                            openProgram(fc.getSelectedFile().getPath());
                            // enable save if a file has been loaded
                            mitemSave.setEnabled(true);
                        }
                        catch(Exception ignore) {
                        }
                    }
                }
            });
        menuFile.add(mitemOpen);
        // menuFile.addSeparator();
        // ---------------------------------------------------------- S A V E
        mitemSave = new JMenuItem("Save");
        mitemSave.setEnabled(false);
        mitemSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        prog.save();
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, ex,
                                                      "EdGrind",
                                                      JOptionPane
                                                      .ERROR_MESSAGE);
                    }
                }
            });
        menuFile.add(mitemSave);
        // ---------------------------------------------------------- Q U I T
        JMenuItem mitemQuit = new JMenuItem("Quit");
        mitemQuit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        menuFile.add(mitemQuit);
        // ==================================================== G R I N D E R
        JMenu menuGrinder = new JMenu("Grinder");
        JRadioButtonMenuItem larry = new JRadioButtonMenuItem("Larry");
        larry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    grinder = Grinder.getGrinder("larry");
                }
            });
        if (!Config.hasGrinder("larry"))
            larry.setEnabled(false);
        JRadioButtonMenuItem moe = new JRadioButtonMenuItem("Moe");
        moe.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    grinder = Grinder.getGrinder("moe");
                }
            });
        if (!Config.hasGrinder("moe"))
            moe.setEnabled(false);
        JRadioButtonMenuItem curly = new JRadioButtonMenuItem("Curly");
        curly.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    grinder = Grinder.getGrinder("curly");
                }
            });
        if (!Config.hasGrinder("curly"))
            curly.setEnabled(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(larry);
        bg.add(moe);
        bg.add(curly);
        if (larry.isEnabled()) {
            larry.setSelected(true);
            grinder = Grinder.getGrinder("larry");
        }
        else if (moe.isEnabled()) {
            moe.setSelected(true);
            grinder = Grinder.getGrinder("moe");
        }
        else if (curly.isEnabled()) {
            curly.setSelected(true);
            grinder = Grinder.getGrinder("curly");
        }
        else
            throw new IllegalStateError("The \"menu-grinder\" array in" +
                                        " config.json must contain one" +
                                        " or more of the following grinder" +
                                        " grinder names: \"larry\", " +
                                        " \"moe\", and/or \"curly\".");
        menuGrinder.add(larry);
        menuGrinder.add(moe);
        menuGrinder.add(curly);
        menuBar.add(menuGrinder);
    }
    protected void openProgram(String fname) {
        try {
            EMDataReader r = new EMDataReader(fname);
            prog.clear();
            r.read(prog);
            mitemSave.setEnabled(true);
            // prog.dump();
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(MainFrame.this, e,
                                          "EdGrind",
                                          JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    void updateGrindName(String name, int page) {
        opPanel.updateGrindName(name, page);
    }
    public EMProgram getProgram() {
        return prog;
    }
    public Grinder getGrinder() {
        return grinder;
    }
    public void chuckChanged(String chuckName) {
        colletPanel.refreshColletList(chuckName);
    }
}
