package exhibition.reservation;

public class ResevationCancelled extends AbstractEvent {

    private Long id;
    private Long exhibitionId;
    private String exhibitionName;
    private String exhibitionStatus;
    private String exhibitionDate;
    private String exhibitionType;
    private String memberNam;

    public ResevationCancelled(){
        super();
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
    public String getMemberNam() {
        return memberNam;
    }

    public void setMemberNam(String memberNam) {
        this.memberNam = memberNam;
    }
}
