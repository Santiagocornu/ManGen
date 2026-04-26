package com.GenMan.GenMan.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;


@Entity
@Table(name = "User")
@Getter
@Setter
@ToString(exclude = "password")
@EqualsAndHashCode
@NoArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long Id;
    @Column(nullable = false)
    String nombre;
    @Column(nullable = false)
    Date fechaCreacion;
    @Column(nullable = false)
    String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    String password;
    @Column(nullable = false)
    String roll;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    Sucursal sucursal;
}
