package ScreeningHumanity.MemberMypageServer.subscribe.entity;

import ScreeningHumanity.MemberMypageServer.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscribe")
public class SubscribeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //구독 하는 사람의 uuid
    @Column(name = "subscriber_uuid", nullable = false)
    private String subscriberUuid;

    //구독 하는 사람의 닉네임
    @Column(name = "subscriber_nick_name", nullable = false)
    private String subscriberNickName;

    //구독 받는 사람의 닉네임
    @Column(name = "subscribed_nick_name", nullable = false)
    private String subscribedNickName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscribeStatus status;
}
