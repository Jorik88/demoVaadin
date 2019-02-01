package com.example.alex.demoVaadin.view;

import com.example.alex.demoVaadin.components.EmployeeEditor;
import com.example.alex.demoVaadin.domain.Employee;
import com.example.alex.demoVaadin.repository.EmployeeRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class EmployeeList extends VerticalLayout {

    private final EmployeeRepository repository;
    private Grid<Employee> grid = new Grid<>(Employee.class);

    private final TextField filter = new TextField("", "Type to filter");
    private final Button addEmp = new Button("AddNew");
    private final HorizontalLayout toolBar = new HorizontalLayout(filter, addEmp);
    private final EmployeeEditor employeeEditor;

    @Autowired
    public EmployeeList(EmployeeRepository repository, EmployeeEditor employeeEditor) {
        this.repository = repository;
        this.employeeEditor = employeeEditor;
        add(toolBar, grid, employeeEditor);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> showEmployee(e.getValue()));

        grid.asSingleSelect().addValueChangeListener(e -> {
            employeeEditor.editEmployee(e.getValue());
        });

        addEmp.addClickListener(e -> employeeEditor.editEmployee(new Employee()));

        employeeEditor.setChangeHandler(() -> {
            employeeEditor.setVisible(false);
            showEmployee(filter.getValue());
        });

        showEmployee("");
    }

    private void showEmployee(String name) {
        if (name.isEmpty()) {
            grid.setItems(this.repository.findAll());
        }else {
            grid.setItems(this.repository.findByName(name));
        }
    }

}
