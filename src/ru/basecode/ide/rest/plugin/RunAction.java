package ru.basecode.ide.rest.plugin;

import com.intellij.icons.AllIcons;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import ru.basecode.ide.rest.plugin.http.Response;
import ru.basecode.ide.rest.plugin.psi.RestESeparator;
import ru.basecode.ide.rest.plugin.psi.RestRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Supplier;

/**
 *
 */
public class RunAction extends AnAction {

    public static final String ID = "rest.action.run";

    private final RequestExecutor executor;

    @Override
    public void update(AnActionEvent e) {
        if (executor == null) {
            e.getPresentation().setEnabled(false);
        } else {
            super.update(e);
            e.getPresentation().setEnabled(executor.isWaiting());
        }
    }

    public RunAction() {
        this(null);
    }

    public RunAction(RequestExecutor executor) {
        super("Execute http request", "", AllIcons.Actions.Execute);
        this.executor = executor;
    }

    private static void writeResponse(Project project, Document doc, Supplier<RestFile> file, String text) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            int responsePosition;
            String separatorString;
            RestFile restFile = file.get();
            RestESeparator separator = restFile.getSeparator();
            if (separator != null && separator.isValid()) {
                responsePosition = separator.getTextOffset();
                separatorString = "";
            } else {
                RestRequest request = restFile.getRequest();
                responsePosition = request.getTextOffset() + request.getTextLength();
                separatorString = "\n";
            }
            String sb = separatorString + "%%%\n" + text;
            doc.replaceString(responsePosition, doc.getTextLength(), sb.replace("\r", ""));
        });
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        if (executor == null || executor.isRunning()) {
            return;
        }
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final VirtualFile file = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE);
        if (!RestProjectComponent.isSuitable(project, file)) {
            return;
        }
        final Document document = e.getRequiredData(CommonDataKeys.EDITOR).getDocument();
        ApplicationManager.getApplication().executeOnPooledThread((Runnable) () -> {
            RestFile restFile = getRestFile(project, document);
            if (restFile == null) {
                return;
            }
            Request request = getRequest(restFile);
            try {
                long startTime = System.currentTimeMillis();
                String start = "# Executing request...\n# URL: " + request.getUrl() + "\n# Start time: " + LocalDateTime.now();
                writeResponse(project, document, () -> restFile, start);
                Response response = executor.execute(request);
                writeResponse(project, document, () -> restFile, "# Reading response...");
                String headers = "\n# " + response.getStatus() + "\n" + getHeaders(response);
                String text = getFormattedResponse(project, response.getContentType(), response.getBody());
                long duration = System.currentTimeMillis() - startTime;
                writeResponse(project, document, () -> restFile, "\n# Duration: " + duration + " ms\n# URL: " + request.getUrl() + "\n" + headers + text);
            } catch (Exception e1) {
                writeResponse(project, document, () -> restFile, "# Error: " + e1.getMessage());
            }
        });
    }

    private String getHeaders(Response response) {
        StringBuilder sb = new StringBuilder("\n");
        for (String header : response.getHeaders()) {
            sb.append("@").append(header).append("\n");
        }
        return sb.append("\n").toString();
    }

    private String getFormattedResponse(Project project, String contentType, String text) {
        Collection<Language> langList = Language.findInstancesByMimeType(contentType);
        if (!langList.isEmpty()) {
            return format(project, langList.iterator().next(), text);
        }
        return text;
    }

    private Request getRequest(RestFile restFile) {
        return ApplicationManager.getApplication().runReadAction(
                (Computable<Request>) () -> RequestParser.parse(restFile.getRequest()));
    }

    private static RestFile getRestFile(Project project, Document document) {
        return ApplicationManager.getApplication().runReadAction((Computable<RestFile>) () -> {
            if (project.isOpen()) {
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                if (psiFile != null && psiFile.isValid() && psiFile instanceof RestFile) {
                    return (RestFile) psiFile;
                }
            }
            return null;
        });
    }

    public static String format(final Project project, Language language, String text) {
        return WriteCommandAction.runWriteCommandAction(project, (Computable<String>) () -> {
            long startTime = System.currentTimeMillis();
            final PsiFile psiFile;
            try {
                psiFile = PsiFileFactoryImpl.getInstance(project).createFileFromText("virtual", language, text);
                CodeStyleManager.getInstance(project).reformatText(psiFile, 0, psiFile.getTextLength());
            } finally {
//                System.out.println(System.currentTimeMillis() - startTime);
            }

            return psiFile.getText();
        });
    }
}
