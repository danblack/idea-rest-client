package ru.basecode.ide.rest.plugin.grammar;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import ru.basecode.ide.rest.plugin.psi.RestTypes;

public class RestLexerAdapter extends MergingLexerAdapter {
  public RestLexerAdapter() {
    super(new FlexAdapter(new _RestLexer(null)), TokenSet.create(RestTypes.BAD_CHARACTER));
  }
}
