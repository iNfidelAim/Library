package app;

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
import ru.library.models.Person;
import ru.library.service.PeopleService;

import java.util.List;

public class PersonView extends ViewPart {

    private TableViewer tableViewer;
    
    
    private PeopleService peopleService = new PeopleService();
    

    @PostConstruct
    public void createComposite(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        // Создаем кнопки
        createButtons(parent);

        // Создаем таблицу для отображения пользователей
        createTableViewer(parent);

        // Получаем данные с сервера и передаем их в таблицу
        try {
            List<Person> persons = peopleService.getAllPersons();  // Метод для получения списка пользователей
            tableViewer.setInput(persons);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createButtons(Composite parent) {
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText("Add Person");
        addButton.addListener(SWT.Selection, event -> openAddPersonDialog(parent));

        Button editButton = new Button(parent, SWT.PUSH);
        editButton.setText("Edit Person");
        editButton.addListener(SWT.Selection, event -> openEditPersonDialog(parent));

        Button deleteButton = new Button(parent, SWT.PUSH);
        deleteButton.setText("Delete Person");
        deleteButton.addListener(SWT.Selection, event -> deleteSelectedPerson(parent));
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
    
    private void createColumns() {
        TableViewerColumn colName = new TableViewerColumn(tableViewer, SWT.NONE);
        colName.getColumn().setText("Name");
        colName.getColumn().setWidth(200);
        colName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Person person = (Person) element;
                return person.getFullName();
            }
        });

        TableViewerColumn colYearOfBirth = new TableViewerColumn(tableViewer, SWT.NONE);
        colYearOfBirth.getColumn().setText("Year of Birth");
        colYearOfBirth.getColumn().setWidth(150);
        colYearOfBirth.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Person person = (Person) element;
                return String.valueOf(person.getYearOfBirth());
            }
        });
    }
    
    private void openAddPersonDialog(Composite parent) {
        AddPersonDialog dialog = new AddPersonDialog(parent.getShell());
        if (dialog.open() == InputDialog.OK) {
            try {
                Person newPerson = dialog.getPerson();
                peopleService.addPerson(newPerson);
                tableViewer.add(newPerson);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(parent.getShell(), "Error", "Failed to add person.");
            }
        }
    }
    
    private void openEditPersonDialog(Composite parent) {
        IStructuredSelection selection = tableViewer.getStructuredSelection();
        if (selection.isEmpty()) {
            MessageDialog.openWarning(parent.getShell(), "Warning", "No person selected.");
            return;
        }

        Person selectedPerson = (Person) selection.getFirstElement();
        EditPersonDialog dialog = new EditPersonDialog(parent.getShell(), selectedPerson);
        if (dialog.open() == InputDialog.OK) {
            try {
            	peopleService.updatePerson(dialog.getPerson());
                tableViewer.refresh();
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(parent.getShell(), "Error", "Failed to update person.");
            }
        }
    }

    private void deleteSelectedPerson(Composite parent) {
        IStructuredSelection selection = tableViewer.getStructuredSelection();
        if (selection.isEmpty()) {
            MessageDialog.openWarning(parent.getShell(), "Warning", "No person selected.");
            return;
        }

        Person selectedPerson = (Person) selection.getFirstElement();
        boolean confirmed = MessageDialog.openConfirm(parent.getShell(), "Confirm", "Are you sure you want to delete this person?");
        if (confirmed) {
            try {
            	peopleService.deletePerson(selectedPerson.getId());
                tableViewer.remove(selectedPerson);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.openError(parent.getShell(), "Error", "Failed to delete person.");
            }
        }
    }

    @Override
    public void setFocus() {
        tableViewer.getControl().setFocus();
    }

	@Override
	public void createPartControl(Composite parent) {
	}
}