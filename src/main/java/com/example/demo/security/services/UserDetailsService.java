package com.example.demo.security.services;

import com.example.demo.security.repositories.CredentialRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final CredentialRepository credentialsRepository;
    public UserDetailsService(CredentialRepository
                                      credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return credentialsRepository.findByEmail(username).orElseThrow();
    }
}

