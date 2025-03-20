package edgrind;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import javax.swing.border.LineBorder;
import javax.swing.Box;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class ContourPanel extends JPanel {
    private MainFrame parent;
    public ContourPanel(MainFrame parent) {
        this.parent = parent;
        setBorder(new EmptyBorder(3, 3, 3, 3));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
        JPanel panel_6 = new JPanel();
        panel_6.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_6.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panel_6);
        panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		
        JPanel panel = new JPanel();
        panel_6.add(panel);
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                                                                  229)),
                                         "Straights", TitledBorder.CENTER,
                                         TitledBorder.TOP, null,
                                         new Color(51, 51, 51)));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new EmptyBorder(0, 0, 0, 3));
        panel.add(panel_1);
        panel_1.setLayout(new GridLayout(10, 1, 0, 0));

        // straights labels A-I
        panel_1.add(new JLabel(""));
        for (int i=65; i<74; ++i) {
            JLabel lbl = new JLabel(Character.toString((char)i));
            lbl.setHorizontalAlignment(SwingConstants.RIGHT);
            panel_1.add(lbl);
        }
		
        JPanel panel_2 = new JPanel();
        panel.add(panel_2);
        panel_2.setLayout(new GridLayout(10, 2, 3, 0));
		
        JLabel lblAngle = new JLabel("Angle");
        lblAngle.setHorizontalAlignment(SwingConstants.CENTER);
        panel_2.add(lblAngle);
		
        JLabel lblDistance = new JLabel("Distance");
        lblDistance.setHorizontalAlignment(SwingConstants.CENTER);
        panel_2.add(lblDistance);

        /* ------------------------------------------------------------
           straights text fields
        */
        for (int i=0; i<9; ++i) {
            panel_2.add(parent.prog.get("straights-angle-" + (char)('a'+i))
                        .getWidget());
            panel_2.add(parent.prog.get("straights-distance-" + (char)('a'+i))
                        .getWidget());
        }
		
        JPanel panel_3 = new JPanel();
        panel_6.add(panel_3);
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207,
                                                                    229)),
                                           "Radii", TitledBorder.CENTER,
                                           TitledBorder.TOP, null,
                                           new Color(51, 51, 51)));
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));
		
        JPanel panel_4 = new JPanel();
        panel_4.setBorder(new EmptyBorder(0, 0, 0, 3));
        panel_3.add(panel_4);
        panel_4.setLayout(new GridLayout(10, 1, 0, 0));
		
        JLabel label_1 = new JLabel("");
        panel_4.add(label_1);
		
        JLabel lblN = new JLabel("N");
        lblN.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblN);
		
        JLabel lblO = new JLabel("O");
        lblO.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblO);
		
        JLabel lblP = new JLabel("P");
        lblP.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblP);
		
        JLabel lblQ = new JLabel("Q");
        lblQ.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblQ);
		
        JLabel lblR = new JLabel("R");
        lblR.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblR);
		
        JLabel lblS = new JLabel("S");
        lblS.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblS);
		
        JLabel lblT = new JLabel("T");
        lblT.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblT);
		
        JLabel lblU = new JLabel("U");
        lblU.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblU);
		
        JLabel lblV = new JLabel("V");
        lblV.setHorizontalAlignment(SwingConstants.RIGHT);
        panel_4.add(lblV);
		
        JPanel panel_5 = new JPanel();
        panel_3.add(panel_5);
        panel_5.setLayout(new GridLayout(10, 3, 0, 0));
		
        JLabel lblStart = new JLabel("Start");
        lblStart.setHorizontalAlignment(SwingConstants.CENTER);
        panel_5.add(lblStart);
		
        JLabel lblRotation = new JLabel("Rotation");
        lblRotation.setHorizontalAlignment(SwingConstants.CENTER);
        panel_5.add(lblRotation);
		
        JLabel lblRadius = new JLabel("Radius");
        lblRadius.setHorizontalAlignment(SwingConstants.CENTER);
        panel_5.add(lblRadius);

        for (int i=0; i<9; ++i) {
            panel_5.add(parent.prog.get("radii-start-" + (char)('n'+i))
                        .getWidget());
            panel_5.add(parent.prog.get("radii-rotation-" + (char)('n'+i))
                        .getWidget());
            panel_5.add(parent.prog.get("radii-radius-" + (char)('n'+i))
                        .getWidget());
        }
		
        Component verticalGlue = Box.createVerticalGlue();
        add(verticalGlue);
        verticalGlue.setPreferredSize(new Dimension(0, 32767));
    }
}
