package ru.basecode.ide.rest.plugin.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;

/**
 *
 */
public class RestTokenType extends IElementType {
    public RestTokenType(@NotNull @NonNls String debugName) {
        super(debugName, RestLanguage.INSTANCE);
    }
}
