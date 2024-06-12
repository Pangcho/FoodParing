package com.example.food;

import jakarta.persistence.*;

@Entity
@Table(name = "wine")
public class Wine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int wineId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_src")
    private String imageSrc;

    @Column(name = "description")
    private String description;

    @Column(name = "pairing_food")
    private String pairingFood;

    @Column(name = "note")
    private String note;

    @Override
    public String toString() {
        return "Wine{" +
                "wineId=" + wineId +
                ", name='" + name + '\'' +
                ", imageSrc='" + imageSrc + '\'' +
                ", description='" + description + '\'' +
                ", pairingFood='" + pairingFood + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
