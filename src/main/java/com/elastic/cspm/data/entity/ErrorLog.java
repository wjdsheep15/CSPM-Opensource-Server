package com.elastic.cspm.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Setter
@Table(name = "ErrorLog")
public class ErrorLog {
    @Id
    @Column(name="errorLog_id")
    private Long id;

    @Column(name="type", nullable = false)
    private String type;

    @Column(name="description", nullable = false)
    private String descriptiong;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "iam_id")
    private IAM iam;
}
