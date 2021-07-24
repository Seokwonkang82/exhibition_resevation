package exhibition.reservation;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Mypage_table")
public class Mypage {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private String memberName;
        private Long exhibitionId;
        private Long reservationId;
        private Long voucherId;
        private String exhibitionName;
        private String exhibitionStatus;
        private String exhibitionDate;
        private String exhibitionType;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public String getMemberName() {
            return memberName;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
        public Long getExhibitionId() {
            return exhibitionId;
        }

        public void setExhibitionId(Long exhibitionId) {
            this.exhibitionId = exhibitionId;
        }
        public Long getReservationId() {
            return reservationId;
        }

        public void setReservationId(Long reservationId) {
            this.reservationId = reservationId;
        }
        public Long getVoucherId() {
            return voucherId;
        }

        public void setVoucherId(Long voucherId) {
            this.voucherId = voucherId;
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

}
