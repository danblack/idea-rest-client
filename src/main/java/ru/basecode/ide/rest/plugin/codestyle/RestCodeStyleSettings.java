package ru.basecode.ide.rest.plugin.codestyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

/**
 * @author danblack
 */
public class RestCodeStyleSettings extends CustomCodeStyleSettings {
  protected RestCodeStyleSettings(CodeStyleSettings container) {
    super("RestCodeStyleSettings", container);
  }
}
