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
        return userRepository.findAllByActiveTrue(pageable)
                .map(userMapper::toDTO);
    }

    public Page<UserResponseDTO> findAllInactive(Pageable pageable) {
        return userRepository.findAllByActiveFalse(pageable)
                .map(userMapper::toDTO);
    }

    public UserResponseDTO findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return userMapper.toDTO(user);
    }

    public UserEntity findByIdAdmin(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return user;
    }

    public UserResponseDTO save(UserCreateDTO user) {
        UserEntity userEntity = userMapper.toUserEntity(user);

        RoleEntity userRole = roleRepository.findByRole(Role.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role USER not found."));

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
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        if(dto.getPassword() != null){
            existingUser.getCredential().setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if(dto.getEmail() != null){
            existingUser.getCredential().setEmail(dto.getEmail());
        }
        userMapper.updateUserEntityFromDTO(dto, existingUser);
        UserEntity savedUser = userRepository.save(existingUser);

        return userMapper.toDTO(savedUser);
    }

    public UserResponseDTO update(String username, UserUpdateDTO dto) {
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        UserEntity existingUser = credential.getUser();

        if(dto.getPassword() != null){
            existingUser.getCredential().setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if(dto.getEmail() != null){
            existingUser.getCredential().setEmail(dto.getEmail());
        }
        userMapper.updateUserEntityFromDTO(dto, existingUser);
        UserEntity savedUser = userRepository.save(existingUser);

        return userMapper.toDTO(savedUser);
    }

    public void update(UserEntity user) {
        userRepository.save(user);
    }

    public void delete(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        user.setActive(false);
        user.getCredential().setActive(false);
        userRepository.save(user);
    }

    public void deleteAccount(String username) {
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        UserEntity user = credential.getUser();
        user.setActive(false);
        user.getCredential().setActive(false);
        userRepository.save(user);
    }

    public void restore(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));
        user.setActive(true);
        user.getCredential().setActive(true);
        userRepository.save(user);
    }

    public UserResponseDTO getProfileByUsername(String username){
        CredentialEntity credential = credentialRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        UserEntity user = credential.getUser();
        return userMapper.toDTO(user);
    }

    public String assignRole(Long id){
        UserEntity user = findByIdAdmin(id);
        CredentialEntity credential = user.getCredential();
        RoleEntity userRole = roleRepository.findByRole(Role.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Role USER not found."));

        Set<RoleEntity> rolesUser = credential.getRoles();
        rolesUser.clear();
        rolesUser.add(userRole);

        credential.setRoles(rolesUser);
        credentialRepository.save(credential);


        if (!rolesUser.contains(userRole)){
            return "The new role could not be assigned.";
        }

        return "Role assigned successfully.";
    }
}
