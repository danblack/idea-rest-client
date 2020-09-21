package ru.basecode.ide.rest.plugin.editor;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.ActivityTracker;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.JBEmptyBorder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.basecode.ide.rest.plugin.http.RequestExecutor;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;

import static com.intellij.openapi.keymap.KeymapManagerListener.TOPIC;

public class RestClientEditor implements FileEditor {

  private final PsiAwareTextEditorImpl editor;
  private final ActionToolbarImpl toolbar;
  private final JPanel panel;

  public RestClientEditor(@NotNull Project project, @NotNull VirtualFile file, @NotNull TextEditorProvider provider) {
    editor = new PsiAwareTextEditorImpl(project, file, provider);
    toolbar = createToolbarFromGroupId("RestClient.Toolbar.Left");
    panel = new JPanel(new BorderLayout());
    panel.add(toolbar, BorderLayout.NORTH);
    panel.add(editor.getComponent(), BorderLayout.CENTER);
  }

  @Override
  public @NotNull JComponent getComponent() {
    return panel;
  }

  @NotNull
  private static ActionToolbarImpl createToolbarFromGroupId(@SuppressWarnings("SameParameterValue") @NotNull String groupId) {
    final ActionManager actionManager = ActionManager.getInstance();

    if (!actionManager.isGroup(groupId)) {
      throw new IllegalStateException(groupId + " should have been a group");
    }
    final ActionGroup group = ((ActionGroup)actionManager.getAction(groupId));
    final ActionToolbarImpl editorToolbar =
        ((ActionToolbarImpl)actionManager.createActionToolbar(ActionPlaces.EDITOR_TOOLBAR, group, true));
    editorToolbar.setOpaque(false);
    editorToolbar.setBorder(new JBEmptyBorder(0, 2, 0, 2));
//    editorToolbar.setForceMinimumSize(true);

    return editorToolbar;
  }

  @Override
  public @Nullable JComponent getPreferredFocusedComponent() {
    return editor.getPreferredFocusedComponent();
  }

  @Override
  public @NotNull FileEditorState getState(@NotNull FileEditorStateLevel level) {
    return editor.getState(level);
  }

  @Override
  public void setState(@NotNull FileEditorState state) {
    editor.setState(state);
  }

  @Override
  public boolean isModified() {
    return editor.isModified();
  }

  @Override
  public boolean isValid() {
    return editor.isValid();
  }

  @Override
  public @NonNls @NotNull String getName() {
    return editor.getName();
  }

  @Override
  public void selectNotify() {
    editor.selectNotify();
    toolbar.getActions().forEach(this::subscribeToKeyboardShortcuts);
  }

  @Override
  public void deselectNotify() {
    editor.deselectNotify();
    toolbar.getActions().forEach(this::unsubscribeFromKeyboardShortcuts);
  }

  @Override
  public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    editor.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    editor.removePropertyChangeListener(listener);
  }

  @Override
  public @Nullable BackgroundEditorHighlighter getBackgroundHighlighter() {
    return editor.getBackgroundHighlighter();
  }

  @Override
  public @Nullable FileEditorLocation getCurrentLocation() {
    return editor.getCurrentLocation();
  }

  @Override
  public @Nullable StructureViewBuilder getStructureViewBuilder() {
    return editor.getStructureViewBuilder();
  }

  @Override
  public void dispose() {
    toolbar.getActions().forEach(this::unsubscribeFromKeyboardShortcuts);
    Disposer.dispose(editor);
  }

  @Override
  public <T> @Nullable T getUserData(@NotNull Key<T> key) {
    return editor.getUserData(key);
  }

  @Override
  public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
    editor.putUserData(key, value);
  }

  private void subscribeToKeyboardShortcuts(AnAction action) {
    ApplicationManager.getApplication().getMessageBus().connect(this)
        .subscribe(TOPIC, new KeymapManagerListener() {
          @Override
          public void shortcutChanged(@NotNull Keymap keymap, @NotNull String actionId) {
            unsubscribeFromKeyboardShortcuts(action);
            registerShortCuts(action, keymap);
          }

          @Override
          public void keymapAdded(@NotNull Keymap keymap) {
            unsubscribeFromKeyboardShortcuts(action);
            registerShortCuts(action, keymap);
          }

          @Override
          public void keymapRemoved(@NotNull Keymap keymap) {
            unsubscribeFromKeyboardShortcuts(action);
            registerShortCuts(action, keymap);
          }

          @Override
          public void activeKeymapChanged(@Nullable Keymap keymap) {
            unsubscribeFromKeyboardShortcuts(action);
            if (keymap != null) {
              registerShortCuts(action, keymap);
            }
          }
        });
    registerShortCuts(action, KeymapManager.getInstance().getActiveKeymap());
  }

  private void registerShortCuts(@NotNull AnAction action, @NotNull Keymap keymap) {
    action.registerCustomShortcutSet(editor.getComponent(), null);
  }

  private void unsubscribeFromKeyboardShortcuts(@NotNull AnAction action) {
    final RequestExecutor executor = RequestExecutor.getOrDefault(editor.getEditor());
    executor.stop();
    // Ask activity tracker to update all action statuses
    ActivityTracker.getInstance().inc();
    action.unregisterCustomShortcutSet(editor.getComponent());
  }
}
