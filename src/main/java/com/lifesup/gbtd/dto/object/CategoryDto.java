package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;

public class CategoryDto extends BaseDto {

    private Integer categoryId;
    private String categoryName;
    private String categoryCode;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
}
