package com.lifesup.gbtd.dto.object;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SheetDto {

    private Long code;
    private String name;

    public SheetDto(String name, Long code) {
        this.code = code;
        this.name = name;
    }
}
