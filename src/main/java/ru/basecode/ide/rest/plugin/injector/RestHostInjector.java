package ru.basecode.ide.rest.plugin.injector;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.psi.RestEHeader;
import ru.basecode.ide.rest.plugin.psi.RestHeaders;
import ru.basecode.ide.rest.plugin.psi.RestRequest;
import ru.basecode.ide.rest.plugin.psi.RestRequestBody;
import ru.basecode.ide.rest.plugin.psi.RestResponse;
import ru.basecode.ide.rest.plugin.psi.RestResponseBody;
import ru.basecode.ide.rest.plugin.psi.impl.RestWrapperPsiElement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RestHostInjector implements MultiHostInjector {
  @Override
  public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar,
      @NotNull final PsiElement context) {
    RestHeaders headers = null;
    if (context instanceof RestResponseBody) {
      headers = ((RestResponse) context.getParent()).getHeaders();
    } else if (context instanceof RestRequestBody) {
      headers = ((RestRequest) context.getParent()).getHeaders();
    }
    if (headers != null) {
      String contentType = getContentType(headers);
      if (contentType != null) {
        Collection<Language> langList = Language.findInstancesByMimeType(contentType);
        if (!langList.isEmpty()) {
          registrar.startInjecting(langList.iterator().next())
              .addPlace(null, null, (PsiLanguageInjectionHost) context,
                  TextRange.create(0, context.getTextLength())).doneInjecting();
        }
      }
    }
  }

  private String getContentType(@NotNull RestHeaders headers) {
    if (headers.isValid()) {
      for (RestEHeader eHeader : headers.getEHeaderList()) {
        String header = eHeader.getText();
        if (header.startsWith("@Content-Type")) {
          int colonIndex = header.indexOf(":");
          if (colonIndex >= 0) {
            String contentType = header.substring(colonIndex + 1);
            int semicolonIndex = contentType.indexOf(";");
            if (semicolonIndex >= 0) {
              return contentType.substring(0, semicolonIndex).trim();
            }
            return contentType.trim();
          }
        }
      }
    }
    return null;
  }

  @NotNull
  @Override
  public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return Collections.singletonList(RestWrapperPsiElement.class);
  }
}
