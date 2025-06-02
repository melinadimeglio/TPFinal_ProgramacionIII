package com.example.demo.services;

import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.repositories.CredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CredentialService {
    private final CredentialRepository credentialRepository;

    @Autowired
    public CredentialService(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    public void deleteCredential(String username){
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(()-> new NoSuchElementException("No se encontro la credencial"));

        credential.setActive(false);
    }
}
