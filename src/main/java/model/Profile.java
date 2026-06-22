package model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table (name = "perfiles")
public class Profile {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String name;

    @OneToMany (mappedBy = "profile")
    private List<User> users;

    public Profile() {
        users = new ArrayList<>();
    }
}
