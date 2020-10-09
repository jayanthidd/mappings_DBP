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
        if(student == null || find(student.getId()) == null)
            return null;
        else {
            manager.getTransaction().begin();
            Student updated = manager.merge(student);
            manager.flush();
            manager.getTransaction().commit();
            return updated;
        }
    }

    @Override
    public void delete(Student student) {
        if(student==null || find(student.getId())==null)
            return;
        manager.getTransaction().begin();
        //manager.remove(manager.merge(customer));
        Student managed = manager.merge(student);
        manager.remove(managed);
        manager.getTransaction().commit();
    }

    @Override
    public Student find(Integer id) {
        if(id==null)
            return null;
        return manager.find(Student.class,id);
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
