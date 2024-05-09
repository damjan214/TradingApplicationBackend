package org.example.model.user;

import jakarta.persistence.*;
import lombok.*;
import org.example.dto.UserDto;
import org.example.model.Portfolio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "countryOfResidence")
    private String countryOfResidence;

    @OneToOne
    @JoinColumn(name = "avatarId")
    private Avatar avatar;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Portfolio portfolio;

    @Column(name = "registerToken")
    private String registerToken;

    @Column(name = "registerTokenExpiration")
    private LocalDateTime registerTokenExpiration;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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

    @PrePersist
    public void prePersist() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", countryOfResidence='" + countryOfResidence + '\'' +
                ", avatar=" + avatar +
                ", portfolio=" + portfolio +
                ", registerToken='" + registerToken + '\'' +
                ", registerTokenExpiration=" + registerTokenExpiration +
                '}';
    }

    public UserDto toDto() {
        return UserDto.builder()
                .id(this.id)
                .uuid(this.uuid)
                .username(this.username)
                .password(this.password)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .email(this.email)
                .countryOfResidence(this.countryOfResidence)

                .build();
    }
}
