/*
  WeldonFlatPanel.java
  S. Edward Dolan
  Wednesday, November  8 2023
*/

package edgrind;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
// 
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
// 
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
// 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
//
import edgrind.sketch.Sketch;
import edgrind.sketch.SketchScene;
import edgrind.sketch.WeldonFlatSketch;
//
import edgrind.error.EdError;
//
import edgrind.expert.EMProgram;
//
import edgrind.geom.Algo;

/*
  .-----------------------------.-----------------------------------.
  | Standards: |0.5000 (1/2)|v| |                                   |
  | .- Grind Parameters ------. |                                   |
  | |     Spindle: |1     |v| | |                                   |
  | |       Wheel: |1     |v| | |                                   |
  | |   Wheel RPM: __________ | |                                   |
  | |    Feedrate: __________ | |                                   |
  | | Step-Over %: __________ | |                                   |
  | | .- Coolant Valves ----. | |           Sketch Scene            |
  | | | [ ]  3 [ ]  2 [ ] 1 | | |                                   |
  | | '---------------------' | |                                   |
  | '-------------------------' |                                   |
  |      |Write Program|        |                                   |
  |                             |                                   |
  |                             |                                   |
  '-----------------------------'-----------------------------------'
  
 */
@SuppressWarnings("serial")
public class WeldonFlatPanel extends JPanel implements ActionListener,
                                                       ItemListener {
    MainFrame mainFrame;
    ParamsPanel paramsPanel;
    SketchScene sketchScene;
    JComboBox<String> cboStandards;
    JButton butWriteProgram;
    WeldonFlatPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        int nextGridY = 0;
        setLayout(gb);
        gc.insets = new Insets(3, 3, 3, 3);
        // --------------------------------------------------
        // Standards: |0.1250 (1/8)|v|
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        JLabel lbl = new JLabel("Standards:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        cboStandards = new JComboBox<String>();
        for (String name : stds.keySet())
            cboStandards.addItem(name);
        cboStandards.addItemListener(this);
        gb.setConstraints(cboStandards, gc);
        add(cboStandards);
        // -----------------------------------------------------------
        // Params Panel
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.weighty = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.fill = GridBagConstraints.NONE;
        paramsPanel = new ParamsPanel(mainFrame);
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
        sketchScene.addSketch(new WeldonFlatSketch(sketchScene));
        sketchScene.getSketch(0).setFillShape(true);
        gb.setConstraints(sketchScene, gc);
        add(sketchScene);
        //
        cboStandards.setSelectedItem("0.5000 (1/2)");
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butWriteProgram)
            paramsPanel.writeProgram(sketchScene.getSketch(0));
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboStandards) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                sketchScene.getSketch(0)
                    .config(stds.dictAt((String)e.getItem()));
                sketchScene.fitAll();
            }
        }
    }
    /**
       Standard weldon flat dimensions by shank diameter.
    */
    static final Dict stds
        = new Dict("0.1250 (1/8)", new Dict("d1", .125, "d2", .105, 
                                            "l1", .5, "l2", .156,
                                            "l3", .754),
                   "0.1875 (3/16)", new Dict("d1", .1875, "d2", .15, 
                                             "l1", .6875, "l2", .156,
                                             "l3", .959),
                   "0.2500 (1/4)", new Dict("d1", .25, "d2", .215, 
                                            "l1", .7812, "l2", .187,
                                            "l3", 1.0967),
                   "0.3125 (5/16)", new Dict("d1", .3125, "d2", .275, 
                                             "l1", .7812, "l2", .25,
                                             "l3", 1.193),
                   "0.3750 (3/8)", new Dict("d1", .375, "d2", .32, 
                                            "l1", .7812, "l2", .281,
                                            "l3", 1.257),
                   "0.4375 (7/16)", new Dict("d1", .4375, "d2", .378, 
                                             "l1", .7812, "l2", .331,
                                             "l3", 1.3372),
                   "0.5000 (1/2)", new Dict("d1", .5, "d2", .435, 
                                            "l1", .8906, "l2", .331,
                                            "l3", 1.452),
                   "0.5625 (9/16)", new Dict("d1", .5625, "d2", .5, 
                                             "l1", .92, "l2", .331,
                                             "l3", 1.479),
                   "0.6250 (5/8)", new Dict("d1", .625, "d2", .555, 
                                            "l1", .9531, "l2", .401,
                                            "l3", 1.624),
                   "0.7500 (3/4)", new Dict("d1", .75, "d2", .67, 
                                            "l1", 1.0156, "l2", .456,
                                            "l3", 1.779),
                   "0.8750 (7/8)", new Dict("d1", .875, "d2", .805, 
                                            "l1", 1.0156, "l2", .456,
                                            "l3", 1.856),
                   "1.0000 (1)", new Dict("d1", 1.0, "d2", .92, 
                                          "l1", 1.1406, "l2", .516,
                                          "l3", 1.994),
                   "1.2500 (1-1/4)", new Dict("d1", 1.25, "d2", 1.151, 
                                              "l1", 1.1406, "l2", .516,
                                              "l3", 2.013));
}

