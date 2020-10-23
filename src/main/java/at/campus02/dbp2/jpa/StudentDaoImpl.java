package at.campus02.dbp2.jpa;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
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
        String query = "select s from Student s";
        TypedQuery<Student> findquery= manager.createQuery(query,Student.class);
        return findquery.getResultList();
    }

    @Override
    public List<Student> findAllByLastName(String lastname) {
        if(lastname==null)
            return findAll();
        String query = "select s from Student s WHERE upper(s.lastName) = upper(:lastname)";// returns a case insensitive search result
        TypedQuery<Student> findlastname = manager.createQuery(query,Student.class);
        findlastname.setParameter("lastname", lastname);
        return findlastname.getResultList();
    }

    @Override
    public List<Student> findAllBornBefore(int year) {
        LocalDate date =  LocalDate.of(year, 1, 1);
        String query = "select s from Student s WHERE s.birthday < :birthday";
        TypedQuery<Student> findbyyear = manager.createQuery(query,Student.class);
        findbyyear.setParameter("birthday", date);
        return findbyyear.getResultList();
    }

    @Override
    public List<Student> findAllByGender(Gender gender) {
        if(gender==null)
            return Collections.emptyList();// return new ArrayList<>();

        return manager.createNamedQuery("Student.findAllByGender",Student.class)
                .setParameter("gender", gender)
                .getResultList();
    }

    @Override
    public void close() {
        if(manager.isOpen()&&manager!=null)// if the createentitymanager method in the constructor returns null, manager could be null.  Most unlikely but possible
            manager.close();
    }
}
