package exhibition.reservation;

import exhibition.reservation.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCreated_then_CREATE_1 (@Payload ReservationCreated reservationCreated) {
        try {

            if (!reservationCreated.validate()) return;

            // view 객체 생성
            Mypage mypage = new Mypage();
            // view 객체에 이벤트의 Value 를 set 함
            mypage.setMemberName(reservationCreated.getMemberName());
            mypage.setExhibitionId(reservationCreated.getExhibitionId());
            mypage.setExhibitionName(reservationCreated.getExhibitionName());
            mypage.setExhibitionStatus(reservationCreated.getExhibitionStatus());
            mypage.setExhibitionDate(reservationCreated.getExhibitionDate());
            mypage.setExhibitionType(reservationCreated.getExhibitionType());
            mypage.setId(reservationCreated.getId());
            // view 레파지 토리에 save
            mypageRepository.save(mypage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenResevationCancelled_then_UPDATE_1(@Payload ResevationCancelled resevationCancelled) {
        try {
            if (!resevationCancelled.validate()) return;
                // view 객체 조회
            Optional<Mypage> mypageOptional = mypageRepository.findById(resevationCancelled.getId());

            if( mypageOptional.isPresent()) {
                 Mypage mypage = mypageOptional.get();
            // view 객체에 이벤트의 eventDirectValue 를 set 함
                 mypage.setExhibitionStatus(resevationCancelled.getExhibitionStatus());
                // view 레파지 토리에 save
                 mypageRepository.save(mypage);
                }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

