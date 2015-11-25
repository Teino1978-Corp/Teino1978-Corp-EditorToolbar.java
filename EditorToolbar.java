package processing.app;

import static processing.app.EditorToolbar.EditorToolbarAction.ACTIVE;
import static processing.app.EditorToolbar.EditorToolbarAction.INACTIVE;
import static processing.app.EditorToolbar.EditorToolbarAction.ROLLOVER;
import static processing.app.EditorToolbarActions.EXPORT;
import static processing.app.EditorToolbarActions.NEW;
import static processing.app.EditorToolbarActions.OPEN;
import static processing.app.EditorToolbarActions.RUN;
import static processing.app.EditorToolbarActions.SAVE;
import static processing.app.EditorToolbarActions.SERIAL;
import static processing.app.EditorToolbarActions.STOP;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

import cc.arduino.components.EditorIcon.State;
import cc.arduino.components.JUserContribution.JUserContributionGroupId;


/**
 * run/stop/etc buttons for the ide
 */
public class EditorToolbar extends JComponent implements MouseInputListener, KeyListener { //, JUserContributable {

  public static abstract class EditorToolbarAction extends AbstractAction implements EditorAction {
    public static final int INACTIVE = 0;
    public static final int ROLLOVER = 1;
    public static final int ACTIVE = 2;
    
    public static final String ALT_NAME = "AltName";
    public static final String ACTION_GROUP = "Group";
    
    public static final String ICON_INACTIVE = "act:"+ INACTIVE;
    public static final String ICON_ROLLOVER = "act:"+ ROLLOVER;
    public static final String ICON_ACTIVE = "act:"+ ACTIVE;

    public EditorToolbarAction(String title, String altTitle) {
      putValue(NAME, title);
      putValue(ALT_NAME, altTitle);
    }
    public Icon icon(int state) {
      return (Icon)getValue("act:"+state);
    }
    public String altName() {
      return (String)getValue(ALT_NAME);
    }
    public String name() {
      return (String)getValue(NAME);
    }
    protected boolean isShiftDown(ActionEvent e) {
      return (e.getModifiers() & InputEvent.SHIFT_MASK) != 0;
    }
  }

//  private EditorToolbarContributionsManager contributions;
  
  /**
   * Width of each toolbar button.
   */
  private static final int BUTTON_WIDTH = 27;
  /**
   * Height of each toolbar button.
   */
  private static final int BUTTON_HEIGHT = 32;
  /**
   * The amount of space between groups of buttons on the toolbar.
   */
  private static final int BUTTON_GAP = 5;
//  /**
//   * Size of the button image being chopped up.
//   */
//  private static final int BUTTON_IMAGE_SIZE = 33;

  private static final int BUTTON_MARGIN = 3;
  
  
  private final Editor editor;

    
  private Image offscreen;
  private int width;
  private int height;

  private int currentRollover;

  private JPopupMenu popup;
//  private final JMenu menu;

  private int buttonCount;
  private int[] state;
  private Icon[] stateIcon;
  private final int[] which; // mapping indices to implementation
  
  private final EditorToolbarAction[] actions;
  private final Rectangle[] buttons;

  private final Font statusFont;
  private final Color statusColor;

  private boolean shiftPressed;

