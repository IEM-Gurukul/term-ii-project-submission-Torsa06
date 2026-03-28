package model;

public abstract class Person {

    private String id;
    private String name;

    public Person(String id, String name) {
        this.id   = id;
        this.name = name;
    }

    public String getId()   { return id; }
    public String getName() { return name; }

    public void setId(String id)     { this.id   = id; }
    public void setName(String name) { this.name = name; }

    public abstract String getDetails();

    @Override
    public String toString() {
        return getDetails();
    }
}
