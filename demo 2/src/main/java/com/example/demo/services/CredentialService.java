package com.example.demo.services;

import com.example.demo.repositories.CheckListRepository;
import com.example.demo.repositories.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {
    private final CredentialRepository credentialRepository;

    @Autowired
    public CredentialService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }
}
