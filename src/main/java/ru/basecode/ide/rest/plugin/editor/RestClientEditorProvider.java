package ru.basecode.ide.rest.plugin.editor;

import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.lang.LanguageUtil;
import com.intellij.openapi.fileEditor.AsyncFileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;
import ru.basecode.ide.rest.plugin.file.RestFileType;

public class RestClientEditorProvider implements AsyncFileEditorProvider, DumbAware {

  private final PsiAwareTextEditorProvider editorProvider;

  public RestClientEditorProvider() {
    editorProvider = new PsiAwareTextEditorProvider();
  }

  @Override
  public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
    final FileType fileType = file.getFileType();

    final boolean isRestType = fileType == RestFileType.INSTANCE || ScratchUtil.isScratch(file)
        && LanguageUtil.getLanguageForPsi(project, file) == RestLanguage.INSTANCE;

    return isRestType && editorProvider.accept(project, file);
  }

  @Override
  public @NotNull FileEditor createEditor(@NotNull Project project,
      @NotNull VirtualFile file) {
    return createEditorAsync(project, file).build();
  }

  @Override
  public @NotNull @NonNls String getEditorTypeId() {
    return RestClientEditor.class.getCanonicalName();
  }

  @Override
  public @NotNull FileEditorPolicy getPolicy() {
    return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
  }

  @Override
  public @NotNull Builder createEditorAsync(@NotNull Project project, @NotNull VirtualFile file) {
    return new Builder() {
      @Override
      public FileEditor build() {
        return new RestClientEditor(project, file, RestClientEditorProvider.this.editorProvider);
      }
    };
  }
}
