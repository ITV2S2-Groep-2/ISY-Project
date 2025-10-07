package com.isy.gui.lang;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class LangHandler {
    private static final LangHandler instance = new LangHandler();
    private final String[] langFiles = new String[]{"/en.lang"};
    private final String[] langNames = new String[]{"English"};
    private final Map<String, LangFile> langs;
    private final Map<JComponent, LangBinding> bindings = new HashMap<>();
    private String currentLang;

    private LangHandler(){
        this.langs = new HashMap<>();
        this.currentLang = langNames[0];

        for (int i = 0; i < langFiles.length; i++) {
            String name = langNames[i];
            String path = langFiles[i];

            langs.put(name, new LangFile(name, path));
        }
    }

    /**
     * Gets the LangHandler instance, should only ever be one
     * @return Current LangHandler instance
     */
    public static LangHandler get(){
        return instance;
    }

    /**
     * Returns a formatted language specific string
     * @param key Language key used to find the right sentence
     * @param params Optional parameters used for formatting
     * @return Formatted string
     */
    public String translate(String key, @Nullable Object... params){
        return this.langs.get(currentLang).translate(key, params);
    }

    /**
     * Bind a JComponent to a language key
     * @param component The component you want to be bound
     * @param key The language key that the component is bound to
     * @param params Optional parameters
     */
    public void bind(JComponent component, String key, @Nullable Object... params){
        this.bindings.put(component, new LangBinding(key, params));
    }

    private void updateComponent(JComponent comp, String key, Object... params) {
        String text = this.translate(key, params);

        if (comp instanceof JLabel label) {
            label.setText(text);
        } else if (comp instanceof AbstractButton button) {
            button.setText(text);
        }
    }

    /**
     * Switch the language to one of the following: {@link LangHandler#langNames}
     * @param lang The language to switch to should be one of the following: {@link LangHandler#langNames}
     */
    public void switchLang(String lang){
        this.currentLang = lang;

        this.bindings.forEach((component, binding) -> {
            updateComponent(component, binding.key, binding.params);
        });
    }
}
