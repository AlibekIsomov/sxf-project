//package com.sxf.project.service.impl;
//
//import com.sxf.project.entity.User;
//import com.sxf.project.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuthService {
//@Autowired
//private UserRepository userRepository;
//
//
//    public User loadUserByUsername(String source) throws UsernameNotFoundException {
//        return (User) userRepository.findByUsernameOrEmail(source)
//                .orElseThrow(() -> new UsernameNotFoundException("user_not_found"));
//    }
//}
