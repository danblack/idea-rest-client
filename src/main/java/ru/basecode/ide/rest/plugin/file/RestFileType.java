package ru.basecode.ide.rest.plugin.file;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.basecode.ide.rest.plugin.RestLanguage;

import javax.swing.*;

import static ru.basecode.ide.rest.plugin.misc.Icons.REST_FILE_TYPE;

public class RestFileType extends LanguageFileType {
  public static final String REST_FILE_TYPE_TITLE = "Rest file";

  public final static LanguageFileType INSTANCE = new RestFileType();

  protected RestFileType() {
    super(RestLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return REST_FILE_TYPE_TITLE;
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Rest file";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "rest";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return REST_FILE_TYPE;
  }
}
