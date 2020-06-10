package com.sendroids.tech.locktransactional.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
final public class Coder extends VersionEntity {
    int age = 18;
    int sex = 0;
    double salary = 2000D;
    String phone = "";
    String email = "";
}
