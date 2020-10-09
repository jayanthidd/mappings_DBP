import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDao;
import at.campus02.dbp2.jpa.StudentDaoImpl;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class StudentDaoSpec {

    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentDao dao;

    private Student prepareStudent(String firstName, String lastName, Gender gender, String birthdayString){
        Student student = new Student();
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setGender(gender);
        if(birthdayString!=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            student.setBirthday(LocalDate.parse(birthdayString, formatter));
        }
        return student;
    }
    private void create(Student student){
        manager.getTransaction().begin();
        manager.persist(student);
        manager.getTransaction().commit();
    }

    @Before
    public void setup() {
        factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");
        manager = factory.createEntityManager();
        dao = new StudentDaoImpl(factory);
    }

    @Test
    public void ensureThatToUpperCaseResultsInALlUpperCaseLetters() {
        //given
        String str = "string";

        //when
        String result = str.toUpperCase();

        //then
        assertThat(result, is("STRING"));//the assert and corematchers methods have been statically imported
    }

    @Test
    public void createNullAsStudentReturnsFalse() {
        //given

        //when
        boolean result = dao.create(null);

        //then
        assertThat(result, is(false));
    }

    @Test
    public void createPersistsStudentInDatabaseAndReturnsTrue(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");

        //when
        boolean result = dao.create(student);

        //then
        assertThat(result, is(true));
        // check if the student exists in the database
        Student fromDB = manager.find(Student.class, student.getId());
        assertThat(fromDB.getId(), is(student.getId()));
    }

    @Test
    public void createAlreadyExistingStudentReturnsFalse(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        create(student);

        //when
        boolean result = dao.create(student);

        //then
        assertThat(result, is(false));
    }
}

