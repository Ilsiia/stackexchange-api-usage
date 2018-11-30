package ilsiya.sabirzyanova.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionsPage {
    private List<Question> items;
    private Boolean has_more;
    private int pageNum = 1;

    public Boolean getHas_more() {
        return has_more;
    }

    public void setHas_more(Boolean has_more) {
        this.has_more = has_more;
    }

    public List<Question> getItems() {
        return items;
    }

    public void setItems(List<Question> items) {
        this.items = items;
    }

    public boolean isFirstPage() {
        return pageNum == 1;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
