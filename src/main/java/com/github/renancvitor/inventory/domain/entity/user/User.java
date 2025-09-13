package com.github.renancvitor.inventory.domain.entity.user;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.renancvitor.inventory.domain.entity.person.Person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "User")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String password;

    @OneToOne(optional = false)
    @JoinColumn(name = "people_id", nullable = false, unique = true)
    private Person personName;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_types_id", nullable = false)
    private UserTypeEntity userType;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, name = "first_access")
    private Boolean firstAccess = true;

    public User(String password, Person personName, UserTypeEntity userType) {
        this.password = password;
        this.personName = personName;
        this.userType = userType;
        this.active = true;
        this.firstAccess = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return personName.getEmail();
    }

    public void updatePassword(String newEncryptedPassword) {
        if (newEncryptedPassword == null || newEncryptedPassword.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser nula ou vazia");
        }
        this.password = newEncryptedPassword;
    }

}
