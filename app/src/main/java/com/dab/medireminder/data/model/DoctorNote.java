package com.dab.medireminder.data.model;

import java.util.List;

public class DoctorNote {
    private List<String> prescriptions;
    private String note;

    public List<String> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<String> prescriptions) {
        this.prescriptions = prescriptions;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
