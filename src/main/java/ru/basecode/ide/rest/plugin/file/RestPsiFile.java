package ru.basecode.ide.rest.plugin.file;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;
import ru.basecode.ide.rest.plugin.psi.RestESeparator;
import ru.basecode.ide.rest.plugin.psi.RestRequest;

public class RestPsiFile extends PsiFileBase {

  public RestPsiFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, RestLanguage.INSTANCE);
  }

  public RestRequest getRequest() {
    for (PsiElement element : getChildren()) {
      if (element instanceof RestRequest) {
        return (RestRequest) element;
      }
    }
    return null;
  }

  public RestESeparator getSeparator() {
    for (PsiElement element : getChildren()) {
      if (element instanceof RestESeparator) {
        return (RestESeparator) element;
      }
    }
    return null;
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return RestFileType.INSTANCE;
  }
}
