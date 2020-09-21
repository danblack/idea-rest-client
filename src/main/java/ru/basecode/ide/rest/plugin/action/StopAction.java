package ru.basecode.ide.rest.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ActivityTracker;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.http.RequestExecutor;

/**
 * @author danblack
 */
public class StopAction extends AnAction {
  private static final Logger log = Logger.getInstance(StopAction.class);

  public StopAction() {
    super(AllIcons.Actions.Suspend);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
//    log.error(e);
    final RequestExecutor executor = RequestExecutor.getOrDefault(e);
    if (executor == null) {
      e.getPresentation().setEnabled(false);
    } else {
      super.update(e);
      e.getPresentation().setEnabled(executor.isRunning());
    }
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
//    log.error(e);
    final RequestExecutor executor = RequestExecutor.getOrDefault(e);
    if (executor != null && executor.isRunning()) {
      executor.stop();
      // Ask activity tracker to update all action statuses
      ActivityTracker.getInstance().inc();
    }
  }

}
