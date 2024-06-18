package ScreeningHumanity.MemberMypageServer.subscribe.service.implement;

import ScreeningHumanity.MemberMypageServer.global.common.exception.CustomException;
import ScreeningHumanity.MemberMypageServer.global.common.feignclinet.ProviderCallFeignClient;
import ScreeningHumanity.MemberMypageServer.global.common.feignclinet.vo.PaymentGetCashVo;
import ScreeningHumanity.MemberMypageServer.global.common.kafka.KafkaProducer;
import ScreeningHumanity.MemberMypageServer.global.common.response.BaseResponse;
import ScreeningHumanity.MemberMypageServer.global.common.response.BaseResponseCode;
import ScreeningHumanity.MemberMypageServer.subscribe.dto.KafkaMessageDto;
import ScreeningHumanity.MemberMypageServer.subscribe.dto.SubscribeDto;
import ScreeningHumanity.MemberMypageServer.subscribe.entity.SubscribeEntity;
import ScreeningHumanity.MemberMypageServer.subscribe.entity.SubscribeStatus;
import ScreeningHumanity.MemberMypageServer.subscribe.repository.SubscribeJpaRepository;
import ScreeningHumanity.MemberMypageServer.subscribe.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubScribeServiceImp implements SubscribeService {

    private final SubscribeJpaRepository subscribeJpaRepository;
    private final KafkaProducer kafkaProducer;
    private final ProviderCallFeignClient providerCallFeignClient;

    @Transactional
    @Override
    public BaseResponseCode createNewSubscribe(String uuid, SubscribeDto.Create requestDto, String accessToken) {
        int updateCount = subscribeJpaRepository.updateSubscribeStatus(uuid, requestDto.getNickName(),
                SubscribeStatus.SUBSCRIBE);
        if(updateCount > 0){
            return BaseResponseCode.UPDATE_SUBSCRIBE_SUCCESS;
        }

        if (Boolean.TRUE == subscribeJpaRepository.existsBySubscriberUuidAndSubscribedToNickNameAndStatus(
                uuid,
                requestDto.getNickName(),
                SubscribeStatus.SUBSCRIBE
        )) {
            throw new CustomException(BaseResponseCode.ALREADY_EXIST_SUBSCRIBE_MEMBER_ERROR);
        }

        try {
            BaseResponse<PaymentGetCashVo> response = providerCallFeignClient.searchMemberCash(accessToken);
            if (response.result().getCash() < requestDto.getCash()) {
                throw new CustomException(BaseResponseCode.NOT_ENOUGH_MEMBER_CASH_ERROR);
            }
        } catch (Exception e) {
            throw new CustomException(BaseResponseCode.SEARCH_MEMBER_CASH_ERROR);
        }

        //create new
        SubscribeEntity savedData = subscribeJpaRepository.save(
                SubscribeEntity
                        .builder()
                        .subscriberUuid(uuid)
                        .subscribedToNickName(requestDto.getNickName())
                        .status(SubscribeStatus.SUBSCRIBE)
                        .build()
        );

        try {
            kafkaProducer.send(
                    "subscribe-payment-changecash",
                    KafkaMessageDto.UseCash
                            .builder()
                            .uuid(uuid)
                            .cash(requestDto.getCash())
                            .build()
            ).get();
        } catch (Exception e) {
            subscribeJpaRepository.deleteById(savedData.getId());
            throw new CustomException(BaseResponseCode.CREATE_NEW_SUBSCRIBE_ERROR);
        }

        return BaseResponseCode.SUCCESS;
    }

    @Transactional
    @Override
    public void deleteSubscribe(String uuid, SubscribeDto.Delete requestDto) {
        int updatedCount = subscribeJpaRepository.updateSubscribeStatus(
                uuid, requestDto.getNickName(), SubscribeStatus.NO_SUBSCRIBE
        );

        if(updatedCount == 0){
            throw new CustomException(BaseResponseCode.NOT_EXIST_SUBSCRIBE_INFO_ERROR);
        }
    }
}