/*
  SpindownPanel.java
  S. Edward Dolan
  Wednesday, November  8 2023
*/

package edgrind;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
// 
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
// 
import java.awt.event.ItemEvent;      // for step count combo...
import java.awt.event.ItemListener;   // ...change
import java.awt.event.ActionListener; // for write program...
import java.awt.event.ActionEvent;    // ...button click
// 
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
// 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
//
import edgrind.sketch.Sketch;
import edgrind.sketch.SketchScene;
import edgrind.sketch.SpindownSketch2;
import edgrind.sketch.SpindownSketch3;
import edgrind.sketch.SpindownSketch4;
import edgrind.sketch.SpindownSketch5;
//
import edgrind.error.EdError;
//
import edgrind.expert.EMProgram;

/*
  .---------------------------------.----------------------------------.
  |  Diameters: |1             |v|  |                                   |
  | .- Grind Parameters ----------. |                                   |
  | |         Spindle: |1     |v| | |                                   |
  | |           Wheel: |1     |v| | |                                   |
  | |       Wheel RPM: __________ | |                                   |
  | |        Y Infeed: __________ | |                                   |
  | |      Y Feedrate: __________ | |                                   |
  | |      A Feedrate: __________ | |                                   |
  | |     Step-Over %: __________ | |           Sketch Scene            |
  | | [ ] Finish Pass? __________ | |                                   |
  | | .- Coolant Valves --------. | |                                   |
  | | |   [ ]3   [ ]2   [ ]1    | | |                                   |
  | | '-------------------------' | |                                   |
  | '-----------------------------' |                                   |
  |         |Write Program|         |                                   |
  |                                 |                                   |
  |                                 |                                   |
  '---------------------------------'-----------------------------------'
 */
@SuppressWarnings("serial")
public class SpindownPanel extends JPanel implements ActionListener,
                                                     ItemListener {
    /** Maximum step diameters including the non-ground stock diameter. */
    static final int MAX_DIAMETERS = 5;
    MainFrame mainFrame;
    SSParamsPanel paramsPanel;
    SketchScene sketchScene;
    JComboBox<Integer> cboDiameters;
    JButton butWriteProgram;
    SpindownPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        int nextGridY = 0;
        setLayout(gb);
        gc.insets = new Insets(3, 3, 3, 3);
        // --------------------------------------------------
        // Diameters: |1        |v|
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel("Diameters:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cboDiameters = new JComboBox<Integer>();
        for (int i=0; i<MAX_DIAMETERS-1; ++i)
            cboDiameters.addItem(i + 2);
        cboDiameters.addItemListener(this);
        gb.setConstraints(cboDiameters, gc);
        add(cboDiameters);
        // ---------------------------------------------------
        // Params Panel
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.fill = GridBagConstraints.NONE;
        paramsPanel = new SSParamsPanel(mainFrame);
        gb.setConstraints(paramsPanel, gc);
        add(paramsPanel);
        // --------------------------------------------------
        // Write Button
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        butWriteProgram = new JButton("Write Program");
        butWriteProgram.addActionListener(this);
        gb.setConstraints(butWriteProgram, gc);
        add(butWriteProgram);
        // --------------------------------------------------
        // Sketch Scene
        gc.insets = new Insets(0, 0, 0, 0);
        gc.gridx = 2;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridheight = 4;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.fill = GridBagConstraints.BOTH;
        sketchScene = new SketchScene();
        sketchScene.addSketch(new SpindownSketch2(sketchScene));
        sketchScene.getSketch(0).setFillShape(true);
        gb.setConstraints(sketchScene, gc);
        add(sketchScene);
        //
        cboDiameters.setSelectedItem(1);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butWriteProgram)
            paramsPanel.writeProgram(sketchScene.getSketch(0),
                                     (int)cboDiameters.getSelectedItem());
    }
    /**
       Load a new sketch based on the number of steps requested.
    */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboDiameters) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int n = (int)e.getItem();
                Dict d = new Dict();
                for (int i=1; i<=n; ++i) {
                    d.put("d" + i, i * .1);
                    d.put("l" + i, i * .1);
                }
                sketchScene.removeSketch(sketchScene.getSketch(0));
                switch (n) {
                    case 2:
                        sketchScene
                            .addSketch(new SpindownSketch2(sketchScene, d));
                        break;
                    case 3:
                        sketchScene
                            .addSketch(new SpindownSketch3(sketchScene, d));
                        break;
                    case 4:
                        sketchScene
                            .addSketch(new SpindownSketch4(sketchScene, d));
                        break;
                    case 5:
                        sketchScene
                            .addSketch(new SpindownSketch5(sketchScene, d));
                        break;
                }
                sketchScene.getSketch(0).setFillShape(true);
                sketchScene.fitAll();
            }
        }
    }
}

