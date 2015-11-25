package processing.app;

import cc.arduino.components.JUserContribution;

public interface EditorToolbarContribution extends JUserContribution {

  public static final JUserContributionGroupId GRP_EXECUTION = new JUserContributionGroupId("GRP_EXECUTION", 1000);
  public static final JUserContributionGroupId GRP_DOC = new JUserContributionGroupId("GRP_DOC", 2000);
  public static final JUserContributionGroupId GRP_RUNTIME = new JUserContributionGroupId("GRP_RUNTIME", 3000);
  public static final JUserContributionGroupId GRP_SKETCH = new JUserContributionGroupId("GRP_SKETCH", 4000);

}