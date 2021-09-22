package com.lifesup.gbtd.model;

import com.lifesup.gbtd.dto.object.UsersDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@SqlResultSetMappings(value = {
        @SqlResultSetMapping(name = "userDto", classes = {
                @ConstructorResult(targetClass = UsersDto.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "username", type = String.class),
                                @ColumnResult(name = "name", type = String.class),
                                @ColumnResult(name = "firstName", type = String.class),
                                @ColumnResult(name = "lastName", type = String.class),
                                @ColumnResult(name = "phone", type = String.class),
                                @ColumnResult(name = "email", type = String.class),
                                @ColumnResult(name = "staffCode", type = String.class),
                                @ColumnResult(name = "positionCode", type = String.class),
                                @ColumnResult(name = "positionName", type = String.class),
                                @ColumnResult(name = "imageUrl", type = String.class),
                                @ColumnResult(name = "deptId", type = Long.class),
                                @ColumnResult(name = "roleCode", type = String.class),
                                @ColumnResult(name = "roleName", type = String.class),
                                @ColumnResult(name = "status", type = Integer.class),
                                @ColumnResult(name = "createUser", type = String.class),
                                @ColumnResult(name = "createTime", type = Date.class),
                                @ColumnResult(name = "updateTime", type = Date.class),
                                @ColumnResult(name = "updateUser", type = String.class)
                        })
        }),

})
public class UsersEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USERS_SEQ")
    @SequenceGenerator(name = "USERS_SEQ", sequenceName = "USERS_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "PHONE")
    private String phone;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "STAFF_CODE")
    private String staffCode;
    @Column(name = "POSITION_CODE")
    private String positionCode;
    @Column(name = "POSITION_NAME")
    private String positionName;
    @Column(name = "IMAGE_URL")
    private String imageUrl;
    @Column(name = "DEPT_ID")
    private Long deptId;
    @Column(name = "ROLE_CODE")
    private String roleCode;
    @Column(name = "ROLE_NAME")
    private String roleName;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "UPDATE_TIME")
    private Date updateTime;
    @Column(name = "UPDATE_USER")
    private String updateUser;

//    @ManyToOne(fetch = FetchType.LAZY) // @ManyToOne is also possible
//    @JoinColumn(name = "DEPT_ID", referencedColumnName = "id", insertable = false, updatable = false)
//    private CatDepartmentEntity CatDepartment;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return "*****";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
