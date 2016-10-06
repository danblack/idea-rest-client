package ru.basecode.ide.rest.plugin.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ru.basecode.ide.rest.plugin.RestLanguage;

/**
 *
 */
public class RestElementType extends IElementType {
    public RestElementType(@NotNull @NonNls String debugName) {
        super(debugName, RestLanguage.INSTANCE);
    }
}
