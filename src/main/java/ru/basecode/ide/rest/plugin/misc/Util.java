package ru.basecode.ide.rest.plugin.misc;

import com.intellij.lang.Language;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.impl.http.HttpVirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Util {
  public static boolean isSuitable(@NotNull Project project, @NotNull VirtualFile file) {
    if (file instanceof HttpVirtualFile) {
      return false;
    }
    final FileViewProvider provider = PsiManager.getInstance(project).findViewProvider(file);
    return provider != null && RestLanguage.INSTANCE == provider.getBaseLanguage();
  }

  public static String format(final Project project, @NotNull Language language,
      @NotNull String responseBody) {
    return WriteCommandAction.runWriteCommandAction(project, (Computable<String>) () -> {
      final PsiFile psiFile = PsiFileFactoryImpl.getInstance(project).createFileFromText("virtual", language, responseBody);
      CodeStyleManager.getInstance(project).reformatText(psiFile, 0, psiFile.getTextLength());
      return psiFile.getText();
    });
  }
}