@SuppressWarnings("serial")
class ParamsPanel extends JPanel implements ItemListener {
    /** Feedrate from from clear Y position to the start of the grind line. */
    protected static final double PLUNGE_FEED = 20.;
    /**
       Wheel distance above blank on initial rapid move to the blank. This
       will also be the clearance above the blank for retract on multiple
       passes.
    */
    protected static final double RAPID_Y = .03;
    /**
       The gap between the wheel and the blank, at the start of the grind
       line.
    */
    protected static final double WHEEL_GAP = .015;
    /** Distance to grind past the flat so the wheel clears the flat. */
    protected static final double LEAD_OUT = .015;
    /** Max distance the X axis may travel past the X prob point. */
    protected static final double PROBE_OVERSHOOT_X = .05;
    // 
    protected MainFrame mainFrame;
    protected JComboBox<String> cboSpindle;
    protected JComboBox<String> cboWheel;
    protected IntEdit txtWheelRPM;
    protected FloatEdit txtFeedrate;
    protected IntEdit txtStepOver;
    protected FloatEdit txtWheelGap;
    protected FloatEdit txtLeadOut;
    protected CoolantValvesPanel valvesPanel;
    ParamsPanel(MainFrame mainFame) {
        this.mainFrame = mainFame;
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
        cboSpindle = new JComboBox<String>();
        cboSpindle.addItem("1");
        cboSpindle.addItem("2");
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
        cboWheel = new JComboBox<String>();
        cboWheel.addItem("1");
        cboWheel.addItem("2");
        cboWheel.addItem("3");
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
        // Feedrate: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Feedrate:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtFeedrate = new FloatEdit(0.35, 0, 20);
        txtFeedrate.setMinimumSize(txtFeedrate.getPreferredSize());
        txtFeedrate.setToolTipText("flat grind feedrate");
        lbl.setLabelFor(txtFeedrate);
        gb.setConstraints(txtFeedrate, gc);
        add(txtFeedrate);
        // --------------------------------------------------
        // Step-Over %: ____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
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
        txtStepOver.setToolTipText("max wheel flat step-over percentage");
        txtStepOver.setMinimumSize(txtStepOver.getPreferredSize());
        lbl.setLabelFor(txtStepOver);
        gb.setConstraints(txtStepOver, gc);
        add(txtStepOver);
        // --------------------------------------------------
        // Wheel Gap: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Wheel Gap:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtWheelGap = new FloatEdit(WHEEL_GAP, .005, .1);
        txtWheelGap.setToolTipText("wheel to blank distance prior" +
                                   " to grind (GL1)");
        txtWheelGap.setMinimumSize(txtWheelGap.getPreferredSize());
        lbl.setLabelFor(txtWheelGap);
        gb.setConstraints(txtWheelGap, gc);
        add(txtWheelGap);
        // --------------------------------------------------
        // Lead Out: _____________
        gc.gridx = 0;
        gc.gridy = nextGridY++;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        lbl = new JLabel("Lead Out:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        txtLeadOut = new FloatEdit(LEAD_OUT, .001, .1);
        txtLeadOut.setMinimumSize(txtLeadOut.getPreferredSize());
        txtLeadOut.setToolTipText("distance to grind beyond flat");
        lbl.setLabelFor(txtLeadOut);
        gb.setConstraints(txtLeadOut, gc);
        add(txtLeadOut);
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
        return Integer.parseInt((String)cboSpindle.getSelectedItem());
    }
    public int getWheelNum() {
        return Integer.parseInt((String)cboWheel.getSelectedItem());
    }
    public int getWheelRPM() {
        return txtWheelRPM.getValue();
    }
    public double getFeedrate() {
        return txtFeedrate.getValue();
    }
    public int getStepOver() {
        return txtStepOver.getValue();
    }
    public double getWheelGap() {
        return txtWheelGap.getValue();
    }
    public double getLeadOut() {
        return txtLeadOut.getValue();
    }
    public String getValvesAsString() {
        return valvesPanel.getOpenValves();
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboSpindle) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                valvesPanel
                    .setSpindle(Integer.parseInt((String)e.getItem()));
            }
        }
    }
    protected void writeProgram(Sketch sketch) {
        Dict wheelData = mainFrame.wheelsPanel.getWheelData(getSpindleNum(),
                                                            getWheelNum());
        if (wheelData == null)
            return;             // no wheel loaded, or not saved
        Wheel wheel = wheelData.wheelAt("wheel");
        if (wheel.getType() != WheelType.W_1A1_CMF) {
            EdError.showError(mainFrame,
                              "Currently, the wheel must be of the type " +
                              WheelType.W_1A1_CMF +
                              " for a weldon flat.");
            return;
        }
        // got a wheel, lets grind!
        Dict wd = wheel.getSketchSpecs(); // from current wheel sketch
        double wheelFlatWid = wd.doubleAt("l3");
        Dict sd = sketch.getSpecs();
        double flatWid = sd.doubleAt("l2");
        if (wheelFlatWid > flatWid) {
            EdError.showError(mainFrame,
                              "Wheel flat width (" + wheelFlatWid + ") too" +
                              " wide for current weldon flat width (" +
                              flatWid + ").");
            return;
        }
        double zlen = wheelData.doubleAt("zlen");
        boolean front = wheelData.boolAt("front");
        double wheelDia = wd.doubleAt("d1");
        double wheelWid = wd.doubleAt("l1");
        double wheelR = wheelDia / 2;
        double blankDia = sd.doubleAt("d1");
        double blankR = blankDia / 2;
        double stickOut = sd.doubleAt("l3");
        double grindDepth = blankDia - sd.doubleAt("d2");
        double flatCenterX = sd.doubleAt("l1");
        // make the front of the wheel the ref point
        double wheelZRef = front ? zlen : zlen + wheelWid;
        // --------------------------------------------------
        Grinder grinder = mainFrame.getGrinder();
        EMProgram prog = mainFrame.getProgram();
        prog.clear();
        prog.loadOpOrderDefaults();
        prog.loadProbeDefaults();
        prog.loadPageDefaults(1);
        /*
          --------------------------------------------------
          probe zero points
          --------------------------------------------------
        */
        double totalStickout = stickOut +
            mainFrame.chuckPanel.getAxialLength() +
            mainFrame.colletPanel.getAxialLength();
        double zpx = grinder.getZeropointX(totalStickout);
        prog.put("zeropoint-x", zpx);
        prog.put("zeropoint-y", grinder.getZeropointY(blankDia));
        prog.put("zeropoint-z", grinder.getZeropointZ());
        prog.put("max-measure-len-x", zpx + PROBE_OVERSHOOT_X);
        /*
          --------------------------------------------------
          common parameters
          --------------------------------------------------
        */
        prog.put("grind-name-1", "Grind Flat");
        prog.put("spindle-rpm-1", getWheelRPM());
        String valves = getValvesAsString();
        if (!valves.isEmpty())
            prog.put("coolant-valves-1", Integer.parseInt(valves));
        prog.put("plunge-feed-1", PLUNGE_FEED);
        prog.put("rough-feed-in-1", getFeedrate());
        prog.put("plg-y-1", grindDepth + RAPID_Y);
        prog.put("lo-y-1", -(grindDepth + RAPID_Y));
        /*
          --------------------------------------------------
          X, Y, C, start positions and X grind line
          --------------------------------------------------
        */
        // Y - place the bottom of the wheel on the flat's surface planea
        double wcl2bcl = wheelR + blankR - grindDepth;
        prog.put("0pt-y-1", grinder.getS1CenterlineY(wcl2bcl));
        // X - place the wheel behind the blank, at the requested gap dist
        double hyp = wheelR + getWheelGap() + blankR;
        double sideA = wcl2bcl;
        double sideB = Math.sqrt(hyp * hyp - sideA * sideA);
        prog.put("0pt-x-1", grinder.getS1CenterlineX() - sideB);
        // C - angle for spindle 1 wheel
        prog.put("0pt-c-1", 90.0);
        // flat width material left after 1st pass
        double chordLen = Algo.chordLength(blankR, blankR - grindDepth);
        prog.put("gl1-x-1", sideB + chordLen + getLeadOut());
        /*
          --------------------------------------------------
          Z start position and step over positions
          --------------------------------------------------
        */
        // distance from s1 wheel front to blank end along the Z axis @ 90 deg
        double wheel2BlankZ = grinder.getS1RefToAFaceZ(totalStickout +
                                                       wheelZRef);
        // Z dist from blank end to right corner of flat
        double flatRCornerZ = flatCenterX + flatWid / 2;
        // wheel chamfer size
        double wheelCmf = wheelWid - wheelFlatWid / 2;
        // wheel Z shift given step over %
        double maxZShift = getStepOver() / 100.0 * wheelFlatWid;
        // flat left over after first grind
        double restZ = flatWid - wheelFlatWid;
        if (restZ == 0) {
            // one pass, wheel flat width == weldon flat width
            prog.put("rough-passes-1", 1);
            prog.put("0pt-z-1", wheel2BlankZ + flatRCornerZ + wheelCmf);
        }
        else if (restZ <= maxZShift) {
            // 2 passes using restZ
            prog.put("rough-passes-1", 2);
            prog.put("ir-z-1", restZ);
            prog.put("0pt-z-1", wheel2BlankZ + flatRCornerZ + wheelCmf -
                     restZ);
        }
        else {
            /*
              Multiple passes using max step-over % because the remaining
              grind is wider than the maxZShift.
            */
            int nZPasses = 1;
            double zShift = 0;
            do
                zShift = restZ / nZPasses++;
            while (zShift > maxZShift);
            prog.put("rough-passes-1", nZPasses);
            prog.put("ir-z-1", zShift);
            // grind left to right (for spindle 1 wheel)
            prog.put("0pt-z-1", wheel2BlankZ + flatRCornerZ + wheelCmf -
                     zShift * (nZPasses - 1));
        }
    }
}
