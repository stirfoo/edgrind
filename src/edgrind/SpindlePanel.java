/*
  SpindlePanel.java
  S. Edward Dolan
  Friday, September 29 2023
*/

package edgrind;

import java.awt.Color;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
// 
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
// 
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
//
import edgrind.sketch.*;

/**
   Allow the user to create/edit wheels and configure a wheel pack.

   <pre>
  .--------------------------------------------------------------------------.
  |.--Wheel Pack--------..--------------------------------------------------.|
  ||  Name: |Foo   |v|  ||                                                  ||
  ||             [Save] ||                                                  ||
  |'--------------------'|                                                  ||
  |   Adapter: |40mm |v| |                                                  ||
  |.--Wheel 1-----------.|                                                  ||
  ||    Name: |W_1V1|v| ||                                                  ||
  ||   Z Len: |1.2345 | ||                                                  ||
  || [ ] Flip [x] Front ||                                                  ||
  ||      [Edit] [Save] ||                                                  ||
  |'--------------------'|                                                  ||
  |.--Wheel 2-----------.|                                                  ||
  ||    Name: |W_1V1|v| ||                                                  ||
  ||   Z Len: |1.2345 | ||                                                  ||
  || [ ] Flip [ ] Front ||                                                  ||
  ||      [Edit] [Save] ||                                                  ||
  |'--------------------'|                   Sketch Scene                   ||
  |.--Wheel 3-----------.|                                                  ||
  ||    Name: |W_1V1|v| ||                                                  ||
  ||   Z Len: |1.2345 | ||                                                  ||
  || [x] Flip [ ] Front ||                                                  ||
  ||      [Edit] [Save] ||                                                  ||
  |'--------------------'|                                                  ||
  |                      |                                                  ||
  |                      |                                                  ||
  |                      |                                                  ||
  |                      |                                                  ||
  |                      '--------------------------------------------------'|
  '--------------------------------------------------------------------------'
  </pre>
 */
