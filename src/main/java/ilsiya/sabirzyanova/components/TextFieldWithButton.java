package ilsiya.sabirzyanova.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class TextFieldWithButton extends CssLayout {

    private final TextField textField;
    private final Button button;

    public TextFieldWithButton(Resource icon, Button.ClickListener listener) {
        setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        textField = new TextField();
        textField.setWidth(200, Unit.PERCENTAGE);
        button = new Button(icon);
        button.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        button.addClickListener(listener);
        button.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        addComponents(textField, button);
    }

    public TextField getTextField() {
        return textField;
    }

    public Button getButton() {
        return button;
    }
}