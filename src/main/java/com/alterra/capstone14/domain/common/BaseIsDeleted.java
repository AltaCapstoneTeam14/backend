package com.alterra.capstone14.domain.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseIsDeleted extends BaseUpdatedAt{
    @JsonIgnore
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @PrePersist
    void onCreate(){
        this.isDeleted = Boolean.FALSE;
        this.setCreatedAt(LocalDateTime.now());
    }
}