package com.telegramBot.FridgeBot.DB;

import lombok.*;
import javax.persistence.*;

@Entity
@AllArgsConstructor @NoArgsConstructor
public class Note {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Getter @Column(unique = true)
    private String name;
    @Setter private Integer need = 0;

    public Note(String name) {
        this.name = name;
    }
}
