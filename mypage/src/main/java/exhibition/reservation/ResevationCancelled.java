package exhibition.reservation;

public class ResevationCancelled extends AbstractEvent {

    private Long id;
    private Long Exhibition id;
    private String Exhibition Name;
    private String Exhibition Status;
    private String Exhibition Date;
    private String Exhibition Type;
    private String Member Name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getExhibitionId() {
        return Exhibition id;
    }

    public void setExhibitionId(Long Exhibition id) {
        this.Exhibition id = Exhibition id;
    }
    public String getExhibitionName() {
        return Exhibition Name;
    }

    public void setExhibitionName(String Exhibition Name) {
        this.Exhibition Name = Exhibition Name;
    }
    public String getExhibitionStatus() {
        return Exhibition Status;
    }

    public void setExhibitionStatus(String Exhibition Status) {
        this.Exhibition Status = Exhibition Status;
    }
    public String getExhibitionDate() {
        return Exhibition Date;
    }

    public void setExhibitionDate(String Exhibition Date) {
        this.Exhibition Date = Exhibition Date;
    }
    public String getExhibitionType() {
        return Exhibition Type;
    }

    public void setExhibitionType(String Exhibition Type) {
        this.Exhibition Type = Exhibition Type;
    }
    public String getMemberNam() {
        return Member Name;
    }

    public void setMemberNam(String Member Name) {
        this.Member Name = Member Name;
    }
}