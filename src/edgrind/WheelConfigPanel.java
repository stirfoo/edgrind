/*
  WheelConfigPanel.java
  S. Edward Dolan
  Saturday, September 30 2023
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
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
//
import edgrind.sketch.WheelSketch;
//
import edgrind.error.EdError;

/*
  .----------------------------.
  |.--Wheel N-----------------.|
  ||   Name: |W_1V1       |v| ||
  ||  Z Len: |              | ||
  ||[ ] Flip [ ] Front        ||
  ||[Edit]   [Reload]  [Save] ||
  |'--------------------------'|
  '----------------------------'
 */

/**
   Select, configure, edit, and save a wheel.
   <p>
   This is used by SpindlePanel to place wheels on the spindle arbor.
   </p>
   <p>
   A wheel may be selected by name. The setup Z length may be changed. The
   wheel may be flipped on the arbor. The setup point may be changed from the
   front or back of the wheel. The wheel may be edited. The wheel may be
   saved, possibly under a different name, to the current Wheel db.
   </p>
 */
@SuppressWarnings("serial")
public class WheelConfigPanel extends JPanel implements ItemListener,
                                                        ActionListener {
    /** Choose the current wheel to load, by name. */
    protected JComboBox<String> cboWheelName;
    /** Label for the combo. */
    protected JLabel lblName;
    /** Modify the z length. */
    protected FloatEdit fltZlen;
    /** Label for the z length edit box. */
    protected JLabel lblZlen;
    /** Flip the wheel on the adapter. */
    protected JCheckBox chkFlip;
    /** Change the z-length referece surface. */
    protected JCheckBox chkFront;
    /** Start/Stop editing the wheel. */
    protected JToggleButton butEdit;
    /** Reload the last saved state of the wheel. */
    protected JButton butReload;
    /** Save the wheel */
    protected JButton butSave;
    /** The current wheel sketch. */
    protected WheelSketch sketch;
    /** The parent SpindlePanel */
    protected SpindlePanel spindlePanel;
    /** This panel's wheel number: 1, 2, or 3. */
    protected int wheelNumber;
    /**
       Allow wheel unlocking when the Edit button is toggled off.
       
       <p> If the edit button is toggled on and the wheel is locked,
       butEdit.doClick() is called to toggle the button off which re-enters
       the actionPerformed() method. This would normally unlock the wheel as
       the edit is complete. But we don't want to do that if another
       WheelConfigPanel has the wheel locked.  </p>
    */
    protected boolean doUnlock = true;
    /**
       @param spindlePanel the parent panel
       @param wheelNumber 1, 2, or 3
    */
    public WheelConfigPanel(SpindlePanel spindlePanel, int wheelNumber) {
        this.spindlePanel = spindlePanel;
        this.wheelNumber = wheelNumber;
        sketch = null;
        setBorder(BorderFactory
                  .createCompoundBorder(BorderFactory
                                        .createTitledBorder("Wheel " +
                                                            wheelNumber),
                                        // padding inside the titled border
                                        BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0)));
        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);
        GridBagConstraints gc = new GridBagConstraints();
        Insets ins = new Insets(2, 2, 2, 2);
        gc.insets = ins;
        int nextGridY = 0;
        // label
        lblName = new JLabel("Name:");
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.anchor = GridBagConstraints.EAST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(lblName, gc);
        add(lblName);
        // wheel name combo
        cboWheelName = new JComboBox<String>();
        lblName.setLabelFor(cboWheelName);
        cboWheelName.addItem(""); // no wheel currently selected
        for (String s : Wheel.allWheelNames())
            cboWheelName.addItem(s);
        cboWheelName.addItemListener(spindlePanel);
        gc.gridx = 1;
        gc.gridy = nextGridY++;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(cboWheelName, gc);
        add(cboWheelName);
        // zlen label
        lblZlen = new JLabel("Z Len:");
        lblZlen.setToolTipText("Distance from back of adapter to wheel");
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(lblZlen, gc);
        add(lblZlen);
        // zlen text
        fltZlen = new FloatEdit(55 / 25.4); // 55mm, fixed adapter dimension
        fltZlen.setToolTipText("Distance from back of adapter to wheel");
        lblZlen.setLabelFor(fltZlen);
        fltZlen.addActionListener(this);
        gc.gridx = 1;
        gc.gridy = nextGridY++;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(fltZlen, gc);
        add(fltZlen);
        // flip
        chkFlip = new JCheckBox("Flip");
        chkFlip.setToolTipText("Flip the wheel on the adaptor");
        chkFlip.addActionListener(this);
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST;
        gb.setConstraints(chkFlip, gc);
        add(chkFlip);
        // front
        chkFront = new JCheckBox("Front");
        chkFront.setToolTipText("Z Len is to outside edge of wheel");
        chkFront.addActionListener(this);
        gc.gridx = 1;
        gc.gridy = nextGridY++;
        gc.anchor = GridBagConstraints.EAST;
        gb.setConstraints(chkFront, gc);
        add(chkFront);
        // edit button
        butEdit = new JToggleButton("Edit");
        butEdit.addActionListener(this);
        gc.gridx = 0;
        gc.gridy = nextGridY;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        gb.setConstraints(butEdit, gc);
        add(butEdit);
        // reload button
        butReload = new JButton("Reload");
        butReload.setToolTipText("Reload the wheel's last saved state");
        butReload.addActionListener(this);
        gc.gridx = 1;
        gc.gridy = nextGridY;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        gb.setConstraints(butReload, gc);
        add(butReload);
        // save button
        butSave = new JButton("Save");
        butSave.addActionListener(this);         // both this
        // butSave.addActionListener(spindlePanel); // and SpindlePanel
        gc.gridx = 2;
        gc.gridy = nextGridY++;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        gb.setConstraints(butSave, gc);
        add(butSave);
        /*
          Do this last to initially disable all the components as the initial
          value of the wheel name will be empty (no wheel selected).
        */
        cboWheelName.addItemListener(this);
        cboWheelName.setSelectedItem("");
        enableComponents(false, false);
    }
    public MainFrame getMainFrame() {
        return spindlePanel.getMainFrame();
    }
    /** Return true if this panel currently has a wheel loaded */
    public boolean hasWheel() {
        return getWheelName() != "";
    }
    /**
       Get the currently loaded wheel name.
       @return the name or null if no wheel currently loaded
    */
    public String getWheelName() {
        return (String)cboWheelName.getSelectedItem();
    }
    /**
       Get the currently loaded wheel.
       @return the wheel or null if no wheel is currently loaded
    */
    public Wheel getWheel() {
        return Wheel.getWheel(getWheelName());
    }
    /**
       Find if the sketch dims have been modified since last loaded or saved.
    */
    public boolean isSketchDictDirty() {
        return sketch != null && sketch.isDictDirty();
    }
    /**
       Find if the wheel configuration (zlen, flip, spindle, mirror) has
       changed since last loaded or saved.
    */
    public boolean isWheelConfigDirty() {
        return sketch != null && sketch.isConfigDirty();
    }
    /**
       Set this panel's current wheel sketch.
    */
    public void setSketch(WheelSketch sketch) {
        this.sketch = sketch;
    }
    /**
       Get this panel's current wheel sketch.
       @return the sketch or null if no wheel is currently loaded
    */
    public WheelSketch getSketch() {
        return sketch;
    }
    /**
       Get this panels current wheel Z length.
    */
    public double getZLength() {
        return fltZlen.getValue();
    }
    /**
       Get this panel's current flipped state.
       @return true if flipped
    */
    public boolean getFlip() {
        return chkFlip.isSelected();
    }
    /**
       Get this panel's current front state.
       @return true if the z-len is ref'd to the <em>front</em> of the wheel
    */
    public boolean getFront() {
        return chkFront.isSelected();
    }
    /**
       Enable/Disable this panel's components.
       <p>
       This is called from a number of locations to enable/disable this
       panel's components depending on what action occured.
       </p>
       @param b true if components should be enabled, false if not
       @param edit true if called as the result of the Edit button action
    */
    protected void enableComponents(boolean b, boolean edit) {
        if (edit) {
            lblName.setEnabled(b);
            cboWheelName.setEnabled(b);
        }
        else
            butEdit.setEnabled(b);
        lblZlen.setEnabled(b);
        fltZlen.setEnabled(b);
        chkFlip.setEnabled(b);
        chkFront.setEnabled(b);
        butReload.setEnabled(b);
        butSave.setEnabled(b);
    }
    // called from SpindlePanel when an edit button toggled
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        lblName.setEnabled(b);
        cboWheelName.setEnabled(b);
        if (!((String)cboWheelName.getSelectedItem()).isEmpty())
            enableComponents(b, false);
    }
    // handle wheel change combo
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboWheelName) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (((String)e.getItem()).isEmpty())
                    enableComponents(false, false);
                else
                    enableComponents(true, false);
            }
            else {
            }
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butEdit)
            doEdit(e);
        else if (e.getSource() == butReload)
            doReload();
        else if (e.getSource() == butSave)
            doSave();
        else {
            // flip, front, or z length changed
            if (fltZlen.isOk())
                sketch.spindleCfg(spindlePanel.spindleNum,
                                  getZLength(),
                                  getFlip(),
                                  getFront());
            else
                // dont update the zlen, the textfield value is invalid
                sketch.spindleCfg(spindlePanel.spindleNum,
                                  getFlip(),
                                  getFront());
        }
        spindlePanel.sketchScene.fitAll();
    }
    /**
       This panel's Edit button was toggled.
    */
    private void doEdit(ActionEvent e) {
        if (((JToggleButton)e.getSource()).isSelected()) {
            if (getWheel().isLocked()) {
                JOptionPane
                    .showMessageDialog(spindlePanel,
                                       "The wheel `" + getWheelName() +
                                       "' is currently locked by another" +
                                       " sketch and cannot be edited.");
                doUnlock = false;
                butEdit.doClick(); // turn it back off
            }
            else {
                /*
                  zero the z length of the sketch so the wheel edge is
                  aligned with the origin
                */
                sketch.spindleCfg(spindlePanel.spindleNum,
                                  0.0, // zero
                                  getFlip(),
                                  getFront());
                enableComponents(false, true);
                getWheel().setLocked(true);
                doUnlock = true;
                spindlePanel.doWheelEdit(wheelNumber - 1);
            }
        }
        else if (doUnlock) {
            // System.out.println("WheelConfigPanel edit OFF");
            // reset it back after editing
            sketch.spindleCfg(spindlePanel.spindleNum,
                              getZLength(),
                              getFlip(),
                              getFront());
            enableComponents(true, true);
            getWheel().setLocked(false);
            spindlePanel.doWheelUnEdit(wheelNumber - 1);
        }
    }
    private void doReload() {
        Wheel wheel = getWheel();
        if (wheel.isLocked()) {
            EdError.showError(getMainFrame(),
                              "Cannot reload wheel \"" + wheel.getName() +
                              "\". It is locked by another panel");
            return;
        }
        wheel.reload();
        sketch.setDictDirty(false);
    }
    /**
       The Save button was clicked.
    */
    private void doSave() {
        if (getWheel().isLocked()) {
            // the wheel is locked, inform the user and return
            JOptionPane
                .showMessageDialog(getMainFrame(),
                                   "The wheel `" + getWheelName() +
                                   "' is currently locked by another" +
                                   " sketch and cannot be saved.");
            return;
        }
        /*
          Try to save the wheel, possibly under another name, but don't allow
          modification of system wheels
        */
        while (true) {
            String name = (String)JOptionPane
                .showInputDialog(spindlePanel,
                                 "Name:",
                                 "Save Wheel",
                                 JOptionPane.PLAIN_MESSAGE,
                                 null,
                                 null,
                                 getWheelName());
            if (name == null)
                break;       // cancel clicked
            else if (name.isEmpty())
                continue;   // what to do on empty input here....
            if (Wheel.getWheel(name) != null) {
                if (Wheel.getWheel(name).isSystem()) {
                    JOptionPane
                        .showMessageDialog(spindlePanel,
                                           "The system wheel " + name +
                                           " cannot be overwritten.");
                    continue;
                }
                int x = JOptionPane
                    .showConfirmDialog(spindlePanel,
                                       "Overwright existing" +
                                       " wheel `" + name + "'?",
                                       "Confirm...",
                                       JOptionPane.YES_NO_OPTION);
                if (x == JOptionPane.YES_OPTION) {
                    /*
                      This will ultimately call refreshWheelList() on all
                      WheelConfigPanel.
                    */
                    Wheel.addWheel(Wheel.getWheel((String)cboWheelName
                                                  .getSelectedItem()).type,
                                   (Dict)sketch.getSpecs().clone(),
                                   name);
                    cboWheelName.setSelectedItem(name);
                }
            }
            else {
                Wheel.addWheel(Wheel.getWheel((String)cboWheelName
                                              .getSelectedItem()).type,
                               (Dict)sketch.getSpecs().clone(),
                               name);
                cboWheelName.setSelectedItem(name);
            }
            break;
        }
    }
    /**
       Called when a wheel is added to or updated in the Wheel db.
       
       <p>
       This will re-add all the wheel names to the combo, sorted
       alphabetically, with the new wheel added/updated.
       </p>
    */
    public void refreshWheelList() {
        // System.out.println("WheelConfigPanel.refreshWheelList");
        // save the currently selected
        String currentName = (String)cboWheelName.getSelectedItem();
        // remove and disconnect the current sketch (may already be null)
        cboWheelName.setSelectedItem("");
        // temporarily unplug the combo box
        cboWheelName.removeItemListener(spindlePanel);
        cboWheelName.removeItemListener(this);
        // clear it
        cboWheelName.removeAllItems();
        // re-add all from the Wheel db (this is a bit of a kludge)
        cboWheelName.addItem("");
        for (String s : Wheel.allWheelNames())
            cboWheelName.addItem(s);
        // wire the combo back up
        cboWheelName.addItemListener(spindlePanel);
        cboWheelName.addItemListener(this);
        // re-select the originally selected wheel
        cboWheelName.setSelectedItem(currentName);
    }
    /**
       Clear this panel (no wheel loaded).
    */
    public void clear() {
        cboWheelName.setSelectedItem("");
        // TODO: anything else?
    }
    public void loadWheelFromPack(boolean flip, Dict d) {
        fltZlen.setText("" + d.doubleAt("z-length"));
        boolean b = d.boolAt("flip?");
        chkFlip.setSelected(flip ? !b : b);
        chkFront.setSelected(d.boolAt("front?"));
        cboWheelName.setSelectedItem(d.stringAt("wheel-db-name"));
    }
}
