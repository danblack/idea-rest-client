package ru.basecode.ide.rest.plugin.misc;

import com.intellij.openapi.util.IconLoader;
import lombok.NoArgsConstructor;

import javax.swing.*;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Icons {
  public static Icon REST_FILE_TYPE = IconLoader.getIcon("/META-INF/restFileType.svg");
}
