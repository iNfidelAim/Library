package app;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ru.library.models.Book;

import org.eclipse.swt.widgets.Shell;

public class EditBookDialog extends Dialog {

    private Text titleText;
    private Text authorText;
    private Text yearText;

    private Book book;

    public EditBookDialog(Shell parentShell, Book book) {
        super(parentShell);
        this.book = book;
    }

    @Override
    protected Composite createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(2, false));

        new Label(container, SWT.NONE).setText("Title:");
        titleText = new Text(container, SWT.BORDER);
        titleText.setText(book.getTitle());

        new Label(container, SWT.NONE).setText("Author:");
        authorText = new Text(container, SWT.BORDER);
        authorText.setText(book.getAuthor());

        new Label(container, SWT.NONE).setText("Year:");
        yearText = new Text(container, SWT.BORDER);
        yearText.setText(String.valueOf(book.getYear()));

        return container;
    }

    @Override
    protected void okPressed() {
        book.setTitle(titleText.getText());
        book.setAuthor(authorText.getText());
        book.setYear(Integer.parseInt(yearText.getText()));
        super.okPressed();
    }

    public Book getBook() {
        return book;
    }
}