package com.demo.java_jwt.model;

import com.demo.java_jwt.util.JwtRequestFilter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "user")
@SQLDelete(sql = "UPDATE user SET is_active = false, deleted_flag = true WHERE id=?")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deleted_flag")
    private Boolean deletedFlag;

    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_at")
    @UpdateTimestamp
    private Timestamp modifiedAt;

    @Column(name = "modified_by")
    private String modifiedBy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserRole> userRoleList;

    @PrePersist
    @PreUpdate
    void setAuditDetails() {
        this.setCreatedBy(JwtRequestFilter.loggedInUserDetails.get());
        this.setModifiedBy(JwtRequestFilter.loggedInUserDetails.get());
    }

}
