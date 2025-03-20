/*
  ChuckPanel.java
  S. Edward Dolan
  Saturday, August 26 2023
*/

package edgrind;

import java.util.TreeMap;
//
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
//
import java.awt.geom.Rectangle2D;
// 
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
// 
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
//
import edgrind.sketch.Sketch;
import edgrind.sketch.SketchScene;
import edgrind.sketch.SK50Sketch;
import edgrind.sketch.HPS20Sketch;
//
import edgrind.sketch.*;

/*
  .------------------------------------.
  |     Select Chuck: |SK50 7010|v|    |
  |------------------------------------|
  |                                    |
  |                                    |
  |                                    |
  |             SketchScene            |
  |                                    |
  |                                    |
  |                                    |
  '------------------------------------'
 */

/**
   Select a chuck to use for a given grinding operation.
 */
@SuppressWarnings("serial")
public class ChuckPanel extends JPanel implements ItemListener {
    MainFrame mainFrame;
    SketchScene sketchScene;
    JComboBox<String> cboChucks;
    Sketch chuckSketch;
    ChuckPanel(MainFrame mf) throws Exception {
        super();
        this.mainFrame = mf;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        setLayout(gb);
        gc.weightx = 1;
        gc.weighty = 0;
        gc.insets = new Insets(3, 3, 3, 3);
        gc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel("Select Chuck:");
        gb.setConstraints(lbl, gc);
        add(lbl);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.WEST;
        cboChucks = new JComboBox<String>();
        for (String name : Chuck.allChuckNames())
            cboChucks.addItem(name);
        cboChucks.addItemListener(this);
        gb.setConstraints(cboChucks, gc);
        add(cboChucks);
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 2;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        sketchScene = new SketchScene(true);
        gb.setConstraints(sketchScene, gc);
        add(sketchScene);
        cboChucks.setSelectedIndex(1); // random first chuck selection
    }
    /**
       Return the maximum length of the loaded chuck.
    */
    public double getAxialLength() {
        return chuckSketch.getBounds2D().getWidth();
    }
    /**
       Called when the chuck combo changes.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == cboChucks) {
            Chuck chuck = Chuck.getChuck((String)e.getItem());
            if (e.getStateChange() == ItemEvent.SELECTED) {
                switch (chuck.getType()) {
                case SK:
                    chuckSketch = new SK50Sketch(chuck.getSpecs(),
                                                 sketchScene);
                    sketchScene.addSketch(chuckSketch);
                    break;
                case HP:
                    chuckSketch = new HPS20Sketch(chuck.getSpecs(),
                                                  sketchScene);
                    sketchScene.addSketch(chuckSketch);
                    break;
                }
                chuckSketch.setReadOnly(true);
                chuckSketch.setFillShape(true);
                mainFrame.chuckChanged(chuck.getName());
                mainFrame.simView.chuckChanged(chuckSketch.getModel());
            }
            else {
                sketchScene.removeSketch(chuckSketch);
                chuckSketch = null;
            }
        }
        sketchScene.fitAll();
    }
}
