package ru.netology.cloudstorage.utils;

import org.junit.Test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

//class JwtTokenUtilsTest {
//    @Test
//    void generateTokenTest() {
//        JwtTokenUtils jwtTokenUtils = new JwtTokenUtils();
//        User user = new User(1L,"aa","bb",new ArrayList<>());
//        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
//                user.getUsername(),
//                user.getPassword(),
//                mapRolesToAuthorities(user.getRoles()));
//        String st = jwtTokenUtils.generateToken(userDetails);
//        String uu = jwtTokenUtils.getUsername(st);
//        assertEquals(userDetails.getUsername(),uu);
//
//    }
//
//    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
//        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
//    }

//}

