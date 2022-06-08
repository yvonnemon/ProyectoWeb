package com.yvonne.proyecto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
public class User {
    String ROLE_PREFIX = "";

    @Id
    @Column(name = "id_user")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "dni", nullable = false, unique = true)
    private String dni;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "telephone", nullable = false)
    private Integer telephone;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "address", nullable = false)
    private String address;

    //@JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password", nullable = false)
    private String password;

    //se le quito el unique para poder hacer pruebas
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role", nullable = false)
    //@Enumerated(EnumType.STRING)
    private Role role;

    @Transient
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Calendar> vacation;

    @Transient
    @Cascade(org.hibernate.annotations.CascadeType.DELETE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Document> documents;

    public User() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getTelephone() {
        return telephone;
    }

    public void setTelephone(Integer telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public GrantedAuthority getAuthorities() {
        //List<GrantedAuthority> list = new ArrayList<>();

       //list.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.toString()));

      return new SimpleGrantedAuthority(role.toString());
   }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", telephone=" + telephone +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && Objects.equals(dni, user.dni) && Objects.equals(name, user.name) && Objects.equals(lastname, user.lastname)
                && Objects.equals(telephone, user.telephone) && Objects.equals(username, user.username) && Objects.equals(address, user.address) && Objects.equals(email, user.email)
                && role == user.role && Objects.equals(vacation, user.vacation) && Objects.equals(documents, user.documents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dni, name, lastname, telephone, username, address, email, role, vacation, documents);
    }
}
