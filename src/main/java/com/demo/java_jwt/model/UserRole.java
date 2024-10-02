package com.demo.java_jwt.model;

import com.demo.java_jwt.util.JwtRequestFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.SQLDelete;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "user_role")
@SQLDelete(sql = "UPDATE user_role SET is_active = false, deleted_flag = true WHERE id=?")
public class UserRole {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "user_id_fk")
  private User user;

  @ManyToOne
  @JoinColumn(name = "role_id_fk")
  private Role role;

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

  @PrePersist
  @PreUpdate
  void setAuditDetails() {
    this.setCreatedBy(JwtRequestFilter.loggedInUserDetails.get());
    this.setModifiedBy(JwtRequestFilter.loggedInUserDetails.get());
  }

}
