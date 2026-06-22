package model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table (name = "productos")
public class Product {

    @Id
    private Long id;

    @Column (name = "nombre")
    private String title;

    @Column (name = "descripcion")
    private String description;

    @Column (name = "categoria")
    private String category;

    @Column (name = "precio")
    private double price;

    @Column (name = "cantidad")
    private int stock;
}
