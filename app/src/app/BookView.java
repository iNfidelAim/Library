package app;

import java.util.List;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import jakarta.annotation.PostConstruct;
import ru.library.models.Book;
import ru.library.service.BooksService;

public class BookView extends ViewPart {

	private TableViewer tableViewer;
	
    private BooksService booksService = new BooksService();


    @PostConstruct
    public void createComposite(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        // Создаем кнопки
        createButtons(parent);

        // Создаем таблицу для отображения книг
        createTableViewer(parent);

        // Получаем данные с сервера и передаем их в таблицу
        try {
            List<Book> books = booksService.getAllBooks();
            tableViewer.setInput(books);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createButtons(Composite parent) {
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText("Add Book");
        addButton.addListener(SWT.Selection, event -> openAddBookDialog(parent));

        Button editButton = new Button(parent, SWT.PUSH);
        editButton.setText("Edit Book");
        editButton.addListener(SWT.Selection, event -> openEditBookDialog(parent));

        Button deleteButton = new Button(parent, SWT.PUSH);
        deleteButton.setText("Delete Book");
        deleteButton.addListener(SWT.Selection, event -> deleteSelectedBook(parent));
    }

    private void createTableViewer(Composite parent) {
        tableViewer = new TableViewer(parent,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new org.eclipse.swt.layout.GridData(SWT.FILL, SWT.FILL, true, true));

        createColumns();
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    }

	@Override
	public void createPartControl(Composite parent) {
	}

	private void createColumns() {
		TableViewerColumn colTitle = new TableViewerColumn(tableViewer, SWT.NONE);
		colTitle.getColumn().setText("Title");
		colTitle.getColumn().setWidth(200);
		colTitle.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Book book = (Book) element;
				return book.getTitle();
			}
		});

		TableViewerColumn colAuthor = new TableViewerColumn(tableViewer, SWT.NONE);
		colAuthor.getColumn().setText("Author");
		colAuthor.getColumn().setWidth(150);
		colAuthor.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Book book = (Book) element;
				return book.getAuthor();
			}
		});

		TableViewerColumn colYear = new TableViewerColumn(tableViewer, SWT.NONE);
		colYear.getColumn().setText("Year");
		colYear.getColumn().setWidth(100);
		colYear.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Book book = (Book) element;
				return String.valueOf(book.getYear());
			}
		});
	}

	private void openAddBookDialog(Composite parent) {
	    AddBookDialog dialog = new AddBookDialog(parent.getShell());
	    if (dialog.open() == InputDialog.OK) {
	        try {
	            Book newBook = dialog.getBook();
	            booksService.addBook(newBook);
	            tableViewer.add(newBook);
	        } catch (Exception e) {
	            e.printStackTrace();
	            MessageDialog.openError(parent.getShell(), "Error", "Failed to add book.");
	        }
	    }
	}
	
	private void openEditBookDialog(Composite parent) {
		IStructuredSelection selection = tableViewer.getStructuredSelection();
		if (selection.isEmpty()) {
			MessageDialog.openWarning(parent.getShell(), "Warning", "No book selected.");
			return;
		}

		Book selectedBook = (Book) selection.getFirstElement();
		EditBookDialog dialog = new EditBookDialog(parent.getShell(), selectedBook);
		if (dialog.open() == InputDialog.OK) {
			try {
				booksService.updateBook(dialog.getBook());
				tableViewer.refresh();
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(parent.getShell(), "Error", "Failed to update book.");
			}
		}
	}

	private void deleteSelectedBook(Composite parent) {
		IStructuredSelection selection = tableViewer.getStructuredSelection();
		if (selection.isEmpty()) {
			MessageDialog.openWarning(parent.getShell(), "Warning", "No book selected.");
			return;
		}

		Book selectedBook = (Book) selection.getFirstElement();
		boolean confirmed = MessageDialog.openConfirm(getSite().getShell(), "Confirm",
				"Are you sure you want to delete this book?");
		if (confirmed) {
			try {
				booksService.deleteBook(selectedBook.getId());
				tableViewer.remove(selectedBook);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(parent.getShell(), "Error", "Failed to delete book.");
			}
		}
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}
}