  public EditorToolbar(Editor editor, JMenu menu, EditorToolbarAction[] actions) {
    this.editor = editor;
//    this.menu = menu;

    buttonCount = 0;
    which = new int[actions.length];

    //which[buttonCount++] = NOTHING;
    which[buttonCount++] = RUN;
    which[buttonCount++] = STOP;
    which[buttonCount++] = EXPORT;
    
    which[buttonCount++] = NEW;
    which[buttonCount++] = OPEN;
    which[buttonCount++] = SAVE;
    
    which[buttonCount++] = SERIAL;
    
    this.buttons = new Rectangle[actions.length];
    this.actions = actions;

    currentRollover = -1;

    statusFont = Theme.getFont("buttons.status.font");
    statusColor = Theme.getColor("buttons.status.color");

    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void computeButtonsLayout(int w, int h) {
    
    int offsetX = 3;
    JUserContributionGroupId grp = (JUserContributionGroupId) actions[0].getValue(EditorToolbarAction.ACTION_GROUP);
    int i = 0;
    for (; i < buttonCount; i++) {
      int x = offsetX;
      if (grp != actions[i].getValue(EditorToolbarAction.ACTION_GROUP)) x += BUTTON_GAP;
      Rectangle rc = new Rectangle(x, BUTTON_MARGIN, BUTTON_WIDTH, h-BUTTON_MARGIN);
      offsetX = x+BUTTON_WIDTH;
      buttons[i] = rc; 
    }

    // Serial button must be on the right
    buttons[SERIAL] = new Rectangle(w - BUTTON_WIDTH - 14, BUTTON_MARGIN, BUTTON_WIDTH, h-BUTTON_MARGIN);
  }

  @Override
  public void paintComponent(Graphics screen) {
//    // this data is shared by all EditorToolbar instances
//    if (buttonImages == null) {
//      loadButtons();
//    }

    // this happens once per instance of EditorToolbar
    if (stateIcon /*stateImage*/ == null) {
      state = new int[buttonCount];
      stateIcon = new Icon[buttonCount];
      for (int i = 0; i < buttonCount; i++) {
        setState(i, INACTIVE, false);
      }
    }

    Dimension size = getSize();    
    if ((offscreen == null) 
        || (size.width != width) 
        || (size.height != height)) {
      
      computeButtonsLayout(size.width, size.height);
      offscreen = createImage(size.width, size.height);
      width = size.width;
      height = size.height;
    }
    Graphics2D g2 = (Graphics2D)offscreen.getGraphics();
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(getBackground());
    g2.fillRect(0, 0, size.width, size.height);

    for (int i = 0; i < buttonCount; i++) {
      stateIcon[i].paintIcon(this, g2, buttons[i].x, buttons[i].y);            
    }

    g2.setColor(statusColor);
    g2.setFont(statusFont);

    /*
    // if i ever find the guy who wrote the java2d api, i will hurt him.
     * 
     * whereas I love the Java2D API. --jdf. lol.
     * 
    Graphics2D g2 = (Graphics2D) g;
    FontRenderContext frc = g2.getFontRenderContext();
    float statusW = (float) statusFont.getStringBounds(status, frc).getWidth();
    float statusX = (getSize().width - statusW) / 2;
    g2.drawString(status, statusX, statusY);
    */
    if (currentRollover != -1) {
      int statusY = (BUTTON_HEIGHT + g2.getFontMetrics().getAscent()) / 2;
      String status = shiftPressed ? actions[currentRollover].altName() : actions[currentRollover].name();
      if (currentRollover != SERIAL)
        g2.drawString(status, (buttonCount - 1) * BUTTON_WIDTH + 3 * BUTTON_GAP, statusY);
      else {
        int statusX = buttons[SERIAL].x - BUTTON_GAP;
        statusX -= g2.getFontMetrics().stringWidth(status);
        g2.drawString(status, statusX, statusY);
      }
    }
    g2.dispose();
    
    screen.drawImage(offscreen, 0, 0, null);

    if (!isEnabled()) {
      screen.setColor(new Color(0, 0, 0, 100));
      screen.fillRect(0, 0, getWidth(), getHeight());
    }
  }


  public void mouseMoved(MouseEvent e) {
    if (!isEnabled())
      return;

    // mouse events before paint();
    if (state == null) return;

    if (state[OPEN] != INACTIVE) {
      // avoid flicker, since there will probably be an update event
      setState(OPEN, INACTIVE, false);
    }
    handleMouse(e);
  }


  public void mouseDragged(MouseEvent e) {
  }


  private void handleMouse(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    if (currentRollover != -1) {
      if (buttons[currentRollover].contains(x, y)) {
        return;

      } else {
        setState(currentRollover, INACTIVE, true);
        currentRollover = -1;
      }
    }
    int sel = findSelection(x, y);
    if (sel == -1) return;

    if (state[sel] != ACTIVE) {
      setState(sel, ROLLOVER, true);
      currentRollover = sel;
    }
  }


  protected EditorToolbarAction getAction(int x, int y) {
    for(int i = 0; i < buttons.length; i++) {
      if (buttons[i].contains(x, y)) {
        return actions[i];
      }
    }
    return null;
  }

  private int findSelection(int x, int y) {
    // if app loads slowly and cursor is near the buttons
    // when it comes up, the app may not have time to load
//    if ((x1 == null) || (x2 == null)) return -1;

    for (int i = 0; i < buttonCount; i++) {
      if (buttons[i].contains(x, y)) {
        return i;
      }
    }
    return -1;
  }


  /*private*/ State getState(int slot) {
    int val = state[slot];
    for(State s : State.values()) {
      if (s.state() == val) return s;
    }
    return null;
  }
  
  /*private*/ void setState(int slot, int newState, boolean updateAfter) {
    state[slot] = newState;
//    stateImage[slot] = buttonImages[which[slot]][newState];
//    stateIcon[slot] = icons[which[slot]][newState];
    stateIcon[slot] = actions[which[slot]].icon(newState);
    if (updateAfter) {
      repaint();
    }
  }


  public void mouseEntered(MouseEvent e) {
    handleMouse(e);
  }


  public void mouseExited(MouseEvent e) {
    // if the popup menu for is visible, don't register this,
    // because the popup being set visible will fire a mouseExited() event
    if ((popup != null) && popup.isVisible()) return;

    if (state[OPEN] != INACTIVE) {
      setState(OPEN, INACTIVE, true);
    }
    handleMouse(e);
  }


  public void mousePressed(MouseEvent e) {

    // jdf
    if (!isEnabled())
      return;

    final int x = e.getX();
    final int y = e.getY();

    int sel = findSelection(x, y);
    if (sel == -1) return;
    currentRollover = -1;
    
    EditorToolbarAction action = getAction(x, y);
    ActionEvent evt = new ActionEvent(this, state[sel], "", e.getModifiers());
    action.actionPerformed(evt);

  }


  public void mouseClicked(MouseEvent e) {
  }


  public void mouseReleased(MouseEvent e) {
  }


  /**
   * Set a particular button to be active.
   */
  private void activate(int what) {
    if (stateIcon /*buttonImages*/ != null) {
      setState(what, ACTIVE, true);
    }
  }

  public void activateRun() {
    activate(RUN);
  }

  public void activateSave() {
    activate(SAVE);
  }

  public void activateExport() {
    activate(EXPORT);
  }

  /**
   * Set a particular button to be active.
   */
  private void deactivate(int what) {
    if (stateIcon/*buttonImages*/ != null) {
      setState(what, INACTIVE, true);
    }
  }

  public void deactivateRun() {
    deactivate(RUN);
  }

  public void deactivateSave() {
    deactivate(SAVE);
  }

  public void deactivateExport() {
    deactivate(EXPORT);
  }

  public Dimension getPreferredSize() {
    return getMinimumSize();
  }


  public Dimension getMinimumSize() {
    return new Dimension(200, /*(BUTTON_COUNT + 1) * BUTTON_WIDTH,*/ BUTTON_HEIGHT);
  }


  public Dimension getMaximumSize() {
    return new Dimension(3000, BUTTON_HEIGHT);
  }


  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
      shiftPressed = true;
      repaint();
    }
  }


  public void keyReleased(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
      shiftPressed = false;
      repaint();
    }
  }


  public void keyTyped(KeyEvent e) {
  }

  
//  private List<ContributionGroup> groups = new ArrayList<>();
//  @Override
//  public List<ContributionGroup> groups() {
//    return groups;
//  }
//
//  @Override
//  public void refresh(String id) {
//    // TODO Auto-generated method stub
//    
//  }

}