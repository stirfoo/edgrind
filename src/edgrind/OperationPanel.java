package edgrind;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
// import javax.swing.JComboBox;
// import javax.swing.DefaultComboBoxModel;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class OperationPanel extends JPanel {
    private JLabel[] namelbls = new JLabel[15];
    private MainFrame parent;
    public OperationPanel(MainFrame parent) {
        this.parent = parent;
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(3, 3, 3, 3));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel);
        panel.setLayout(new GridLayout(0, 4, 3, 0));

        /*
          Add read-only grind name labels, and order textfields.

          Widgets are added left-to-right top-to-bottom, array indexes are:

          .----------------.
          | 0 | 0 |  8 |  8 |
          | 1 | 1 |  9 |  9 |
          |       ...       |
          | 6 | 6 | 14 | 14 |
          | 7 | 7 |         |
          '-----------------'
        */
        for (int i=0, j=8; i<7; ++i, ++j) {
            addGrindNameOrderWidgets(panel, i);
            addGrindNameOrderWidgets(panel, j);
        }
        // now add the last label/textfield pair:  | 7 | 7 |
        addGrindNameOrderWidgets(panel, 7);
		
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(0, 3, 3, 3));
        panel_1.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_1);
        panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
        JPanel panel_7 = new JPanel();
        panel_7.setBorder(new EmptyBorder(0, 0, 3, 0));
        panel_1.add(panel_7);
        panel_7.setLayout(new GridLayout(0, 2, 3, 3));
        // 
        JLabel lblPropeLength = new JLabel("Probe Length");
        panel_7.add(lblPropeLength);
        lblPropeLength.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_7.add(parent.prog.get("probe-len").getWidget());
        // 
        JLabel lblProbeRadialPositioning
            = new JLabel("Probe Radial Positioning");
        panel_7.add(lblProbeRadialPositioning);
        lblProbeRadialPositioning
            .setHorizontalAlignment(SwingConstants.RIGHT);
        panel_7.add(parent.prog.get("probe-radial-pos").getWidget());
	// 
        JLabel lblFileNameFor = new JLabel("File Name For Probe Data");
        panel_7.add(lblFileNameFor);
        lblFileNameFor.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_7.add(parent.prog.get("probe-data-file-name").getWidget());
        // 
        JPanel panel_5 = new JPanel();
        panel_5.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                                                                    229)),
                                           "Stock Removal", TitledBorder.LEFT,
                                           TitledBorder.TOP, null,
                                           new Color(51, 51, 51)));
        panel_1.add(panel_5);
        panel_5.setLayout(new GridLayout(0, 6, 0, 0));
        // 
        JLabel lblA = new JLabel("A ");
        lblA.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblA);
        panel_5.add(parent.prog.get("stock-removal-a").getWidget());
        // 
        JLabel lblX = new JLabel("X1 ");
        lblX.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblX);
        panel_5.add(parent.prog.get("stock-removal-x1").getWidget());
        // 
        JLabel lblZ = new JLabel("Z1 ");
        lblZ.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_5.add(lblZ);
        panel_5.add(parent.prog.get("stock-removal-z1").getWidget());
	// 
        JPanel panel_6 = new JPanel();
        panel_6.setBorder(new EmptyBorder(3, 0, 3, 0));
        panel_1.add(panel_6);
        // 
        JLabel lblControl = new JLabel("Control");
        panel_6.add(lblControl);
        panel_6.add(parent.prog.get("control").getWidget());
        //
        JCheckBox cb
            = (JCheckBox)parent.prog.get("hmc400-var-speed").getWidget();
        cb.setText("If HMC400 Control, Use Variable-Speed Spindle");
        cb.setAlignmentX(Component.CENTER_ALIGNMENT);
        cb.setAlignmentY(Component.TOP_ALIGNMENT);
        cb.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(cb);
        // 
        Component verticalGlue = Box.createVerticalGlue();
        verticalGlue.setPreferredSize(new Dimension(0, 32767));
        add(verticalGlue);

    }
    void addGrindNameOrderWidgets(JPanel panel, int idx) {
        JLabel lbl = namelbls[idx] = new JLabel("");
        lbl.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lbl);
        panel.add(parent.prog.get("op-order-" + (idx + 1)).getWidget());
    }
    // Called when the "Grind #N" field on a GrindPanel changes.
    void updateGrindName(String name, int page) {
        namelbls[page-1].setText(name);
    }
}
