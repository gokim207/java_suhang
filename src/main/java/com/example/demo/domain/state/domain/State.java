package com.example.demo.domain.state.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "standard", nullable = false)
    private String standard;

    @Column(name = "isMain", nullable = false)
    private boolean isMain;

    @Builder
    public State(Long id, String name, String description, String standard ,boolean isMain) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.standard = standard;
        this.isMain = isMain;
    }
}
