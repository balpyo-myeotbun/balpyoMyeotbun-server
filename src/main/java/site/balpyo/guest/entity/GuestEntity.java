package site.balpyo.guest.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import site.balpyo.ai.entity.AIGenerateLogEntity;
import site.balpyo.script.entity.ScriptEntity;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "guest")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestEntity {

    @Id
    private String uid;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
