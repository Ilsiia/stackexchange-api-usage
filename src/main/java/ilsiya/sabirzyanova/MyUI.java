package ilsiya.sabirzyanova;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import ilsiya.sabirzyanova.components.TextFieldWithButton;
import ilsiya.sabirzyanova.model.Question;
import ilsiya.sabirzyanova.model.QuestionsPage;
import ilsiya.sabirzyanova.service.StackExchangeQueryService;

import javax.servlet.annotation.WebServlet;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
@Title("StackOverflow")
public class MyUI extends UI {

    private VerticalLayout main = new VerticalLayout();
    private Label label = new Label("StackOverflow");
    private Label searchText = new Label("search text");
    private GridLayout gridLayout = new GridLayout(4, 100);
    private ThemeResource greenCheck = new ThemeResource("image/green_check.png");
    private ThemeResource empty = new ThemeResource("image/empty.png");
    private Button goBack = new Button();
    private Button goNext = new Button();
    private TextFieldWithButton fieldWithButton;
    private int pageNum = 1;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        searchText.setVisible(false);
        goBack.setStyleName("back-button");
        goNext.setStyleName("next-button");
        goNext.setVisible(false);
        goBack.setVisible(false);
        goBack.addClickListener(event -> {
            pageNum--;
            doSearch();
        });
        goNext.addClickListener(event -> {
            pageNum++;
            doSearch();
        });
        label.setStyleName("title");
        fieldWithButton = new TextFieldWithButton(new ThemeResource("image/search-32.png"),
                onClick -> {
                    pageNum = 1;
                    doSearch();
                });
        gridLayout.setSizeFull();
        main.addComponents(label, fieldWithButton, searchText, new HorizontalLayout(goBack, goNext), gridLayout);
        setContent(main);
    }

    private void doSearch() {
        gridLayout.removeAllComponents();
        String value = fieldWithButton.getTextField().getValue();
        QuestionsPage page = null;
        try {
            page = StackExchangeQueryService.getInstance().getQuestionsPage(value, pageNum);
        } catch (Exception e) {
            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
        searchText.setValue("search result for '" + value + "'");
        searchText.setVisible(true);
        List<Question> items = page.getItems();
        items.forEach(question -> {
            Link sourceLink = new Link(question.getTitle(), new ExternalResource(question.getLink()));
            Image image = new Image(null, question.getIs_answered() ? greenCheck : empty);
            image.setDescription(question.getIs_answered() ? "answered" : "not answered");
            gridLayout.addComponents(new Label(question.getCreation_date()), sourceLink, new Label(question.getOwner().getDisplay_name()), image);
        });
        goBack.setVisible(!page.isFirstPage());
        goNext.setVisible(page.getHas_more());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
