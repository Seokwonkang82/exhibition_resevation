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
    @Autowired VoucherRepository voucherRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCreated_CreateVoucher(@Payload ReservationCreated reservationCreated){

        if(!reservationCreated.validate()) return;

        System.out.println("\n\n##### listener CreateVoucher : " + reservationCreated.toJson() + "\n\n");



        // Sample Logic //
        // Voucher voucher = new Voucher();
        // voucherRepository.save(voucher);
         Voucher voucher = new Voucher();
         voucher.setReservationId(reservationCreated.getId());     
         voucher.setVoucherStatus(reservationCreated.getExhibitionStatus());  
         voucherRepository.save(voucher);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverResevationCancelled_DeleteVoucher(@Payload ResevationCancelled resevationCancelled){

        if(!resevationCancelled.validate()) return;

        System.out.println("\n\n##### listener DeleteVoucher : " + resevationCancelled.toJson() + "\n\n");



        // Sample Logic //
        // Voucher voucher = new Voucher();
        // voucherRepository.save(voucher);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
