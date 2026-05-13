package com.example.demo.services;

import com.example.demo.DTOs.FriendRequest.FriendRequestDTO;
import com.example.demo.entities.FriendRequestEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.enums.FriendRequestStatus;
import com.example.demo.repositories.FriendRequestRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.repositories.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final CredentialRepository credentialRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    @Autowired
    public FriendRequestService(FriendRequestRepository friendRequestRepository, CredentialRepository credentialRepository, UserService userService, UserRepository userRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.credentialRepository = credentialRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    private UserEntity getLoggedUser (String email){
        return credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getUser();
    }

    // Metodo para enviar la solicitud de amistad

    public void sendRequest (String senderEmail, Long receiverId){
        UserEntity sender = getLoggedUser(senderEmail);
        UserEntity receiver = userService.findByIdAdmin(receiverId);

        if (sender.getId().equals(receiverId)){
            throw new IllegalArgumentException("You can't send yourself a friend request.");
        }

        boolean yaExiste = friendRequestRepository
                .existsBySenderAndReceiverOrReceiverAndSender(sender, receiver, sender, receiver);
        if (yaExiste){
            throw new IllegalStateException("A request has already been submitted between these users. Please, await a response.");
        }

        FriendRequestEntity request = FriendRequestEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .friendRequestStatus(FriendRequestStatus.PENDING)
                .build();

        friendRequestRepository.save(request);
    }


    public List<FriendRequestDTO> getPendingRequest (String email){
        UserEntity receiver = getLoggedUser(email);

        return friendRequestRepository.findAllByReceiverAndFriendRequestStatus(receiver, FriendRequestStatus.PENDING)
                .stream()
                .map(r -> new FriendRequestDTO(
                        r.getId(),
                        r.getSender().getUsername(),
                        r.getFriendRequestStatus()
                ))
                .toList();
    }


    public void acceptRequest(String email, Long requestId){
        UserEntity receiver = getLoggedUser(email);
        FriendRequestEntity request = findAndValidate(requestId, receiver);

        request.setFriendRequestStatus(FriendRequestStatus.ACCEPTED);

        receiver.getFriends().add(request.getSender());
        request.getSender().getFriends().add(receiver);

        friendRequestRepository.save(request);
        userService.update(receiver);
        userService.update(request.getSender());
    }

    public void denyRequest (String email, Long requestId){
        UserEntity receiver = getLoggedUser(email);
        FriendRequestEntity request = findAndValidate(requestId, receiver);

        friendRequestRepository.delete(request);
    }


    private FriendRequestEntity findAndValidate (Long requestId, UserEntity receiver){
        FriendRequestEntity request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Friend request not found."));

        if (!request.getReceiver().getId().equals(receiver.getId())){
            throw new IllegalArgumentException("You do not have permission to respond to this request.");
        }

        if (!request.getFriendRequestStatus().equals(FriendRequestStatus.PENDING)){
            throw new IllegalStateException("The request has already been processed.");
        }

        return request;
    }

}
