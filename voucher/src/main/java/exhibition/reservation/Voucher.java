package exhibition.reservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Voucher_table")
public class Voucher {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long reservationId;
    private String voucherStatus;

    @PostPersist
    public void onPostPersist(){
        VoucherActivated voucherActivated = new VoucherActivated();
        BeanUtils.copyProperties(this, voucherActivated);
        voucherActivated.publishAfterCommit();

    }
    @PostUpdate
    public void onPostUpdate(){
        VoucherInactivated voucherInactivated = new VoucherInactivated();
        BeanUtils.copyProperties(this, voucherInactivated);
        voucherInactivated.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    public String getVoucherStatus() {
        return voucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        this.voucherStatus = voucherStatus;
    }




}
