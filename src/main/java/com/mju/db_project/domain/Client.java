package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @Column(name = "client_id")
    private Integer clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;
}

