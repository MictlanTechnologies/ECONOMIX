package org.economix.util;

import javax.swing.*;
import java.awt.*;

public final class IconUtil {
    private static ImageIcon appIcon;
    private static ImageIcon appIconMini;
    private static final String ICON_PATH = "/archivosGraficos/ECONOMIX_LOGO.png";
    private static final String ICON_PATH_MINI = "/archivosGraficos/ECONOMIX_LOGO_MINI.png";

    public static ImageIcon getAppIcon() {
        if (appIcon == null) {
            var url = IconUtil.class.getResource(ICON_PATH);
            if (url != null) {
                ImageIcon base = new ImageIcon(url);
                Image scaled = base.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                appIconMini = new ImageIcon(scaled);
            } else {
                appIconMini = new ImageIcon();
            }
        }
        return appIconMini;
    }

    public static ImageIcon getAppIconMini() {
        if (appIconMini == null) {
            var url = IconUtil.class.getResource(ICON_PATH_MINI);
            if (url != null) {
                appIconMini = new ImageIcon(url);
            } else {
                appIconMini = new ImageIcon();
            }
        }
        return appIconMini;
    }

    public static Image getAppImage() {
        return getAppIcon().getImage();
    }
}