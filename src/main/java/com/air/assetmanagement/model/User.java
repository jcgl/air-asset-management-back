package com.air.assetmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "`user`", schema = "dbo")
public class User {

    @Id
    @Column(name = "id")
    private Long id;

    //@Column(name = "username")
    //private String username;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    //@Column(name = "role")
    //private String role;

    protected User() {}

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() { return id; }
    public String getName() { return firstName + " " + lastName; }
    //public String getEmail() { return username + "@air.com"; }
    //public String getRole() { return role; }
}
