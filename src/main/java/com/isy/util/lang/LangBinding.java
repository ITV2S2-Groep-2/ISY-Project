package com.isy.util.lang;

public class LangBinding {
    final String key;
    final Object[] params;

    LangBinding(String key, Object[] params) {
        this.key = key;
        this.params = params;
    }
}
