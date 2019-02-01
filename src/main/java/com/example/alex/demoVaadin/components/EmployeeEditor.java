package com.example.alex.demoVaadin.components;

import com.example.alex.demoVaadin.domain.Employee;
import com.example.alex.demoVaadin.repository.EmployeeRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@UIScope
public class EmployeeEditor extends VerticalLayout implements KeyNotifier {


    private final EmployeeRepository employeeRepository;

    private Employee employee;

    private TextField firstName = new TextField("firstName");
    private TextField lastName = new TextField("lastName");
    private TextField patronymic = new TextField("patronymic");

    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<Employee> binder = new Binder<>(Employee.class);

    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public EmployeeEditor(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;

        add(firstName, lastName, patronymic, actions);
        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editEmployee(employee));
        setVisible(false);
    }

    public void delete() {
        employeeRepository.delete(employee);
        changeHandler.onChange();
    }

    public void save() {
        employeeRepository.save(employee);
        changeHandler.onChange();
    }

    public void editEmployee(Employee newEmp) {
        if (newEmp == null) {
            setVisible(false);
            return;
        }

        if (newEmp.getId() != null) {
            employee = employeeRepository.findById(newEmp.getId()).orElse(newEmp);
        }else {
            employee = newEmp;
        }

        binder.setBean(employee);

        setVisible(true);
        lastName.focus();
    }
}
