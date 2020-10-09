package at.campus02.dbp2.jpa;

import java.util.List;

public interface StudentDao {

    boolean create(Student student);
    Student update (Student student);
    void delete(Student student);
    Student find(Integer id);

    List<Student> findAll();
    List<Student> findAllByLastName(Student student);
    List<Student> findAllBornBefore(int year);
    List<Student> findAllByGender(String gender);

    void close();

}
