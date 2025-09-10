package com.example.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userName;
    @Column(unique = true,nullable = false)
    private String email;
    private String password;
    @Column(name = "is_verified")
    private boolean isVerified;
    @Column(name = "is_active")
    private boolean isActive;
    @CreatedDate
    @Column(name = "created_at")
    private LocalDate createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
