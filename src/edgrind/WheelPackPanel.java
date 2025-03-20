/*
  WheelPackPanel.java
  S. Edward Dolan
  Wednesday, October 11 2023
*/

package edgrind;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
// 
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// 
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

/**
   <pre>
   .--Wheel Pack-------.
   |  Name: |Foo   |v| |
   |            [Save] |
   '-------------------'
   </pre>
 */
@SuppressWarnings("serial")
public class WheelPackPanel extends JPanel implements ItemListener,
                                                      ActionListener {
    protected JLabel lblName;
    protected JComboBox<String> cboWheelPackName;
    protected JButton butSave;
    protected SpindlePanel spindlePanel;
    public WheelPackPanel(SpindlePanel spindlePanel) {
        this.spindlePanel = spindlePanel;
        setBorder(BorderFactory
                  .createCompoundBorder(BorderFactory
                                        .createTitledBorder("Wheel Pack"),
                                        // padding inside the titled border
                                        BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0)));
        GridBagLayout gb = new GridBagLayout();;
        setLayout(gb);
        GridBagConstraints gc = new GridBagConstraints();
        Insets ins = new Insets(2, 2, 2, 2);
        gc.insets = ins;
        // combo label
        lblName = new JLabel("Name:");
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = .3;
        gc.ipadx = 3;
        gc.ipadx = 10;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblName, gc);
        add(lblName);
        // wheel pack names combo
        cboWheelPackName = new JComboBox<String>();
        lblName.setLabelFor(cboWheelPackName);
        cboWheelPackName.addItem(""); // no wheel pack currently selected
        for (String s : WheelPack.allWheelPackNames())
            cboWheelPackName.addItem(s);
        cboWheelPackName.addItemListener(spindlePanel);
        gc.gridx = 1;
        gc.weightx = 1;
        // gc.anchor = GridBagConstraints.CENTER;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(cboWheelPackName, gc);
        add(cboWheelPackName);
        // save button
        butSave = new JButton("Save");
        butSave.addActionListener(this);         // both this
        butSave.setEnabled(false); // initially no wheel pack to save
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 0;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.NONE;
        gb.setConstraints(butSave, gc);
        add(butSave);
        cboWheelPackName.addItemListener(this);
        cboWheelPackName.setSelectedItem("");
    }
    public JComboBox<String> getCboWheelPackName() {
        return cboWheelPackName;
    }
    public void enableSave(boolean b) {
        butSave.setEnabled(b);
    }
    public String getWheelPackName() {
        return (String)cboWheelPackName.getSelectedItem();
    }
    @Override
    public void setEnabled(boolean b) {
        lblName.setEnabled(b);
        cboWheelPackName.setEnabled(b);
        butSave.setEnabled(b);
    }
    // handle wheel pack change combo
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboWheelPackName) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String name = (String)e.getItem();
                if (name.isEmpty())
                    butSave.setEnabled(false);
                else
                    butSave.setEnabled(true);
            }
            else {
                // TODO: anything on unselect?
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butSave) {
            if (spindlePanel.hasDirtyWheels()) {
                JOptionPane.showMessageDialog(spindlePanel,
                                              "Cannot save a wheel pack" +
                                              " until all wheels have been" +
                                              " saved.");
                return;
            }
            // save clicked
            while (true) {
                String wheelPackName = (String)JOptionPane
                    .showInputDialog(spindlePanel,
                                     "Name:",
                                     "Save Wheel Pack",
                                     JOptionPane.PLAIN_MESSAGE,
                                     null,
                                     null,
                                     getWheelPackName());
                if (wheelPackName == null)
                    break;       // cancel clicked
                else if (wheelPackName.isEmpty()) {
                    JOptionPane.showMessageDialog(spindlePanel,
                                                  "Weel pack name cannot" +
                                                  " be empty.");
                    continue;
                }
                WheelPack wheelPack = WheelPack.getWheelPack(wheelPackName);
                if (wheelPack != null) {
                    int x = JOptionPane
                        .showConfirmDialog(spindlePanel,
                                           "Overwright existing" +
                                           " wheel pack `" + wheelPackName +
                                           "'?", "Confirm...",
                                           JOptionPane.YES_NO_OPTION);
                    if (x == JOptionPane.YES_OPTION) {
                        // update an existing wheel pack
                        WheelPack
                            .addWheelPack(wheelPackName,
                                          spindlePanel
                                          .assembleWheelPack(wheelPackName));
                        
                    }
                }
                else
                    // add a fresh wheel pack to the db
                    WheelPack.addWheelPack(wheelPackName,
                                           spindlePanel
                                           .assembleWheelPack(wheelPackName));
                break;
            }
        }        
    }
    /**
       Reload all wheel packs from the db.
       <p>
       This occurs when a wheel pack is saved or modified.
       </p>
    */
    public void refreshWheelPackList() {
        // System.out.println("WheelPackPanel.refreshWheelPackList()");
        String currentName = (String)cboWheelPackName.getSelectedItem();
        cboWheelPackName.setSelectedItem("");
        cboWheelPackName.removeItemListener(spindlePanel);
        cboWheelPackName.removeAllItems();
        cboWheelPackName.addItem("");
        for (String s : WheelPack.allWheelPackNames())
            cboWheelPackName.addItem(s);
        cboWheelPackName.addItemListener(spindlePanel);
        cboWheelPackName.setSelectedItem(currentName);
    }
}
