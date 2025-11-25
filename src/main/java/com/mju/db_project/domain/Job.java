package com.mju.db_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "JOB")
@Getter
@Setter
@NoArgsConstructor
public class Job {

    @Id
    @Column(name = "job_code")
    private Integer jobCode;

    @Column(name = "job_name", nullable = false)
    private String jobName;
}

