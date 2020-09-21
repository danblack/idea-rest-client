package ru.basecode.ide.rest.plugin.grammar;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;
import ru.basecode.ide.rest.plugin.file.RestPsiFile;
import ru.basecode.ide.rest.plugin.parser._RestParser;
import ru.basecode.ide.rest.plugin.psi.RestTypes;

public class RestParserDefinition implements ParserDefinition {

  public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

  public static final IFileElementType FILE =
      new IFileElementType(Language.findInstance(RestLanguage.class));

  @NotNull
  @Override
  public Lexer createLexer(Project project) {
    return new RestLexerAdapter();
  }

  @Override
  public PsiParser createParser(Project project) {
    return new _RestParser();
  }

  @Override
  public IFileElementType getFileNodeType() {
    return FILE;
  }

  @NotNull
  @Override
  public TokenSet getWhitespaceTokens() {
    return WHITE_SPACES;
  }

  @NotNull
  @Override
  public TokenSet getCommentTokens() {
    return TokenSet.EMPTY;
  }

  @NotNull
  @Override
  public TokenSet getStringLiteralElements() {
    return TokenSet.EMPTY;
  }

  @NotNull
  @Override
  public PsiElement createElement(ASTNode node) {
    return RestTypes.Factory.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider) {
    return new RestPsiFile(viewProvider);
  }

}
