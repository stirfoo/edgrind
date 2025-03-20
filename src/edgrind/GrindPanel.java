package edgrind;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class GrindPanel extends JPanel {
    private int page;
    private MainFrame parent;
    class GrindNameListener implements DocumentListener {
        JTextField tf;
        int page;
        GrindNameListener(JTextField tf, int page) {
            this.tf = tf;
            this.page = page;
        }
        public void changedUpdate(DocumentEvent e) {onChange();}
        public void removeUpdate(DocumentEvent e) {onChange();}
        public void insertUpdate(DocumentEvent e) {onChange();}
        public void onChange() {
            parent.updateGrindName(tf.getText(), page);
        }
    }
    public GrindPanel(int page, MainFrame parent) {
        this.page = page;
        this.parent = parent;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(3, 3, 3, 3));
        panel_1.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_1);
        // 
        JLabel lblgrind = new JLabel("Grind " + page);
        lblgrind.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_1.add(lblgrind);
        JTextField grindName
            = (JTextField)parent.prog.get("grind-name-" + page).getWidget();
        grindName.setColumns(12);
        panel_1.add(grindName);
        grindName.getDocument()
            .addDocumentListener(new GrindNameListener(grindName, page));
        // 
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(0, 3, 0, 3));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel);
        panel.setLayout(new GridLayout(10, 4, 3, 0));
        // 
        JLabel lblSpindleSpeedrpm = new JLabel("Spindle RPM");
        lblSpindleSpeedrpm.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblSpindleSpeedrpm);
	panel.add(parent.prog.get("spindle-rpm-" + page).getWidget());
	// 
        JLabel lblCoolantValves = new JLabel("Coolant Valves");
        lblCoolantValves.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblCoolantValves);
        panel.add(parent.prog.get("coolant-valves-" + page).getWidget());
        // 
        JLabel lblCylindricalGrindingno = new JLabel("Cyl Grind RPM (0=no)");
        lblCylindricalGrindingno.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblCylindricalGrindingno);
	panel.add(parent.prog.get("cyl-grind-rpm-" + page).getWidget());
        // 
        JLabel label_2 = new JLabel("");
        panel.add(label_2);
        JCheckBox cb
            = (JCheckBox)parent.prog.get("steadyrest-" + page).getWidget();
        cb.setText("Steadyrest");
        panel.add(cb);
        // 
        JLabel lblTouchProbeOperation = new JLabel("Touch Probe Op");
        lblTouchProbeOperation.setHorizontalAlignment(SwingConstants
                                                      .RIGHT);
        panel.add(lblTouchProbeOperation);
        panel.add(parent.prog.get("touch-probe-op-" + page).getWidget());
        // 
        JLabel label_3 = new JLabel("");
        panel.add(label_3);
        cb = (JCheckBox)parent.prog.get("high-pressure-" + page).getWidget();
        cb.setText("High Pressure");
        panel.add(cb);
        // 
        JLabel lblNumerOfFlutes = new JLabel("Number of Flutes");
        lblNumerOfFlutes.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblNumerOfFlutes);
        panel.add(parent.prog.get("num-flutes-" + page).getWidget());
        // 
        JLabel label_4 = new JLabel("");
        panel.add(label_4);
        cb = (JCheckBox)parent.prog.get("quick-a-ret-" + page).getWidget();
        cb.setText("Quick A Return");
        panel.add(cb);
        // 
        JLabel lblRougnPasses = new JLabel("Rough Passes");
        lblRougnPasses.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblRougnPasses);
        panel.add(parent.prog.get("rough-passes-" + page).getWidget());
        // 
        JLabel lblFinishPasses = new JLabel("Finish Passes");
        lblFinishPasses.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblFinishPasses);
        panel.add(parent.prog.get("finish-passes-" + page).getWidget());
        // 
        JLabel lblPlungeFeed = new JLabel("Plunge Feed");
        lblPlungeFeed.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblPlungeFeed);
        panel.add(parent.prog.get("plunge-feed-" + page).getWidget());
        // 
        JLabel label_5 = new JLabel("");
        panel.add(label_5);
        JLabel label_6 = new JLabel("");
        panel.add(label_6);
        // 
        JLabel lblRoughFeedIn = new JLabel("Rough Feed In");
        lblRoughFeedIn.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblRoughFeedIn);
        panel.add(parent.prog.get("rough-feed-in-" + page).getWidget());
        // 
        JLabel lblOut = new JLabel("Out");
        lblOut.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblOut);
        panel.add(parent.prog.get("rough-feed-out-" + page).getWidget());
        // 
        JLabel lblFinishFeedIn = new JLabel("Finish Feed In");
        lblFinishFeedIn.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblFinishFeedIn);
        panel.add(parent.prog.get("finish-feed-in-" + page).getWidget());
        // 
        JLabel lblOut_1 = new JLabel("Out");
        lblOut_1.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblOut_1);
        panel.add(parent.prog.get("finish-feed-out-" + page).getWidget());
        // 
        JLabel lblLiftoffFeedrapid = new JLabel("L/O Feed (0=rapid)");
        lblLiftoffFeedrapid.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblLiftoffFeedrapid);
        panel.add(parent.prog.get("lift-off-feed-" + page).getWidget());
        // 
        JLabel label_7 = new JLabel("");
        panel.add(label_7);
        JLabel label_8 = new JLabel("");
        panel.add(label_8);
        // 
        JLabel lblSpiralLead = new JLabel("Spiral Lead*");
        lblSpiralLead.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblSpiralLead);
        panel.add(parent.prog.get("spiral-lead-" + page).getWidget());
        // 
        JLabel lblDwell = new JLabel("Dwell");
        lblDwell.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblDwell);
        panel.add(parent.prog.get("dwell-" + page).getWidget());
        // 
        JPanel panel_2 = new JPanel();
        panel_2.setBorder(new EmptyBorder(0, 0, 3, 3));
        panel_2.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_2.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_2);
        panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
        // 
        JPanel panel_3 = new JPanel();
        panel_3.setBorder(new EmptyBorder(0, 3, 0, 3));
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_2.add(panel_3);
        panel_3.setLayout(new GridLayout(8, 0, 3, 0));
        // 
        JLabel lblAsdf = new JLabel("");
        panel_3.add(lblAsdf);
        JLabel lblIr = new JLabel("I/r");
        lblIr.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblIr);
        JLabel lblNewLabel = new JLabel("I/f");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel);
        JLabel lblNewLabel_1 = new JLabel("Plg");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel_1);
        JLabel lblNewLabel_2 = new JLabel("0pt");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel_2);
        JLabel lblNewLabel_3 = new JLabel("GL1");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel_3);
        JLabel lblNewLabel_4 = new JLabel("L/O");
        lblNewLabel_4.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel_4);
        JLabel lblNewLabel_5 = new JLabel("Ret");
        lblNewLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_3.add(lblNewLabel_5);
        // 
        JPanel panel_4 = new JPanel();
        panel_4.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_4.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_2.add(panel_4);
        panel_4.setLayout(new GridLayout(8, 5, 3, 0));
        // 
        JLabel lblNewLabel_6 = new JLabel("A (Dg)*");
        lblNewLabel_6.setHorizontalAlignment(SwingConstants.CENTER);
        panel_4.add(lblNewLabel_6);
        JLabel lblX = new JLabel("X (in)");
        lblX.setHorizontalAlignment(SwingConstants.CENTER);
        panel_4.add(lblX);
        JLabel lblY = new JLabel("Y (in)");
        lblY.setHorizontalAlignment(SwingConstants.CENTER);
        panel_4.add(lblY);
        JLabel lblZ = new JLabel("Z (in)");
        lblZ.setHorizontalAlignment(SwingConstants.CENTER);
        panel_4.add(lblZ);
        JLabel lblC = new JLabel("C (Dg)");
        lblC.setHorizontalAlignment(SwingConstants.CENTER);
        panel_4.add(lblC);
        //
        panel_4.add(parent.prog.get("ir-a-" + page).getWidget());
        panel_4.add(parent.prog.get("ir-x-" + page).getWidget());
        panel_4.add(parent.prog.get("ir-y-" + page).getWidget());
        panel_4.add(parent.prog.get("ir-z-" + page).getWidget());
        panel_4.add(new JLabel(""));
        // 
        panel_4.add(parent.prog.get("if-a-" + page).getWidget());
        panel_4.add(parent.prog.get("if-x-" + page).getWidget());
        panel_4.add(parent.prog.get("if-y-" + page).getWidget());
        panel_4.add(parent.prog.get("if-z-" + page).getWidget());
        panel_4.add(new JLabel(""));
        // 
        panel_4.add(new JLabel(""));
        panel_4.add(parent.prog.get("plg-x-" + page).getWidget());
        panel_4.add(parent.prog.get("plg-y-" + page).getWidget());
        panel_4.add(parent.prog.get("plg-z-" + page).getWidget());
        panel_4.add(new JLabel(""));
        // 
        panel_4.add(parent.prog.get("0pt-a-" + page).getWidget());
        panel_4.add(parent.prog.get("0pt-x-" + page).getWidget());
        panel_4.add(parent.prog.get("0pt-y-" + page).getWidget());
        panel_4.add(parent.prog.get("0pt-z-" + page).getWidget());
        panel_4.add(parent.prog.get("0pt-c-" + page).getWidget());
        // 
        panel_4.add(parent.prog.get("gl1-a-" + page).getWidget());
        panel_4.add(parent.prog.get("gl1-x-" + page).getWidget());
        panel_4.add(parent.prog.get("gl1-y-" + page).getWidget());
        panel_4.add(parent.prog.get("gl1-z-" + page).getWidget());
        panel_4.add(parent.prog.get("gl1-c-" + page).getWidget());
        // 
        panel_4.add(parent.prog.get("lo-a-" + page).getWidget());
        panel_4.add(parent.prog.get("lo-x-" + page).getWidget());
        panel_4.add(parent.prog.get("lo-y-" + page).getWidget());
        panel_4.add(parent.prog.get("lo-z-" + page).getWidget());
        panel_4.add(new JLabel(""));
        // 
        panel_4.add(new JLabel(""));
        panel_4.add(parent.prog.get("ret-x-" + page).getWidget());
        panel_4.add(parent.prog.get("ret-y-" + page).getWidget());
        panel_4.add(parent.prog.get("ret-z-" + page).getWidget());
        panel_4.add(new JLabel(""));
        // 
        JPanel panel_5 = new JPanel();
        panel_5.setBorder(new EmptyBorder(0, 3, 3, 3));
        panel_5.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_5);
        panel_5.setLayout(new GridLayout(1, 6, 3, 0));
        // 
        JLabel lblContours = new JLabel("Contours");
        lblContours.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblContours);
        panel_5.add(parent.prog.get("contours-" + page).getWidget());
	// 
        JLabel lblRotationA = new JLabel("Rotation A");
        lblRotationA.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblRotationA);
        panel_5.add(parent.prog.get("rotation-a-" + page).getWidget());
        // 
        JLabel lblC_1 = new JLabel("C");
        lblC_1.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblC_1);
        panel_5.add(parent.prog.get("rotation-c-" + page).getWidget());
        // 
        Component verticalGlue = Box.createVerticalGlue();
        verticalGlue.setPreferredSize(new Dimension(0, 32767));
        add(verticalGlue);
    }
}
