package com.isy.gui.lang;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LangHandler {
    private String[] langFiles = new String[]{"/en.lang"};
    private String[] langNames = new String[]{"English"};
    private final Map<String, LangFile> langs;
    private String currentLang;

    public LangHandler(){
        this.langs = new HashMap<>();
        this.currentLang = langNames[0];

        for (int i = 0; i < langFiles.length; i++) {
            String name = langNames[i];
            String path = langFiles[i];

            langs.put(name, new LangFile(name, path));
        }
    }

    public String translate(String key, @Nullable Object... params){
        return this.langs.get(currentLang).translate(key, params);
    }

    public void switchLang(String lang){
        this.currentLang = lang;
    }
}
