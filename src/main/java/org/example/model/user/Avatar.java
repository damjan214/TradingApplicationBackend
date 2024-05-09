package org.example.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "avatars")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "avatarId")
    private Long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB", name = "data")
    private byte[] data;

    @Column(name = "name")
    private String name;

    @OneToOne(mappedBy = "avatar", fetch = FetchType.LAZY)
    private User user;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID().toString();
    }
}
