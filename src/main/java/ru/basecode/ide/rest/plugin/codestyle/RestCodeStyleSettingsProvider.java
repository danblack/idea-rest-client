package ru.basecode.ide.rest.plugin.codestyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.basecode.ide.rest.plugin.RestLanguage;

/**
 * @author danblack
 */
public class RestCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @Override
  public @NotNull CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings,
      @NotNull CodeStyleSettings modelSettings) {
    return new CodeStyleAbstractConfigurable(settings, modelSettings,
        RestLanguage.INSTANCE.getDisplayName()) {

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }

      @Override
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new RestTabbedLanguageCodeStylePanel(getCurrentSettings(), settings);
      }
    };
  }

  @Nullable
  @Override
  public String getConfigurableDisplayName() {
    return RestLanguage.INSTANCE.getDisplayName();
  }

  @Nullable
  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new RestCodeStyleSettings(settings);
  }

  private static class RestTabbedLanguageCodeStylePanel extends TabbedLanguageCodeStylePanel {
    protected RestTabbedLanguageCodeStylePanel(CodeStyleSettings currentSettings,
        CodeStyleSettings settings) {
      super(RestLanguage.INSTANCE, currentSettings, settings);
    }
  }
}
