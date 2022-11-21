package com.example.demo;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;

import java.time.LocalDate;

public class Parameters {

    public String instrument;
    public String configuration;
    public String matrix;
    public String report;
    private String plotScale;

    private String plotType;

    public LocalDate startDate;
    public LocalDate endDate;

    private Boolean showAnnotation;
    private Boolean groupAxis;
    public Boolean showExcluded;
    private Boolean showGuide;

    public Parameters(ChoiceBox instrumentBox,
                      ChoiceBox configurationBox,
                      ChoiceBox matrixBox,
                      ChoiceBox reportBox,
                      ChoiceBox yAxisBox,
                      RadioButton leveyJenningsButton,
                      RadioButton movingRangeButton,
                      RadioButton cusummButton,
                      RadioButton cusumvButton,
                      DatePicker startDatePicker,
                      DatePicker endDatePicker,
                      CheckBox annotationCheckBox,
                      CheckBox groupXAxisCheckBox,
                      CheckBox showExcludedCheckBox,
                      CheckBox showGuideSetCheckBox) {


        this.instrument = instrumentBox.getSelectionModel().getSelectedItem().toString();
        this.configuration = configurationBox.getSelectionModel().getSelectedItem().toString();
        this.matrix = matrixBox.getSelectionModel().getSelectedItem().toString();
        this.report = reportBox.getSelectionModel().getSelectedItem().toString();
        this.plotScale = yAxisBox.getSelectionModel().getSelectedItem().toString();

        if(leveyJenningsButton.isSelected()){
            this.plotType = "Levey-Jennings";
        } else if (movingRangeButton.isSelected()) {
            this.plotType = "Moving-Range";
        } else if (cusummButton.isSelected()) {
            this.plotType = "CUSUMm";
        } else {
            this.plotType = "CUSUMv";
        }

        this.startDate = startDatePicker.getValue();
        this.endDate = endDatePicker.getValue();

        this.showAnnotation = annotationCheckBox.isSelected();
        this.groupAxis = groupXAxisCheckBox.isSelected();
        this.showExcluded = showExcludedCheckBox.isSelected();
        this.showGuide = showGuideSetCheckBox.isSelected();
    }

}
