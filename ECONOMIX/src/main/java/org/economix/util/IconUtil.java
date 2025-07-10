package org.economix.util;

import javax.swing.*;
import java.awt.*;

public final class IconUtil {
    private static ImageIcon appIcon;
    private static final String ICON_PATH = "/archivosGraficos/ECONOMIX_LOGO.png";

    public static ImageIcon getAppIcon() {
        if (appIcon == null) {
            var url = IconUtil.class.getResource(ICON_PATH);
            if (url != null) {
                appIcon = new ImageIcon(url);
            } else {
                appIcon = new ImageIcon();
            }
        }
        return appIcon;
    }

    public static Image getAppImage() {
        return getAppIcon().getImage();
    }
}