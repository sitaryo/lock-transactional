package com.sendroids.tech.locktransactional.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class VersionEntity extends BaseEntity {

    @Version
    private long version = 0L;
}
