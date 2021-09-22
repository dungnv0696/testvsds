package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnProfile {
    private int index;
    private int width;
    private int height;
    private String title;
    private String alias;

    public ColumnProfile(int width, String title) {
        this.width = width;
        this.title = title;
    }

    public ColumnProfile(int width, String title, String alias) {
        this.width = width;
        this.title = title;
        this.alias = alias;
    }
}
