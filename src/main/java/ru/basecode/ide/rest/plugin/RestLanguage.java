package ru.basecode.ide.rest.plugin;

import com.intellij.lang.Language;

public class RestLanguage extends Language {
  public static final Language INSTANCE = new RestLanguage();

  public RestLanguage() {
    super("Rest");
  }
}
