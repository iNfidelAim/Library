package app;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ru.library.models.Book;

import org.eclipse.swt.widgets.Shell;

public class AddBookDialog extends Dialog {

    private Text titleText;
    private Text authorText;
    private Text yearText;

    private Book book;

    public AddBookDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Composite createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        // Поле для ввода названия книги
        new Label(container, SWT.NONE).setText("Title:");
        titleText = new Text(container, SWT.BORDER);

        // Поле для ввода автора
        new Label(container, SWT.NONE).setText("Author:");
        authorText = new Text(container, SWT.BORDER);

        // Поле для ввода года издания
        new Label(container, SWT.NONE).setText("Year:");
        yearText = new Text(container, SWT.BORDER);

        return container;
    }

    @Override
    protected void okPressed() {
        // Сохраняем введенные данные в объект Book
        book = new Book(titleText.getText(), authorText.getText(), Integer.parseInt(yearText.getText()));
        super.okPressed();
    }

    public Book getBook() {
        return book;
    }
}
