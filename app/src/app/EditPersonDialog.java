package app;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ru.library.models.Person;

import org.eclipse.swt.widgets.Shell;

public class EditPersonDialog extends Dialog {

    private Text fullNameText;
    private Text yearOfBirthText;

    private Person person;

    public EditPersonDialog(Shell parentShell, Person person) {
        super(parentShell);
        this.person = person;
    }

    @Override
    protected Composite createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        new Label(container, SWT.NONE).setText("Full Name:");
        fullNameText = new Text(container, SWT.BORDER);
        fullNameText.setText(person.getFullName());

        new Label(container, SWT.NONE).setText("Year of Birth:");
        yearOfBirthText = new Text(container, SWT.BORDER);
        yearOfBirthText.setText(String.valueOf(person.getYearOfBirth()));

        return container;
    }

    @Override
    protected void okPressed() {
        person.setFullName(fullNameText.getText());
        person.setYearOfBirth(Integer.parseInt(yearOfBirthText.getText()));
        super.okPressed();
    }

    public Person getPerson() {
        return person;
    }
}