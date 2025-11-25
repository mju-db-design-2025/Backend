package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "client_contact")
@Getter
@Setter
@NoArgsConstructor
public class ClientContact {

    @EmbeddedId
    private ClientContactId id;

    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client client;
}