@SuppressWarnings("serial")
public class SpindlePanel extends JPanel implements ItemListener,
                                                    DBChangeListener {
    WheelsPanel wheelsPanel;
    SketchScene sketchScene;
    WheelPackPanel wheelPackPanel;
    JLabel lblAdapterName;
    JComboBox<String> cboAdapterName;
    WheelConfigPanel[] cfgPanels = new WheelConfigPanel[3];
    int spindleNum;
    /*
      When a wheel is edited, the current adapter sketch needs to be removed
      and added back when done editing.
    */
    Sketch adapterSketch;
    SpindlePanel(WheelsPanel wheelsPanel, int spindleNum) throws Exception {
        super();
        this.wheelsPanel = wheelsPanel;
        this.spindleNum = spindleNum;
        sketchScene = new SketchScene(true); // true means show the origin
        GridBagLayout gb = new GridBagLayout();
        setLayout(gb);
        GridBagConstraints gc = new GridBagConstraints();
        Insets ins = new Insets(3, 3, 3, 3);
        initWheelPackPanel(gb, gc);
        initAdapterCombo(gb, gc, ins);
        initWheelConfigPanels(gb, gc);
        initSketchScene(gb, gc);
        cboAdapterName.setSelectedItem("40mm");
        Wheel.addDBChangeListener(this);
        WheelPack.addDBChangeListener(this);
    }
    public MainFrame getMainFrame() {
        return wheelsPanel.getMainFrame();
    }
    /**
       Find if a wheel's dimensions have been changed since last loaded or
       saved. This gets queried when the WheelPackPanel combo changes or its
       Save button is clicked.
    */
    public boolean hasDirtyWheels() {
        return (cfgPanels[0].isSketchDictDirty() ||
                cfgPanels[1].isSketchDictDirty() ||
                cfgPanels[2].isSketchDictDirty());
    }
    public WheelPack assembleWheelPack(String wheelPackName) {
        Dict d1 = null, d2 = null, d3 = null;
        if (cfgPanels[0].hasWheel())
            d1 = new Dict("wheel-db-name", cfgPanels[0].getWheelName(),
                          "z-length", cfgPanels[0].getZLength(),
                          "flip?", cfgPanels[0].getFlip(),
                          "front?", cfgPanels[0].getFront());
        if (cfgPanels[1].hasWheel()) {
            d2 = new Dict("wheel-db-name", cfgPanels[1].getWheelName(),
                          "z-length", cfgPanels[1].getZLength(),
                          "flip?", cfgPanels[1].getFlip(),
                          "front?", cfgPanels[1].getFront());
        }
        if (cfgPanels[2].hasWheel())
            d3 = new Dict("wheel-db-name", cfgPanels[2].getWheelName(),
                          "z-length", cfgPanels[2].getZLength(),
                          "flip?", cfgPanels[2].getFlip(),
                          "front?", cfgPanels[2].getFront());
        return new WheelPack(wheelPackName,
                             (String)cboAdapterName.getSelectedItem(),
                             spindleNum, d1, d2, d3);
    }
    protected void initWheelPackPanel(GridBagLayout gb,
                                      GridBagConstraints gc) {
        wheelPackPanel = new WheelPackPanel(this);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gb.setConstraints(wheelPackPanel, gc);
        add(wheelPackPanel);
    }
    protected void initAdapterCombo(GridBagLayout gb,
                                    GridBagConstraints gc,
                                    Insets ins) throws Exception {
        // label
        gc.insets = ins;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.EAST;
        lblAdapterName = new JLabel("Adapter:");
        gb.setConstraints(lblAdapterName, gc);
        add(lblAdapterName);
        // adapter select combo
        gc.gridx = 1;
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.WEST;
        // gc.fill = GridBagConstraints.BOTH;
        cboAdapterName = new JComboBox<String>();
        for (String name : WheelAdapter.allAdapterNames())
            cboAdapterName.addItem(name);
        cboAdapterName.addItemListener(this);
        gb.setConstraints(cboAdapterName, gc);
        add(cboAdapterName);
    }
    void initWheelConfigPanels(GridBagLayout gb, GridBagConstraints gc) {
        for (int i=0; i<3; ++i) {
            cfgPanels[i] = new WheelConfigPanel(this, i + 1);
            gc.gridx = 0;
            gc.gridy = i + 2;
            gc.gridwidth = 2;
            gc.anchor = GridBagConstraints.WEST;
            gb.setConstraints(cfgPanels[i], gc);
            add(cfgPanels[i]);
        }
    }
    protected void initSketchScene(GridBagLayout gb, GridBagConstraints gc) {
        gc.insets = new Insets(0, 0, 0, 0);
        gc.gridx = 2;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridheight = 6;
        gc.fill = GridBagConstraints.BOTH;
        gb.setConstraints(sketchScene, gc);
        add(sketchScene);
    }
    // loadWheel helper method
    private void addWheel(Wheel wheel, WheelSketch s, WheelConfigPanel p)
        throws Exception {
        if (wheel.isSystem())
            s.setReadOnly(true);
        s.showDims(false);
        s.setFillShape(true);
        wheel.addSpecsListener(s);
        p.setSketch(s);
        s.spindleCfg(spindleNum, p.getZLength(), p.getFlip(), p.getFront());
        sketchScene.addSketch(s);
        // wheel is freshly loaded from the db via the combo box
    }
    protected void loadWheel(Wheel wheel, int n) {
        // System.out.println("spindlePanel.loadWheel: " + wheel.getName());
        WheelType wtype = wheel.getType();
        WheelConfigPanel p = cfgPanels[n - 1];
        switch (wtype) {
        case W_1A1: {
            try {
                /*
                  Use the wheel's sketch specs, which may have been
                  altered by another WheelConfigPanel.
                */
                addWheel(wheel,
                         new W1A1Sketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_1V1: {
            try {
                addWheel(wheel,
                         new W1V1Sketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_1A1_CMF: {
            try {
                addWheel(wheel,
                         new W1A1CmfSketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_11V9: {
            try {
                addWheel(wheel,
                         new W11V9Sketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_11V5: {
            try {
                addWheel(wheel,
                         new W11V5Sketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_1F1: {
            try {
                addWheel(wheel,
                         new W1F1Sketch(wheel.getSketchSpecs(), sketchScene),
                         p);
            }
            catch (Exception ignore) {
                System.out.println("BOOM: SpindlePanel.loadWheel(): "
                                   + ignore);
            }
            break;
        }
        case W_1E1: break;
        }
    }
    private void unloadWheel(Wheel wheel, int n) {
        // System.out.println("SpindlePanel.unloadWheel: " + wheel.getName());
        Sketch s = cfgPanels[n - 1].getSketch();
        wheel.removeSpecsListener(s);
        sketchScene.removeSketch(s);
        cfgPanels[n - 1].setSketch(null);
    }
    private void loadAdapter(WheelAdapter adapter) {
        try {
            adapterSketch
                = new WheelAdapterSketch(adapter.getSpecs(), sketchScene,
                                         spindleNum == 2);
            adapterSketch.setFillColor(Color.black);
            adapterSketch.setFillShape(true);
            adapterSketch.setReadOnly(true);
            adapterSketch.setZValue(-1);
            adapterSketch.showDims(false);
            sketchScene.addSketch(adapterSketch);
            wheelsPanel.mainFrame.simView
                .adapterChanged(adapterSketch.getModel(), spindleNum);
        }
        catch (Exception ignore) {
            System.err.println("BOOM: SpindlePanel.loadAdapter: " +
                               ignore.getMessage());
        }
        
    }
    private void unloadAdapter() {
        sketchScene.removeSketch(adapterSketch);
        adapterSketch = null;
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object c = e.getSource();
        if (handleWheelPackChange(c, e))
            return;
        if (handleAdapterChange(c, e))
            return;
        if (handleWheelChange(c, e))
            return;
    }
    private boolean handleWheelPackChange(Object c, ItemEvent e) {
        if (c == wheelPackPanel.getCboWheelPackName()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String name = (String)e.getItem();
                if (name.isEmpty())
                    for (WheelConfigPanel p : cfgPanels)
                        p.clear();
                else
                    loadWheelPack(WheelPack.getWheelPack(name));
            }
            // do nothing on unselect
            return true;
        }
        return false;
    }
    private boolean handleAdapterChange(Object c, ItemEvent e) {
        if (c == cboAdapterName) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                WheelAdapter adapter
                    = WheelAdapter.getAdapter((String)e.getItem());
                if (adapter != null)
                    loadAdapter(adapter);
            }
            else
                unloadAdapter();
            return true;
        }
        return false;
    }
    private boolean handleWheelChange(Object c, ItemEvent e) {
        // System.out.println("handleWheelChange");
        Wheel wheel;
        boolean ok = false;
        try {
            // wheel 1 change
            if (c == cfgPanels[0].cboWheelName) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    wheel = Wheel.getWheel((String)e.getItem());
                    if (wheel != null)
                        loadWheel(wheel, 1);
                }
                else {
                    unloadWheel(Wheel.getWheel((String)e.getItem()), 1);
                }
                ok = true;
            }
            // wheel 2 change
            else if (c == cfgPanels[1].cboWheelName) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    wheel = Wheel.getWheel((String)e.getItem());
                    if (wheel != null)
                        loadWheel(wheel, 2);
                }
                else {
                    unloadWheel(Wheel.getWheel((String)e.getItem()), 2);
                }
                ok = true;
            }
            // wheel 3 change
            else if (c == cfgPanels[2].cboWheelName) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    wheel = Wheel.getWheel((String)e.getItem());
                    if (wheel != null)
                        loadWheel(wheel, 3);
                }
                else {
                    unloadWheel(Wheel.getWheel((String)e.getItem()), 3);
                }
                ok = true;
            }
        }
        catch (Exception ignore) {
        }
        /*
          The wheel pack save button is only enabled if at least one wheel is
          loaded on the adapter.
        */
        wheelPackPanel.enableSave(cfgPanels[0].hasWheel() ||
                                  cfgPanels[1].hasWheel() ||
                                  cfgPanels[2].hasWheel());
        return ok;
    }
    /**
       Called when a WheelConfigPanel edit button is toggled on.
       @param pnum the index of the calling WheelConfigPanel
    */
    public void doWheelEdit(int pnum) {
        // disable changing adapter and remove its sketch
        lblAdapterName.setEnabled(false);
        cboAdapterName.setEnabled(false);
        sketchScene.removeSketch(adapterSketch);
        // disable loading a wheel pack
        wheelPackPanel.setEnabled(false);
        for (int i=0; i<3; ++i) {
            WheelConfigPanel p = cfgPanels[i];
            if (i == pnum) {
                notifySystemWheel(p);
                try {
                    p.getSketch().showDims(true);
                } catch(Exception x) {
                    // nada
                }
            }
            else {
                /* remove the other two wheels if present and disable their
                   WheelConfigPanel.
                */
                p.setEnabled(false);
                sketchScene.removeSketch(p.getSketch());
            }
        }
        sketchScene.fitAll();
    }
    /**
       Called when a WheelConfigPanel edit button is toggled off.
    */
    public void doWheelUnEdit(int pnum) {
        // re-enable changing adapter and add its sketch
        lblAdapterName.setEnabled(true);
        cboAdapterName.setEnabled(true);
        sketchScene.addSketch(adapterSketch);
        // re-enable changing wheel pack
        wheelPackPanel.setEnabled(true);
        for (int i=0; i<3; ++i) {
            WheelConfigPanel p = cfgPanels[i];
            if (i == pnum) {
                try {
                    /*
                      Transfer the sketch's dims back to the wheel's sketch
                      dims.  The wheel will notify any sketches referencing
                      the wheel of the change.
                    */
                    Sketch s = p.getSketch();
                    s.showDims(false);
                    p.getWheel().setSketchSpecs(s.getSpecs());
                }
                catch(Exception x) {
                    System.err.println("BOOM SpindlePanel.doWheelUnEdit(): " +
                                       x.getMessage());;
                }
            }
            else {
                p.setEnabled(true);
                sketchScene.addSketch(p.getSketch());
            }
        }
        sketchScene.fitAll();
    }
    /**
       Inform the user that they cannot edit a system wheel.

       @param p the panel whose edit button was clicked
    */
    private void notifySystemWheel(WheelConfigPanel p) {
        String wheelName = p.getWheelName();
        Wheel wheel = Wheel.getWheel(wheelName);
        if (wheel != null && wheel.isSystem()) {
            JOptionPane
                .showMessageDialog(this,
                                   "<html><p>The system wheel " + wheelName +
                                   " cannot be edited.<br>Its dimensions" +
                                   " will be shown but cannot be changed." +
                                   "<br>Save it under a different name," +
                                   " then edit.</p></html>");
        }
    }
    /**
       Called from WheelsPanel when a spindle tab is changed.

       This is necessary because the two SpindlePanel instances may have the
       same wheel loaded. If one panel changes the dims, only its
       SktechView.fitAll() is called.
    */
    public void fitSketch() {
        sketchScene.fitAll();
    }
    /**
       Load a wheel pack from the db.

       This is called from WheelPackPanel when its combo box changes. Each
       WheelConfigPanel is cleared then possibly loaded with the wheel
       configuration of the wheel pack.

       @param wheelPack the wheel pack to load
    */
    public void loadWheelPack(WheelPack wheelPack) {
        cboAdapterName.setSelectedItem(wheelPack.getAdapterName());
        WheelConfigPanel p = cfgPanels[0];
        boolean flip = wheelPack.getSpindle() != spindleNum;
        p.clear();
        if (wheelPack.getWheel1Specs() != null)
            p.loadWheelFromPack(flip, wheelPack.getWheel1Specs());
        p = cfgPanels[1];
        p.clear();
        if (wheelPack.getWheel2Specs() != null)
            p.loadWheelFromPack(flip, wheelPack.getWheel2Specs());
        p = cfgPanels[2];
        p.clear();
        if (wheelPack.getWheel3Specs() != null)
            p.loadWheelFromPack(flip, wheelPack.getWheel3Specs());
    }
    /**
       Called when an EdGrind db changed.
       
       @param type the type of db that changed
       @param name the name of the db item that changed
    */
    public void onDBChange(DBType type, String name) {
        // System.out.println("SpindlePanel.onDBChange() type:" + type +
        //                    " name:" + name);
        switch (type) {
        case WHEEL:
            for (WheelConfigPanel p : cfgPanels)
                p.refreshWheelList();
            break;
        case WHEEL_PACK:
            wheelPackPanel.refreshWheelPackList();
            break;
        }
    }
    public Dict getWheelData(int wheelNum) {
        WheelConfigPanel p = cfgPanels[wheelNum - 1];
        if (!p.hasWheel() ||
            // TODO: really need to save the wheel before using it?
            p.isSketchDictDirty())
            return null;
        return new Dict("wheel", p.getWheel(),
                        "zlen", p.getZLength(),
                        "flip?", p.getFlip(),
                        "front?", p.getFront());
    }
}
