package exhibition.reservation;

public class ResevationCancelled extends AbstractEvent {

    private Long id;
    private Long exhibitionid;
    private String exhibitionName;
    private String exhibitionStatus;
    private String exhibitionDate;
    private String exhibitionType;
    private String memberName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getExhibitionId() {
        return exhibitionid;
    }

    public void setExhibitionId(Long exhibitionid) {
        this.exhibitionid = exhibitionid;
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
