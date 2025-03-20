/*
  WheelsPanel.java
  S. Edward Dolan
  Saturday, August 26 2023
*/

package edgrind;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
// 
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

@SuppressWarnings("serial")
public class WheelsPanel extends JPanel {
    protected MainFrame mainFrame;
    protected SpindlePanel spindlePanel1, spindlePanel2;
    WheelsPanel(MainFrame mf) throws Exception {
        this.mainFrame = mf;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JTabbedPane tp = new JTabbedPane(JTabbedPane.BOTTOM);
        spindlePanel1 = new SpindlePanel(this, 1);
        tp.add("Spindle 1", spindlePanel1);
        spindlePanel2 = new SpindlePanel(this, 2);
        tp.add("Spindle 2", spindlePanel2);
        add(tp);
        tp.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    JTabbedPane p = (JTabbedPane)e.getSource();
                    if (p.getSelectedIndex() == 0)
                        spindlePanel1.fitSketch();
                    else
                        spindlePanel2.fitSketch();
                }
            });
        setVisible(true);
    }
    public MainFrame getMainFrame() {
        return mainFrame;
    }
    public Dict getWheelData(int spindleNum, int wheelNum) {
        Dict d = null;
        if (spindleNum == 1)
            d = spindlePanel1.getWheelData(wheelNum);
        else
            d = spindlePanel2.getWheelData(wheelNum);
        if (d == null) 
            JOptionPane
                .showMessageDialog(mainFrame, "Spindle " + spindleNum +
                                   " Wheel " + wheelNum + " is in an" +
                                   " invalid state. Load a wheel or save" +
                                   " the currenly loaded wheel.");
        return d;
    }
}
