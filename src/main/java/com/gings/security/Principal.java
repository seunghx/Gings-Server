package com.gings.security;

import com.gings.utils.code.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Principal {
    public int userId;
    public UserRole role;
}