/*
  .- Grind Parameters ----------.
  |         Spindle: |1     |v| |
  |           Wheel: |1     |v| |
  |       Wheel RPM: __________ |
  |        Y Infeed: __________ |
  |      Y Feedrate: __________ |
  |      A Feedrate: __________ |
  | [ ] Finish Pass? __________ |
  |     Step-Over %: __________ |
  | .- Coolant Valves --------. |
  | |   [ ]3   [ ]2   [ ]1    | |
  | '-------------------------' |
  '-----------------------------'
*/
@SuppressWarnings("serial")
class SSParamsPanel extends JPanel implements ItemListener, ActionListener {
    /** Total grind pages available */
    protected static final int MAX_GRIND_PAGES = 15;
    /** Amount to add to the y infeed. */
    protected static final double RAPID_Y = .015;
    /** Amount to add to the y infeed. */
    protected static final double SPRING_PLUNGE_Y = .01;
    /** Max distance the X axis may travel past the X prob point. */
    protected static final double PROBE_OVERSHOOT_X = .05;
    // 
    protected MainFrame mainFrame;
    protected JComboBox<Integer> cboSpindle;
    protected JComboBox<Integer> cboWheel;
    protected IntEdit txtWheelRPM;
    protected FloatEdit txtYInfeed;
    protected FloatEdit txtYFeedrate;
    protected FloatEdit txtAFeedrate;
    protected JCheckBox chkFinishPass;
    protected FloatEdit txtFinishPass;
    protected IntEdit txtStepOver;
    protected CoolantValvesPanel valvesPanel;
    SSParamsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        TitledBorder tbdr = new TitledBorder("Grind Parameters");
        setBorder(BorderFactory
                  .createCompoundBorder(tbdr,
                                        BorderFactory
                                        .createEmptyBorder(5, 5, 5, 5)));
        setLayout(gb);
        int nextGridY = 0;
        Insets ins = new Insets(2, 2, 2, 2);
        // --------------------------------------------------
        // Spindle: |      |v|
        gc.insets = ins;
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel("Spindle:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cboSpindle = new JComboBox<Integer>();
        cboSpindle.addItem(1);
        cboSpindle.addItem(2);
        cboSpindle.addItemListener(this);
        gb.setConstraints(cboSpindle, gc);
        add(cboSpindle);
        // --------------------------------------------------
        // Wheel: |      |v|
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Wheel:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cboWheel = new JComboBox<Integer>();
        cboWheel.addItem(1);
        cboWheel.addItem(2);
        cboWheel.addItem(3);
        lbl.setLabelFor(cboWheel);
        gb.setConstraints(cboWheel, gc);
        add(cboWheel);
        // --------------------------------------------------
        // Wheel RPM: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Wheel RPM:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtWheelRPM = new IntEdit(-2750, -5000, 5000,
                                  LineEdit.ALLOW_NEGATIVE);
        txtWheelRPM.setMinimumSize(txtWheelRPM.getPreferredSize());
        txtWheelRPM.setToolTipText("use a negative number to reverse" +
                                   " direction");
        lbl.setLabelFor(txtWheelRPM);
        gb.setConstraints(txtWheelRPM, gc);
        add(txtWheelRPM);
        // --------------------------------------------------
        // Y Passes: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Y Infeed:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtYInfeed = new FloatEdit(.02, .001, .1);
        txtYInfeed.setMinimumSize(txtYInfeed.getPreferredSize());
        txtYInfeed.setToolTipText("maximum Y step-down per A revolution");
        lbl.setLabelFor(txtYInfeed);
        gb.setConstraints(txtYInfeed, gc);
        add(txtYInfeed);
        // --------------------------------------------------
        // Y Feed: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Y Feed:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtYFeedrate = new FloatEdit(0.25, .01, 20);
        txtYFeedrate.setMinimumSize(txtYFeedrate.getPreferredSize());
        txtYFeedrate.setToolTipText("Y axis plunge feedrate");
        lbl.setLabelFor(txtYFeedrate);
        gb.setConstraints(txtYFeedrate, gc);
        add(txtYFeedrate);
        // --------------------------------------------------
        // A Feed: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("A Feed:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtAFeedrate = new FloatEdit(7.0, .01, 20);
        txtAFeedrate.setMinimumSize(txtAFeedrate.getPreferredSize());
        txtAFeedrate.setToolTipText("A axis rotary feedrate");
        lbl.setLabelFor(txtAFeedrate);
        gb.setConstraints(txtAFeedrate, gc);
        add(txtAFeedrate);
        // --------------------------------------------------
        // Step-Over %: ____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Step-Over %:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtStepOver = new IntEdit(85, 1, 100);
        txtStepOver.setToolTipText("max wheel step-over percentage");
        txtStepOver.setMinimumSize(txtStepOver.getPreferredSize());
        lbl.setLabelFor(txtStepOver);
        gb.setConstraints(txtStepOver, gc);
        add(txtStepOver);
        // --------------------------------------------------
        // Finish Pass? ____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        chkFinishPass = new JCheckBox("Finish Pass?");
        chkFinishPass.addActionListener(this);
        gb.setConstraints(chkFinishPass, gc);
        add(chkFinishPass);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtFinishPass = new FloatEdit(0, 0, .02, LineEdit.ALLOW_ZERO);
        txtFinishPass.setToolTipText("enter 0 for a spring (spark-out) pass");
        txtFinishPass.setMinimumSize(txtFinishPass.getPreferredSize());
        txtFinishPass.setEnabled(false);
        gb.setConstraints(txtFinishPass, gc);
        add(txtFinishPass);
        // --------------------------------------------------
        // Coolant Valves
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        valvesPanel = new CoolantValvesPanel(1);
        gb.setConstraints(valvesPanel, gc);
        add(valvesPanel);
    }
    public int getSpindleNum() {
        return (int)cboSpindle.getSelectedItem();
    }
    public int getWheelNum() {
        return (int)cboWheel.getSelectedItem();
    }
    public int getWheelRPM() {
        return txtWheelRPM.getValue();
    }
    public double getYInfeed() {
        return txtYInfeed.getValue();
    }
    public double getYFeedrate() {
        return txtYFeedrate.getValue();
    }
    public double getAFeedrate() {
        return txtAFeedrate.getValue();
    }
    public int getStepOver() {
        return txtStepOver.getValue();
    }
    public boolean doFinishPass() {
        return chkFinishPass.isSelected();
    }
    public double getFinishPass() {
        return txtFinishPass.getValue();
    }
    public String getValvesAsString() {
        return valvesPanel.getOpenValves();
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboSpindle)
            if (e.getStateChange() == ItemEvent.SELECTED)
                valvesPanel.setSpindle((int)e.getItem());
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chkFinishPass)
            txtFinishPass.setEnabled(chkFinishPass.isSelected());
    }
    protected boolean writeProgram(Sketch sketch, int nDias) {
        Dict wheelData = mainFrame.wheelsPanel.getWheelData(getSpindleNum(),
                                                            getWheelNum());
        if (wheelData == null)
            return false;       // invalid wheel
        Wheel wheel = wheelData.wheelAt("wheel");
        if (wheel.getType() != WheelType.W_1A1) {
            EdError.showError(mainFrame,
                              "The wheel must be of the type " +
                              WheelType.W_1A1 +
                              " for a spindown operation.");
            return false;
        }
        double zlen = wheelData.doubleAt("zlen");
        boolean flip = wheelData.boolAt("flip");
        boolean front = wheelData.boolAt("front");
        Dict wd = wheel.getSketchSpecs(); // from current wheel sketch
        Dict sd = sketch.getSpecs();
        double wheelWid = wd.doubleAt("l1");
        double wheelDia = wd.doubleAt("d1");
        double wheelR = wd.doubleAt("r1");
        double wheelZRef = front ? zlen : zlen + wheelWid;
        int nGrinds = nDias - 1;
        double blankDia = sd.doubleAt("d" + (nGrinds + 1));
        double stickOut = sd.doubleAt("l" + (nGrinds + 1));
        double totalStickout = stickOut +
            mainFrame.chuckPanel.getAxialLength() +
            mainFrame.colletPanel.getAxialLength();
        Grinder grinder = mainFrame.getGrinder();
        double zpx = grinder.getZeropointX(totalStickout);
        // 
        EMProgram prog = mainFrame.getProgram();
        prog.clear();
        prog.loadOpOrderDefaults();
        prog.loadProbeDefaults();
        // program parameters common to single and multiple page grind
        prog.put("zeropoint-x", zpx);
        prog.put("zeropoint-y", grinder.getZeropointY(blankDia));
        prog.put("zeropoint-z", grinder.getZeropointZ());
        prog.put("max-measure-len-x", zpx + .05);
        double prevGrindLength = 0;
        int page = 1;
        String valves = getValvesAsString();
        double zshift = getStepOver() / 100.0 * wheelWid;
        double yFinish = 0.0;
        if (doFinishPass())
            yFinish = getFinishPass();
        for (int i=1; i<=nGrinds; ++i) {
            double grindLen = sd.doubleAt("l" + i);
            double grindDia = sd.doubleAt("d" + i) + yFinish * 2;
            if (wheelR > grindLen) {
                EdError.showError(mainFrame,
                                  "The wheel corner radius (" + wheelR +
                                  ") must be less than the grind length (" +
                                  (grindLen - prevGrindLength) + ").");
                return false;
            }
            double stepLen = grindLen - prevGrindLength;
            List<Double> zs = findZRoughPasses(stepLen, wheelWid, wheelR,
                                               zshift, prevGrindLength);
            double grindDepth = (blankDia - grindDia) / 2.0;
            /*
              Find the number of y depth passes. The initial user-defined Y
              Infeed is first tried. If the resulting if/r is > the Y Infeed,
              the number of Y passes is incremented until the if/r is <= the Y
              Infeed. This will ensure the requested Y Infeed is not exceeded.
             */
            int nYPasses = (int)(grindDepth / getYInfeed());
            double ifr = grindDepth / nYPasses;
            while (ifr > getYInfeed())
                ifr = grindDepth / ++nYPasses;
            // the current plunge pass per diameter
            int diaZPass = 1;
            for (double z : zs) {
                prog.loadPageDefaults(page);
                prog.put("grind-name-" + page, "Diameter " +
                         i + "-" + diaZPass++);
                prog.put("spindle-rpm-" + page, getWheelRPM());
                if (!valves.isEmpty())
                    prog.put("coolant-valves-" + page,
                             Integer.parseInt(valves));
                prog.put("cyl-grind-rpm-" + page, 0);
                prog.put("plunge-feed-" + page, getYFeedrate());
                prog.put("rough-feed-in-" + page, getAFeedrate());
                prog.put("plg-y-" + page, ifr + RAPID_Y);
                prog.put("rough-passes-" + page, nYPasses);
                prog.put("ir-y-" + page, ifr);
                // wheel center-line inline with blank centerline
                prog.put("0pt-x-" + page, grinder.getS1CenterlineX());
                // wheel bottom tangent to the blank top
                prog.put("0pt-y-" + page,
                         grinder.getS1CenterlineY(wheelDia / 2 +
                                                  blankDia / 2));
                // wheel front inline with blank end
                prog.put("0pt-z-" + page,
                         grinder.getS1RefToAFaceZ(zlen +
                                                  totalStickout -
                                                  z));
                prog.put("0pt-c-" + page, 90.0);
                prog.put("gl1-a-" + page, 370.0);
                prog.put("lo-y-" + page, -(ifr + RAPID_Y));
                // ----------------------------------------------------------
                // Finish Pass
                if (doFinishPass()) {
                    prog.put("finish-passes-" + page, 1);
                    prog.put("if-y-" + page, getFinishPass());
                    if (grinder.hasFinishFeedrate())
                        prog.put("finish-feed-in-" + page, getAFeedrate() * 2);
                }
                try {
                    page = nextPage(page);
                }
                catch (Exception ignore) {
                    return false;
                }
            }
            prevGrindLength = grindLen;
        }
        return true;
    }
    private int nextPage(int page) {
        if (page == MAX_GRIND_PAGES)
            EdError.showError(mainFrame, "Maximum " + MAX_GRIND_PAGES +
                              " grind pages exceeded");
        return page + 1;
    }
    /**
       Find the Z coordinates of each Z plunge point.
       
       <p>
       The coords will be ordered from the face of the blank to the shoulder,
       referencing the front of the wheel.
       </p>
       
       @param gl grind length from the end of the blank
       @param ww wheel width
       @param wr wheel corner radius
       @param offset start of grind diameter in z from the end of the blank
       @return a list of z coordinates
    */
    private List<Double> findZRoughPasses(double gl, double ww, double wr,
                                          double zshift, double offset) {
        List<Double> list = new ArrayList<Double>();
        if (ww - wr > gl) {
            list.add(gl + offset);       // single z plunge
            return list;
        }
        double wfw = ww - wr * 2; // wheel flat width
        double stepOver = getStepOver() / 100.0;
        if (wfw < zshift) {
            EdError.showError(mainFrame,
                              "The wheel corner radius is too large for" +
                              " a step-over of " + getStepOver() +
                              "%. The grind will leave scallops.");
            return null;
        }
        double z = gl - ww + offset;
        list.add(z + ww);
        while (z > 0) {
            z -= zshift;
            list.add(z + ww);
        }
        Collections.reverse(list);
        return list;
    }
}
