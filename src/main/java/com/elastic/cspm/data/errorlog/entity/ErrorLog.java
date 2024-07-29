package com.elastic.cspm.data.errorlog.entity;

import com.elastic.cspm.data.iam.entity.IAM;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
