package ru.basecode.ide.rest.plugin;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.psi.RestESeparator;
import ru.basecode.ide.rest.plugin.psi.RestRequest;

/**
 *
 */
public class RestFile extends PsiFileBase {

    protected RestFile(@NotNull FileViewProvider viewProvider) {
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
