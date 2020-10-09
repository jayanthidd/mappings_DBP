package at.campus02.dbp2.jpa;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

public class StudentDaoImpl implements StudentDao{
    EntityManager manager;

    public StudentDaoImpl(EntityManagerFactory factory) {
        manager = factory.createEntityManager();
    }

    @Override
    public boolean create(Student student) {
        if(student == null)
            return false;
        if (student.getId()!=null)
            return false;

        manager.getTransaction().begin();
        manager.persist(student);
        manager.flush();
        manager.getTransaction().commit();// if this does not work an exception will be thrown and it will not return true
        return true;
    }

    @Override
    public Student update(Student student) {
        if(student == null)
            return null;
        else {
            manager.getTransaction().begin();
            manager.merge(student);
            manager.flush();
            manager.getTransaction().commit();
            return student;
        }
    }

    @Override
    public void delete(Student student) {

    }

    @Override
    public Student find(Integer id) {
        return null;
    }

    @Override
    public List<Student> findAll() {
        return null;
    }

    @Override
    public List<Student> findAllByLastName(Student student) {
        return null;
    }

    @Override
    public List<Student> findAllBornBefore(int year) {
        return null;
    }

    @Override
    public List<Student> findAllByGender(String gender) {
        return null;
    }

    @Override
    public void close() {

    }
}
