package edgrind;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.SpringLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.border.MatteBorder;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ProbePanel extends JPanel {
    private MainFrame parent;
    public ProbePanel(MainFrame parent) {
        this.parent = parent;
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setAlignmentY(Component.TOP_ALIGNMENT);
        // setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)),
        //                            "Probing", TitledBorder.CENTER,
        //                            TitledBorder.TOP, null,
        //                            new Color(51, 51, 51)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
        JPanel panel_6 = new JPanel();
        panel_6.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_6.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_6);

        JCheckBox cb
            = (JCheckBox)parent.prog.get("interrupt-after-probing").getWidget();
        cb.setText("Interrupt After Probing");
        panel_6.add(cb);

        // -------------------------------------------------- Length Probing
		
        JPanel panel_2 = new JPanel();
        panel_2.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_2.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_2.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                                                                    229)),
                                           "Length", TitledBorder.CENTER,
                                           TitledBorder.TOP, null,
                                           new Color(51, 51, 51)));
        add(panel_2);
        panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
        Component horizontalGlue = Box.createHorizontalGlue();
        panel_2.add(horizontalGlue);
        horizontalGlue.setPreferredSize(new Dimension(32767, 0));
		
        JPanel panel = new JPanel();
        panel_2.add(panel);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setLayout(new GridLayout(4, 2, 3, 0));
	// 
        JLabel lblZeropointX = new JLabel("Zeropoint X");
        lblZeropointX.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblZeropointX);
        panel.add(parent.prog.get("zeropoint-x").getWidget());
        // 
        JLabel lblZeropointY = new JLabel("Zeropoint Y");
        lblZeropointY.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblZeropointY);
        panel.add(parent.prog.get("zeropoint-y").getWidget());
        // 
        JLabel lblNewLabel = new JLabel("Zeropoint Z");
        lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblNewLabel);
        panel.add(parent.prog.get("zeropoint-z").getWidget());
        // 
        JLabel lblMaxMeasuringLength = new JLabel("Max Measuring Length X");
        lblMaxMeasuringLength.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(lblMaxMeasuringLength);
        panel.add(parent.prog.get("max-measure-len-x").getWidget());

        // -------------------------------------------------- Radial Probing
		
        JPanel panel_3 = new JPanel();
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                                                                    229)),
                                           "Radial", TitledBorder.CENTER,
                                           TitledBorder.TOP, null,
                                           new Color(51, 51, 51)));
        add(panel_3);
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
		
        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new EmptyBorder(0, 0, 0, 3));
        panel_4.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_4.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.add(panel_4);
        panel_4.setLayout(new GridLayout(7, 1, 0, 0));
		
        JLabel lblX = new JLabel("");
        panel_4.add(lblX);
		
        panel_4.add(new JLabel("#1"));
        panel_4.add(new JLabel("#2"));
        panel_4.add(new JLabel("#3"));
        panel_4.add(new JLabel("#4"));
        panel_4.add(new JLabel("#5"));
        panel_4.add(new JLabel("#6"));

        // -------------------------------------------------- N Teeth Column
		
        JPanel panel_5 = new JPanel();
        panel_5.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_5.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.add(panel_5);
        panel_5.setLayout(new GridLayout(7, 1, 0, 0));

        JLabel lblNTeeth = new JLabel("N Teeth");
        lblNTeeth.setHorizontalTextPosition(SwingConstants.CENTER);
        panel_5.add(lblNTeeth);

        for (int i=0; i<6; ++i)
            panel_5.add(parent.prog.get("rad-probe-num-teeth-" +
                                        (i + 1)).getWidget());
        // -------------------------------------------------- Radius Values
        
        JPanel panel_1 = new JPanel();
        panel_3.add(panel_1);
        panel_1.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_1.setLayout(new GridLayout(7, 6, 0, 0));
		
        JLabel lblNewLabel_2 = new JLabel("A");
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblNewLabel_2);
		
        JLabel lblNewLabel_3 = new JLabel("X");
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblNewLabel_3);
		
        JLabel lblY = new JLabel("Y");
        lblY.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblY);
		
        JLabel lblZ = new JLabel("Z");
        lblZ.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblZ);
		
        JLabel lblRapidIndex = new JLabel("Rapid Idx");
        lblRapidIndex.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblRapidIndex);
		
        JLabel lblCorrLevelA = new JLabel("Corr. Lvl A");
        lblCorrLevelA.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(lblCorrLevelA);

        for (int i=1; i<7; ++i) {
            panel_1.add(parent.prog.get("rad-probe-a-" + i).getWidget());
            panel_1.add(parent.prog.get("rad-probe-x-" + i).getWidget());
            panel_1.add(parent.prog.get("rad-probe-y-" + i).getWidget());
            panel_1.add(parent.prog.get("rad-probe-z-" + i).getWidget());
            panel_1.add(parent.prog.get("rad-probe-rapid-index-" + i)
                        .getWidget());
            panel_1.add(parent.prog.get("rad-probe-corr-level-a-" + i)
                        .getWidget());
        }

        // -------------------------------------------------- Glue
		
        Component horizontalGlue_1 = Box.createHorizontalGlue();
        horizontalGlue_1.setPreferredSize(new Dimension(32767, 0));
        panel_3.add(horizontalGlue_1);
		
        Component verticalGlue = Box.createVerticalGlue();
        verticalGlue.setPreferredSize(new Dimension(0, 32767));
        add(verticalGlue);
    }
}
