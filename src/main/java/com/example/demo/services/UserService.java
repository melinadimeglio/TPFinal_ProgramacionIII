package com.example.demo.services;

import com.example.demo.DTOs.User.Request.UserCreateDTO;
import com.example.demo.DTOs.User.Response.UserResponseDTO;
import com.example.demo.DTOs.User.UserUpdateDTO;
import com.example.demo.controllers.hateoas.UserModelAssembler;
import com.example.demo.entities.UserEntity;
import com.example.demo.mappers.UserMapper;
import com.example.demo.security.repositories.RoleRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.entities.CredentialEntity;
import com.example.demo.security.entities.RoleEntity;
import com.example.demo.security.enums.Role;
import com.example.demo.security.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.security.repositories.CredentialRepository;


import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialRepository credentialRepository;
    private final RoleRepository roleRepository;
    private final JWTService jwtService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       PasswordEncoder passwordEncoder, CredentialRepository credentialRepository,
                       RoleRepository roleRepository, JWTService jWTService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.credentialRepository = credentialRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jWTService;
    }

    public Page<UserResponseDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    public UserResponseDTO findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));
        return userMapper.toDTO(user);
    }

    public UserEntity findByIdAdmin(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));
        return user;
    }

    public UserResponseDTO save(UserCreateDTO user) {
        UserEntity userEntity = userMapper.toUserEntity(user);

        RoleEntity userRole = roleRepository.findByRole(Role.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER no encontrado"));

        UserEntity savedUser = userRepository.save(userEntity);
        CredentialEntity credential = new CredentialEntity();
        credential.setEmail(user.getEmail());
        credential.setPassword(passwordEncoder.encode(user.getPassword()));
        credential.setUser(savedUser);
        credential.setRoles(Set.of(userRole));

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                credential.getEmail(),
                credential.getPassword(),
                credential.getAuthorities()
        );

        String refreshToken = jwtService.generateRefreshToken(userDetails);
        credential.setRefreshToken(refreshToken);

        credentialRepository.save(credential);

        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));

        userMapper.updateUserEntityFromDTO(dto, existingUser);
        UserEntity savedUser = userRepository.save(existingUser);

        return userMapper.toDTO(savedUser);
    }

    public void update(UserEntity user) {
        userRepository.save(user);
    }

    public void delete(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con ID: " + id));
        userRepository.delete(user);
    }

    public void deleteAccount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
        user.setActive(false);
    }

    public UserResponseDTO getProfileByUsername(String username){
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario con username: " + username));
        return userMapper.toDTO(user);
    }

    public UserEntity assingRole(Long id, String role){
        UserEntity user = findByIdAdmin(id);

        if(role.equals(Role.ROLE_ADMIN.toString())){
            user.getCredential().getRoles().forEach(roleEntity -> roleEntity.setRole(Role.ROLE_ADMIN));
        }
        else if(role.equals(Role.ROLE_COMPANY.toString())){
            user.getCredential().getRoles().forEach(roleEntity -> roleEntity.setRole(Role.ROLE_COMPANY));
        }
        else {
            //logica para advertir que no hubo modificacion
        }
        return user;
    }
}
