/*
  CoolantValvesPanel.java
  S. Edward Dolan
  Monday, August 28 2023
*/

package edgrind;

import java.awt.GridLayout;
// 
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;

/*
  For Spindle 1
  
  .- Coolant Valves ------.
  | [x]3    [x]2    [ ]1  |
  '-----------------------'
  
  For Spindle 2
  
  .- Coolant Valves ------.
  | [x]6    [x]5    [ ]4  |
  '-----------------------'
*/
@SuppressWarnings("serial")
public class CoolantValvesPanel extends JPanel {
    // index 5 is valve #6, index 0 is valve #1
    protected JCheckBox valves[] = new JCheckBox[6];
    protected int spindle;
    CoolantValvesPanel(int spindle) {
        setBorder(BorderFactory.createTitledBorder("Coolant" + " Valves"));
        setLayout(new GridLayout(1, 3));
        setSpindle(spindle);
    }
    /**
       Reinitialize the valve layout.
       <p>
       Remove and reinstall all valve checkboxen depending on the current
       spindle.
       </p>
     */
    private void initValves() {
        for (int i=0; i<6; ++i)
            if (valves[i] != null) {
                remove(valves[i]);
                valves[i] = null;
            }
        if (spindle == 1) {
            for (int i=2; i>=0; --i) {
                valves[i] = new JCheckBox("" + (i+1));
                valves[i].setHorizontalAlignment(SwingUtilities.CENTER);
                add(valves[i]);
            }
        }
        else {
            for (int i=5; i>=3; --i) {
                valves[i] = new JCheckBox("" + (i+1));
                valves[i].setHorizontalAlignment(SwingUtilities.CENTER);
                add(valves[i]);
            }
        }
        validate();
        repaint();
    }
    public void setSpindle(int spindle) {
        this.spindle = spindle;
        initValves();
    }
    /**
       @return "", "63", "21", etc.
    */
    public String getOpenValves() {
        String out = "";
        if (spindle == 1) {
            for (int i=2; i>=0; --i)
                if (valves[i].isSelected())
                    out += (i + 1);
        }
        else {
            for (int i=5; i>=3; --i)
                if (valves[i].isSelected())
                    out += (i + 1);
        }
        return out;
    }
    /*
      The string "61" or "16" would open valves 1 and 6 (0, and 5 in the
      valves array, respectively)
    */
    public void setOpenValves(String s) {
        for (Character c : s.toCharArray())
            switch (c) {
            case '1': valves[0].setSelected(true); break;
            case '2': valves[1].setSelected(true); break;
            case '3': valves[2].setSelected(true); break;
            case '4': valves[3].setSelected(true); break;
            case '5': valves[4].setSelected(true); break;
            case '6': valves[5].setSelected(true); break;
            default: break;  // ignore anything else
            }
    }
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        for (int i=0; i<6; ++i)
            if (valves[i] != null)
                valves[i].setEnabled(b);
    }
}
