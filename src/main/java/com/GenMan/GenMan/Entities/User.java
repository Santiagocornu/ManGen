package com.GenMan.GenMan.Entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(
        name = "User",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_email_sucursal",
                columnNames = {"email", "sucursal_id"}
        )
)
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
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate fechaCreacion;
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
