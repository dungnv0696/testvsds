package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.CatItemDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CAT_ITEM")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "findCatItemsMapping", classes = {
                @ConstructorResult(targetClass = CatItemDto.class,
                        columns = {
                                @ColumnResult(name = "itemId", type = Long.class),
                                @ColumnResult(name = "itemCode", type = String.class),
                                @ColumnResult(name = "itemName", type = String.class),
                                @ColumnResult(name = "itemValue", type = String.class),
                                @ColumnResult(name = "categoryId", type = Long.class),
                                @ColumnResult(name = "categoryCode", type = String.class),
                                @ColumnResult(name = "position", type = Long.class),
                                @ColumnResult(name = "description", type = String.class),
                                @ColumnResult(name = "editable", type = Long.class),
                                @ColumnResult(name = "parentItemId", type = Long.class),
                                @ColumnResult(name = "status", type = Long.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class),
                                @ColumnResult(name = "parentName", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "getCatItemsMapping", classes = {
                @ConstructorResult(targetClass = CatItemDto.class,
                        columns = {
                                @ColumnResult(name = "itemId", type = Long.class),
                                @ColumnResult(name = "categoryCode", type = String.class),
                                @ColumnResult(name = "itemName", type = String.class)
                        })
        }),
        @SqlResultSetMapping(name = "catItem.getServiceSource", columns = {
                @ColumnResult(name = "itemName", type = String.class)
        }),
        @SqlResultSetMapping(name = "findByCatCodeAndItemCodeAndParentItemId", classes = {
                @ConstructorResult(targetClass = CatItemDto.class,
                        columns = {
                                @ColumnResult(name = "itemId", type = Long.class),
                        })
        })
})
public class CatItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAT_ITEM_SEQ")
    @SequenceGenerator(name = "CAT_ITEM_SEQ", sequenceName = "CAT_ITEM_SEQ", allocationSize = 1)
    @Column(name = "ITEM_ID")
    private Long itemId;

    @Column(name = "ITEM_CODE")
    private String itemCode;
    @Column(name = "ITEM_NAME")
    private String itemName;
    @Column(name = "ITEM_VALUE")
    private String itemValue;
    @Column(name = "CATEGORY_ID")
    private Long categoryId;
    @Column(name = "CATEGORY_CODE")
    private String categoryCode;
    @Column(name = "POSITION")
    private Long position;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "EDITABLE")
    private Long editable;
    @Column(name = "PARENT_ITEM_ID")
    private Long parentItemId;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;
}
