package at.ac.tuwien.sepm.assignment.individual.entity;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Objects;

public class HorseTree {

    private Long id;
    private String name;
    private LocalDate dateOfBirth;
    private Sex sex;
    private HorseTree mother;
    private HorseTree father;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Sex getSex() {
        return sex;
    }

    public HorseTree getMother() {
        return mother;
    }

    public HorseTree getFather() {
        return father;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public void setMother(HorseTree mother) {
        this.mother = mother;
    }

    public void setFather(HorseTree father) {
        this.father = father;
    }

    private String fieldsString() {
        DateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");

        return "id=" + id
                + ", name='" + name + '\''
                + ", date_of_birth=" + dateForm.format(dateOfBirth)
                + ", sex=" + sex;
    }

    @Override
    public String toString() {
        return "HorseTree{ " + fieldsString() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HorseTree horseTree = (HorseTree) o;
        return Objects.equals(id, horseTree.id)
                && Objects.equals(name, horseTree.name)
                && Objects.equals(dateOfBirth, horseTree.dateOfBirth)
                && Objects.equals(sex, horseTree.sex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfBirth, sex, mother, father);
    }
}
