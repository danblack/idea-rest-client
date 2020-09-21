package ru.basecode.ide.rest.plugin.action;

import com.intellij.icons.AllIcons;
import com.intellij.ide.ActivityTracker;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.basecode.ide.rest.plugin.file.RestPsiFile;
import ru.basecode.ide.rest.plugin.http.HttpRequest;
import ru.basecode.ide.rest.plugin.http.HttpResponse;
import ru.basecode.ide.rest.plugin.http.RequestExecutor;
import ru.basecode.ide.rest.plugin.psi.RestESeparator;
import ru.basecode.ide.rest.plugin.psi.RestRequest;
import ru.basecode.ide.rest.plugin.psi.RestRequestParser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Supplier;

import static com.intellij.openapi.keymap.KeymapManagerListener.TOPIC;
import static ru.basecode.ide.rest.plugin.misc.Util.format;
import static ru.basecode.ide.rest.plugin.misc.Util.isSuitable;

public class SendAction extends AnAction {
  private static final Logger log = Logger.getInstance(SendAction.class);

  public SendAction() {
    super(AllIcons.Actions.Execute);
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    final RequestExecutor executor = RequestExecutor.getOrDefault(e);
    if (executor == null) {
      e.getPresentation().setEnabled(false);
    } else {
      super.update(e);
      e.getPresentation().setEnabled(executor.isWaiting());
    }
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final RequestExecutor executor = RequestExecutor.getOrDefault(e);
    if (executor == null || executor.isRunning()) {
      return;
    }
    final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
    final VirtualFile file = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
    if (!isSuitable(project, file)) {
      return;
    }
    final Document document = e.getRequiredData(CommonDataKeys.EDITOR).getDocument();
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      RestPsiFile restPsiFile = getRestFile(project, document);
      HttpRequest request = getRequest(restPsiFile);
      if (restPsiFile == null || request == null) {
        return;
      }
      try {
        long startTime = System.currentTimeMillis();
        String start = "# Executing request...\n# URL: " + request.getUrl() + "\n# Start time: "
            + LocalDateTime.now();
        writeResponse(project, document, () -> restPsiFile, start);
        HttpResponse response = executor.execute(request);
        writeResponse(project, document, () -> restPsiFile, "# Reading response...");
        String headers = "\n# " + response.getStatus() + "\n" + getHeaders(response);
        long duration = System.currentTimeMillis() - startTime;
        final StringBuilder psiFileContent = new StringBuilder();
        psiFileContent.append("\n# Duration: ").append(duration).append(" ms\n# URL: ").append(request.getUrl()).append("\n").append(headers);
        if(response.getBody() != null) {
          String formattedResponseBody = getFormattedResponse(project, response.getContentType(), response.getBody());
          psiFileContent.append(formattedResponseBody);
        }
        writeResponse(project, document, () -> restPsiFile, psiFileContent.toString());
      } catch (Exception ex) {
        writeResponse(project, document, () -> restPsiFile, "# Error: " + ex.getMessage());
      } finally {
        // Ask activity tracker to update all action statuses
        ActivityTracker.getInstance().inc();
      }
    });
  }

  private String getHeaders(HttpResponse response) {
    StringBuilder sb = new StringBuilder("\n");
    for (String header : response.getHeaders()) {
      sb.append("@").append(header).append("\n");
    }
    return sb.append("\n").toString();
  }

  private String getFormattedResponse(Project project, @NotNull String contentType, @NotNull String responseBody) {
    Collection<Language> langList = Language.findInstancesByMimeType(contentType);
    if (!langList.isEmpty()) {
      return format(project, langList.iterator().next(), responseBody);
    }
    return responseBody;
  }

  @Nullable
  private HttpRequest getRequest(RestPsiFile restPsiFile) {
    return ApplicationManager.getApplication().runReadAction((Computable<HttpRequest>) () -> {
      if (restPsiFile.getRequest() == null) {
        return null;
      }
      return RestRequestParser.parse(restPsiFile.getRequest());
    });
  }

  private void writeResponse(Project project, Document doc, Supplier<RestPsiFile> file,
      String text) {
    WriteCommandAction.runWriteCommandAction(project, () -> {
      int responsePosition;
      String separatorString;
      RestPsiFile restPsiFile = file.get();
      RestESeparator separator = restPsiFile.getSeparator();
      if (separator != null && separator.isValid()) {
        responsePosition = separator.getTextOffset();
        separatorString = "";
      } else {
        RestRequest request = restPsiFile.getRequest();
        responsePosition = request.getTextOffset() + request.getTextLength();
        separatorString = "\n";
      }
      String sb = separatorString + "%%%\n" + text;
      doc.replaceString(responsePosition, doc.getTextLength(), sb.replace("\r", ""));
    });
  }

  private RestPsiFile getRestFile(Project project, Document document) {
    return ApplicationManager.getApplication().runReadAction((Computable<RestPsiFile>) () -> {
      if (project.isOpen()) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile != null && psiFile.isValid() && psiFile instanceof RestPsiFile) {
          return (RestPsiFile) psiFile;
        }
      }
      return null;
    });
  }

}
