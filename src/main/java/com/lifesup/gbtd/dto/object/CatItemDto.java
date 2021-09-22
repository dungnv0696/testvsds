package com.lifesup.gbtd.dto.object;

import com.lifesup.gbtd.dto.base.BaseDto;
import com.lifesup.gbtd.validator.group.Add;
import com.lifesup.gbtd.validator.group.Update;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CatItemDto extends BaseDto {
    private Long itemId;
    @NotEmpty(message = "ItemCode is required", groups = {Add.class, Update.class})
    private String itemCode;
    @NotEmpty(message = "ItemName is required", groups = {Add.class, Update.class})
    private String itemName;
    @NotEmpty(message = "ItemValue is required", groups = {Add.class, Update.class})
    private String itemValue;
    @NotNull(message = "CategoryId is required", groups = {Add.class, Update.class})
    private Long categoryId;
    @NotEmpty(message = "CategoryCode is required", groups = {Add.class, Update.class})
    private String categoryCode;
    private Long position;
    private String description;
    @NotNull(message = "Editable is required", groups = {Add.class, Update.class})
    private Long editable;
    private Long parentItemId;
    @NotNull(message = "Status is required", groups = {Add.class, Update.class})
    private Long status;
    private Date updateTime;
    private String updateUser;
    private String parentName;

    // dto only
    private List<Long> itemIds;
    private List<Long> categoryIds;
    private List<String> categoryCodes;
    private String parentCode;
    private String[] parentCategoryCodes;
    private String parentValue;

    public CatItemDto(Long itemId, String itemCode, String itemName, String itemValue, Long categoryId,
                      String categoryCode, Long position, String description, Long editable, Long parentItemId,
                      Long status, Date updateTime, String updateUser, String parentName) {
        this(itemId, itemCode, itemName, itemValue, categoryId, categoryCode, position, description,
                editable, parentItemId, status, updateTime, updateUser);
        this.parentName = parentName;
    }

    public CatItemDto(Long itemId, String itemCode, String itemName, String itemValue, Long categoryId,
                      String categoryCode, Long position, String description, Long editable,
                      Long parentItemId, Long status, Date updateTime, String updateUser) {
        this.itemId = itemId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemValue = itemValue;
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.position = position;
        this.description = description;
        this.editable = editable;
        this.parentItemId = parentItemId;
        this.status = status;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
    }

    public CatItemDto(Long itemId, String categoryCode, String itemName) {
        this.itemId = itemId;
        this.categoryCode = categoryCode;
        this.itemName = itemName;
    }

    public CatItemDto(Long itemId) {
        this.itemId = itemId;
    }

    public CatItemDto() {
    }
}
