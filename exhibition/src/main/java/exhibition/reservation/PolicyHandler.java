package exhibition.reservation;

import exhibition.reservation.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired ExhibitionRepository exhibitionRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCreated_ChangeExhibitionStatus(@Payload ReservationCreated reservationCreated){

        if(!reservationCreated.validate()) return;

        System.out.println("\n\n##### listener ChangeExhibitionStatus : " + reservationCreated.toJson() + "\n\n");



        // Sample Logic //
        // Exhibition exhibition = new Exhibition();
        // exhibitionRepository.save(exhibition);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverResevationCancelled_ChangeExhibitionStatus(@Payload ResevationCancelled resevationCancelled){

        if(!resevationCancelled.validate()) return;

        System.out.println("\n\n##### listener ChangeExhibitionStatus : " + resevationCancelled.toJson() + "\n\n");



        // Sample Logic //
        // Exhibition exhibition = new Exhibition();
        // exhibitionRepository.save(exhibition);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
