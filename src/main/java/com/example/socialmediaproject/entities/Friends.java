package com.example.socialmediaproject.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@IdClass(FriendsPK.class)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Friends {

    @Id
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Id
    @Column(name = "user_friend_id", nullable = false, length = 36)
    private String userFriendId;
    @Basic
    @Column(name = "establish_at", nullable = true)
    private Timestamp establishAt;

    @Basic
    @Column(name = "friend_status",nullable = true)
    private String friendStatus;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Friends friends = (Friends) o;
        return getUserId() != null && Objects.equals(getUserId(), friends.getUserId())
                && getUserFriendId() != null && Objects.equals(getUserFriendId(), friends.getUserFriendId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userFriendId);
    }
}
