package exhibition.reservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Exhibition_table")
public class Exhibition {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String exhibitionName;
    private String exhibitionStatus;
    private String exhibitionDate;
    private String exhibitionType;

    @PostPersist
    public void onPostPersist(){
        ExhibitionUploaded exhibitionUploaded = new ExhibitionUploaded();
        BeanUtils.copyProperties(this, exhibitionUploaded);
        exhibitionUploaded.publishAfterCommit();

    }
    @PostUpdate
    public void onPostUpdate(){
        StatusChanged statusChanged = new StatusChanged();
        BeanUtils.copyProperties(this, statusChanged);
        statusChanged.publishAfterCommit();

    }
    @PostRemove
    public void onPostRemove(){
        ExhibitionDeleted exhibitionDeleted = new ExhibitionDeleted();
        BeanUtils.copyProperties(this, exhibitionDeleted);
        exhibitionDeleted.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
