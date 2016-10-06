package ru.basecode.ide.rest.plugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 *
 */
public class RestFileType extends LanguageFileType {

    public final static LanguageFileType INSTANCE = new RestFileType();

    protected RestFileType() {
        super(RestLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Rest file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Rest language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "rest";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AllIcons.General.Web;
    }
}
