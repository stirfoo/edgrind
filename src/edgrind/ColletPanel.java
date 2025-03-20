/*
  ColletPanel.java
  S. Edward Dolan
  Saturday, August 26 2023
*/

package edgrind;

import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
// 
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
// 
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
//
import edgrind.sketch.Sketch;
import edgrind.sketch.SketchScene;
import edgrind.sketch.NannPBSketch;

/*
  .--------------------------------------.
  |     Select Collet: | foo |v|         |
  |--------------------------------------|
  |                                      |
  |                                      |
  |                                      |
  |             SketchScene              |
  |                                      |
  |                                      |
  |                                      |
  '--------------------------------------'
 */

@SuppressWarnings("serial")
public class ColletPanel extends JPanel implements ItemListener {
    MainFrame mainFrame;
    SketchScene sketchScene;
    JComboBox<String> cboCollets;
    Sketch colletSketch;
    ColletPanel(MainFrame mf) throws Exception {
        super();
        this.mainFrame = mf;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        setLayout(gb);
        // --------------------------------------------------
        // Select Collet
        gc.weightx = 1;
        gc.weighty = 0;
        gc.insets = new Insets(3, 3, 3, 3);
        gc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel("Select Collet:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        // 
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        cboCollets = new JComboBox<String>();
        cboCollets.addItem("");
        for (String s : Collet.allColletNames())
            cboCollets.addItem(s);
        cboCollets.addItemListener(this);
        gb.setConstraints(cboCollets, gc);
        add(cboCollets);
        // --------------------------------------------------
        // Sketch Scene
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        sketchScene = new SketchScene(true);
        gb.setConstraints(sketchScene, gc);
        add(sketchScene);
        // 
        cboCollets.setSelectedIndex(1); // random first chuck selection
    }
    public double getAxialLength() {
        if (cboCollets.getSelectedItem().equals(""))
            return 0;
        return colletSketch.getBounds2D().getWidth();
    }
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboCollets) {
            Collet collet = Collet.getCollet((String)e.getItem());
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (collet != null) {
                    switch (collet.getType()) {
                    case NANN_PULLBACK:
                        colletSketch = new NannPBSketch(collet.getSpecs(),
                                                        sketchScene);
                        sketchScene.addSketch(colletSketch);
                        break;
                    }
                    colletSketch.setReadOnly(true);
                    colletSketch.setFillShape(true);
                }
            }
            else {
                sketchScene.removeSketch(colletSketch);
                colletSketch = null;
            }
        }
        // Extra repaint needed here in the case where "" (no collet) is
        // selected.
        sketchScene.repaint();
    }
    public void refreshColletList(String chuckName) {
        boolean reloadCurrent = false;
        String currentName = (String)cboCollets.getSelectedItem();
        cboCollets.setSelectedItem("");
        cboCollets.removeItemListener(this);
        cboCollets.removeAllItems();
        cboCollets.addItem("");
        for (String s : Collet.getChuckCollets(chuckName)) {
            if (s.equals(currentName))
                reloadCurrent = true;
            cboCollets.addItem(s);
        }
        cboCollets.addItemListener(this);
        if (reloadCurrent)
            cboCollets.setSelectedItem(currentName);
        else if (cboCollets.getItemCount() > 1)
            cboCollets.setSelectedIndex(1);
    }
}
