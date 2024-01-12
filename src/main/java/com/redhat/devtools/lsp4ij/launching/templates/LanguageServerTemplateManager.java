/*******************************************************************************
 * Copyright (c) 2024 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.lsp4ij.launching.templates;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.redhat.devtools.lsp4ij.internal.IntelliJPlatformUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Language server template manager.
 */
public class LanguageServerTemplateManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(LanguageServerTemplateManager.class);

    private static final String TEMPLATES_DIR = "templates";

    private final List<LanguageServerTemplate> templates;

    public static LanguageServerTemplateManager getInstance() {
        return ApplicationManager.getApplication().getService(LanguageServerTemplateManager.class);
    }

    private LanguageServerTemplateManager() {
        LanguageServerTemplates root;
        try (Reader templateReader = loadTemplateReader("template-ls.json")) {
            root = new Gson().fromJson(templateReader, LanguageServerTemplates.class);
        } catch (IOException e) {
            LOGGER.warn("Failed to load LS templates:", e);
            root = new LanguageServerTemplates();
        }
        templates = root.getTemplates()
                .stream()
                .filter(t -> IntelliJPlatformUtils.isDevMode() || !t.isDev())
                .toList();
    }

    public List<LanguageServerTemplate> getTemplates() {
        return templates;
    }

    static Reader loadTemplateReader(@NotNull String path) {
        var is = LanguageServerTemplateManager.class.getClassLoader().getResourceAsStream(TEMPLATES_DIR + "/" + path);
        assert is != null;
        return new InputStreamReader(new BufferedInputStream(is));
    }
}
