package exhibition.reservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Reservation_table")
public class Reservation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long exhibitionId;
    private String exhibitionName;
    private String exhibitionStatus;
    private String exhibitionDate;
    private String exhibitionType;
    private String memberName;

    @PostUpdate
    public void onPostUpdate(){
        ResevationCancelled resevationCancelled = new ResevationCancelled();
        BeanUtils.copyProperties(this, resevationCancelled);
        resevationCancelled.publishAfterCommit();

    }
    @PrePersist
    public void onPrePersist() throws Exception {

             exhibition.reservation.external.Exhibition exhibition = new exhibition.reservation.external.Exhibition();

             exhibition = ReservationApplication.applicationContext.getBean(exhibition.reservation.external.ExhibitionService.class)
             .getExhibitionStatus(exhibitionId);

        // 예약 가능상태 여부에 따라 처리
        if ("Available".equals(exhibition.getExhibitionStatus())){
            this.setExhibitionName(exhibition.getExhibitionName());
            this.setExhibitionDate(exhibition.getExhibitionDate());
            this.setExhibitionType(exhibition.getExhibitionType());
            //this.setResortType(resort.getResortType());
            this.setExhibitionStatus("Confirmed");
        } else {
            throw new Exception("The exhibition is not in a usable status.");
        }    

    }
    
    @PostPersist
    public void onPostPersist() throws Exception {

        ReservationCreated reservationCreated = new ReservationCreated();
        BeanUtils.copyProperties(this, reservationCreated);
        reservationCreated.publishAfterCommit();

        
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Long exhibitionId) {
        this.exhibitionId = exhibitionId;
    }
    public String getExhibitionName() {
        return exhibitionName;
    }

    public void setExhibitionName(String exhibitionName) {
        this.exhibitionName = exhibitionName;
    }
    public String getExhibitionStatus() {
        return exhibitionStatus;
    }

    public void setExhibitionStatus(String exhibitionStatus) {
        this.exhibitionStatus = exhibitionStatus;
    }
    public String getExhibitionDate() {
        return exhibitionDate;
    }

    public void setExhibitionDate(String exhibitionDate) {
        this.exhibitionDate = exhibitionDate;
    }
    public String getExhibitionType() {
        return exhibitionType;
    }

    public void setExhibitionType(String exhibitionType) {
        this.exhibitionType = exhibitionType;
    }
    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }




}
