package com.m520it.eventtest;

/**
 * Created by 520 on 2016/12/19.
 */

public class PeopleBean implements Cloneable {

    private String name;
    private int age;
    private Integer id;

    private Student mStudent;

    public  Student getStudent() {
        return mStudent;
    }

    public  void setStudent(Student student) {
        mStudent = student;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public  PeopleBean(int age, Integer id, Student student, String name) {
        this.age = age;
        this.id = id;
        mStudent = student;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PeopleBean that = (PeopleBean) o;

        if (age != that.age)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        return mStudent != null ? mStudent.equals(that.mStudent) : that.mStudent == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (mStudent != null ? mStudent.hashCode() : 0);
        return result;
    }

    public int getAge() {

        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
