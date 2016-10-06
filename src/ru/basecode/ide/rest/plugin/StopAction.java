package ru.basecode.ide.rest.plugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * @author danblack
 */
public class StopAction extends AnAction {

    public static final String ID = "rest.action.stop";

    private final RequestExecutor executor;

    @Override
    public void update(AnActionEvent e) {
        if (executor == null) {
            e.getPresentation().setEnabled(false);
        } else {
            super.update(e);
            e.getPresentation().setEnabled(executor.isRunning());
        }
    }

    public StopAction() {
        this(null);
    }

    public StopAction(RequestExecutor executor) {
        super(AllIcons.Actions.Suspend);
        this.executor = executor;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (executor != null && executor.isRunning()) {
            executor.stop();
        }
    }

}
