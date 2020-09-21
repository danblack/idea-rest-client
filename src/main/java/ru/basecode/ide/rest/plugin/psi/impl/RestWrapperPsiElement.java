package ru.basecode.ide.rest.plugin.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class RestWrapperPsiElement extends ASTWrapperPsiElement
    implements PsiLanguageInjectionHost {

  public RestWrapperPsiElement(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public boolean isValidHost() {
    return true;
  }

  @Override
  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    return this;
  }

  @NotNull
  @Override
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new ResponseLiteralEscaper(this);
  }
}
