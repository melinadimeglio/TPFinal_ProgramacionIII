package com.example.demo.entities;

import com.example.demo.enums.FriendRequestStatus;
import com.example.demo.enums.UserPreferences;
import com.example.demo.security.entities.CredentialEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "FriendRequest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserEntity receiver;

    @CreationTimestamp
    private LocalDateTime friendshipDate;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus friendRequestStatus;

}
