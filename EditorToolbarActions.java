package processing.app;

import static processing.app.I18n.tr;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import cc.arduino.components.EditorIcon.State;
import cc.arduino.components.IconArrowDown;
import cc.arduino.components.IconArrowRight;
import cc.arduino.components.IconArrowUp;
import cc.arduino.components.IconCheck;
import cc.arduino.components.IconDoc;
import cc.arduino.components.IconSerial;
import cc.arduino.components.IconStop;
import processing.app.EditorToolbar.EditorToolbarAction;

public class EditorToolbarActions {
  private EditorToolbarActions() {}

  /*private*/ static final Dimension BUTTON_SIZE = new Dimension(24, 24);

  public static final int RUN = 0;
  public static final int STOP = 1;
  public static final int EXPORT = 2;

  public static final int NEW = 3;
  public static final int OPEN = 4;
  public static final int SAVE = 5;

  public static final int SERIAL = 6;

  private static class Run extends IconCheck {
    final State s;
    public Run(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class Stop extends IconStop {
    final State s;
    public Stop(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class Export extends IconArrowRight {
    final State s;
    public Export(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class New extends IconDoc {
    final State s;
    public New(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class Open extends IconArrowUp {
    final State s;
    public Open(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class Save extends IconArrowDown {
    final State s;
    public Save(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  private static class Console extends IconSerial {
    final State s;
    public Console(State s) { this.s = s; }
    @Override
    public State state() { return s; }
  }
  
  // ---------------------------------------------------------------------------------
  // EditorToolbarContribution.GRP_EXECUTION

  public static class EditorToolbarVerifyAction extends EditorToolbarAction {

    public EditorToolbarVerifyAction() {
      super(tr("Verify"), tr("Verify"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_EXECUTION);
      putValue(ICON_INACTIVE, new Run(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Run(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Run(State.INACTIVE).size(BUTTON_SIZE));
    }

    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {
      
      editor.handleRun(false, editor.presentHandler, editor.runHandler);
      
    }

  }
  
  public static class EditorToolbarStopAction extends EditorToolbarAction {
    
    public EditorToolbarStopAction() {
      super(tr("Stop"), tr("Stop"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_EXECUTION);
      putValue(ICON_INACTIVE, new Stop(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Stop(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Stop(State.INACTIVE).size(BUTTON_SIZE));
    }
    
    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {

      editor.handleStopJob();
      
    }
    
  }
  
  public static class EditorToolbarUploadAction extends EditorToolbarAction {

    public EditorToolbarUploadAction() {
      super(tr("Upload"), tr("Upload Using Programmer"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_EXECUTION);
      putValue(ICON_INACTIVE, new Export(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Export(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Export(State.INACTIVE).size(BUTTON_SIZE));
    }

    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {
      
      editor.handleExport(isShiftDown(e));
      
    }
    
   }

  // ---------------------------------------------------------------------------------
  // EditorToolbarContribution.GRP_DOC
  
  public static class EditorToolbarNewAction extends EditorToolbarAction {

    public EditorToolbarNewAction() {
      super(tr("New"), tr("New"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_DOC);
      putValue(ICON_INACTIVE, new New(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new New(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new New(State.INACTIVE).size(BUTTON_SIZE));
    }

    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {

      editor.base.handleNew();
      
    }
    
  }

  public static class EditorToolbarOpenAction extends EditorToolbarAction {
    
    public EditorToolbarOpenAction() {
      super(tr("Open"), tr("Open"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_DOC);
      putValue(ICON_INACTIVE, new Open(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Open(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Open(State.INACTIVE).size(BUTTON_SIZE));
    }
    
    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {
      // LJPM restore 'Open'
//    popup = menu.getPopupMenu();
//    popup.show(EditorToolbar.this, x, y);
    }
    
  }
  
  public static class EditorToolbarSaveAction extends EditorToolbarAction {
    
    public EditorToolbarSaveAction() {
      super(tr("Save"), tr("Save As..."));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_DOC);
      putValue(ICON_INACTIVE, new Save(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Save(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Save(State.INACTIVE).size(BUTTON_SIZE));
    }
    
    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {

      if (isShiftDown(e)) {
        editor.handleSaveAs();
      } else {
        editor.handleSave(false);
      }

    }
    
  }
  
  // ---------------------------------------------------------------------------------
  // EditorToolbarContribution.GRP_RUNTIME
  
  public static class EditorToolbarSerialMonitorAction extends EditorToolbarAction {
    
    public EditorToolbarSerialMonitorAction() {
      super(tr("Serial Monitor"), tr("Serial Monitor"));
      putValue(ACTION_GROUP, EditorToolbarContribution.GRP_RUNTIME);
      putValue(ICON_INACTIVE, new Console(State.ACTIVE).size(BUTTON_SIZE));
      putValue(ICON_ROLLOVER, new Console(State.ROLLOVER).size(BUTTON_SIZE));
      putValue(ICON_ACTIVE, new Console(State.INACTIVE).size(BUTTON_SIZE));
    }
    
    @Override
    public void execute(ActionEvent e, Editor editor) throws Exception {
      
      editor.handleSerial();
      
    }
    
  }
  
}