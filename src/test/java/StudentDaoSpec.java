import at.campus02.dbp2.jpa.Gender;
import at.campus02.dbp2.jpa.Student;
import at.campus02.dbp2.jpa.StudentDao;
import at.campus02.dbp2.jpa.StudentDaoImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class StudentDaoSpec {

    private EntityManagerFactory factory;
    private EntityManager manager;
    private StudentDao dao;

    //<editor-fold description ="Help Methods">
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
    //</editor-fold>
    @Before
    public void setup() {
        factory = Persistence.createEntityManagerFactory("nameOfJpaPersistenceUnit");
        manager = factory.createEntityManager();
        dao = new StudentDaoImpl(factory);
    }

    @After
    public void tearDown(){
        dao.close();
        if(manager.isOpen())// if we do not check and we try to close an already closed manager or factoy, it will throw an exception
            manager.close();
        if(factory.isOpen())
            factory.close();
    }

    //<editor-fold description ="CREATE">
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
    //</editor-fold>

    //<editor-fold description ="FIND">
    @Test
    public void findStudentReturnsEntityFromDatabase(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        create(student);

        //when
        //the student which our method is going to find
        Student result = dao.find(student.getId());
        //the student which we are directly retrieving from the database
        Student fromDB = manager.find(Student.class, student.getId());

        //then
        assertThat(result,is(fromDB));
    }

    @Test
    public void findStudentWithNullAsIdReturnsNull(){
        //expect
        assertThat(dao.find(null),is(nullValue()));
    }

    @Test
    public void finsStudentWithNotExistingIdReturnsNull(){
        //expect
        assertThat(dao.find(4711), is(nullValue()));
    }
    //</editor-fold>

    //<editor-fold description ="UPDATE">
    @Test
    public void updateStudentChangesValuesInDatabase(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        create(student);

        //since we are using the same entity manager for persisting and then finding
        manager.clear();

        //when
        student.setLastName("Married-Now");
        //the student which our method is going to find
        Student result = dao.update(student);
        //the student which we are directly retrieving from the database
        Student fromDB = manager.find(Student.class, student.getId());

        //then
        assertThat(result.getLastName(),is("Married-Now"));
        assertThat(fromDB.getLastName(),is("Married-Now"));
        assertThat(result,is(fromDB));
    }

    @Test
    public void updateNullAsStudentReturnsNull(){
        //expect
        assertThat(dao.update(null),is(nullValue()));//null instead of nullValue() will not work here
    }

    @Test
    public void updateNotExistingStudentReturnsNull(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");

        //when
        Student result = dao.update(student);

        //then
        assertThat(result,is(nullValue()));
    }
    //</editor-fold>

    //<editor-fold description ="DELETE">
    @Test
    public void deleteStudentRemovesEntityFromDatabase(){
        //given
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        create(student);

        //when
        manager.clear();//cache must be cleared
        dao.delete(student);
        Student deleted = manager.find(Student.class,student.getId());

        //then
        assertThat(deleted,is(nullValue()));
    }

    @Test
    public void deleteNullOrNotExistingStudentDoesNotThrowException(){
        //expect no exception
        Student student = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        dao.delete(null);
        dao.delete(student);
    }
    //</editor-fold>

    //<editor-fold description ="QUERIES">

    @Test
    public void findAllReturnsAllEntitiesFromDatabase() {
        //given
        Student student1 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        //when
        List<Student> result = dao.findAll();

        //then
        assertThat(result.size(),is(3));
        assertThat(result,hasItems(student1,student2,student3));
    }

    @Test
    public void findByLastnameReturnsMatchingStudents(){
        //given
        Student student1 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        //when
        List<Student> result = dao.findAllByLastName("secondName");

        //then
        assertThat(result.size(),is(2));
        assertThat(result,hasItems(student1,student3));
        assertThat(result.contains(student2), is(false));
    }

    @Test
    public void findByLastnameReturnsMatchingStudentsCaseInsensitive(){
        //given
        Student student1 = prepareStudent("firstName", "SecondName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "secondname", Gender.FEMALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        //when
        List<Student> result = dao.findAllByLastName("secondName");

        //then
        assertThat(result.size(),is(2));
        assertThat(result,hasItems(student1,student3));
        assertThat(result.contains(student2), is(false));
    }

    @Test
    public void findByLastNameWithNullParameterReturnsAllEntities(){
        //given
        Student student1 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.FEMALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        //when
        List<Student> result = dao.findAllByLastName(null);

        //then
        assertThat(result.size(),is(3));
        assertThat(result,hasItems(student1,student2,student3));
    }

    @Test
    public void findByGenderReturnsMatchingStudents(){
        //given
        Student student1 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.MALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "secondName", Gender.MALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        manager.clear();

        //when
        List<Student> result = dao.findAllByGender(Gender.MALE);

        //then
        assertThat(result,hasItems(student2,student3));
    }

    @Test
    public void findByGenderWithNullParameterReturnsAnEmptyList(){
        //given
        Student student1 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1978");
        Student student2 = prepareStudent("firstName", "lastName", Gender.MALE, "13.05.1978");
        Student student3 = prepareStudent("firstName", "secondName", Gender.MALE, "13.05.1978");

        create(student1);
        create(student2);
        create(student3);

        //when
        List<Student> result = dao.findAllByGender(null);

        //then
        assertThat(result.isEmpty(),is(true));
    }

    @Test
    public void findAllBornBeforeReturnsMatchingEntities(){
        //given
        Student student1 = prepareStudent("firstName", "secondName", Gender.FEMALE, "13.05.1975");
        Student student2 = prepareStudent("firstName", "lastName", Gender.MALE, "13.05.1979");
        Student student3 = prepareStudent("firstName", "secondName", Gender.MALE, "13.05.1981");
        Student student4 = prepareStudent("firstName", "secondName", Gender.MALE, null);

        create(student1);
        create(student2);
        create(student3);
        create(student4);

        //then
        assertThat(dao.findAllBornBefore(1990).size(),is(3));
        assertThat(dao.findAllBornBefore(1990),hasItems(student1,student2,student3));

        assertThat(dao.findAllBornBefore(1980).size(),is(2));
        assertThat(dao.findAllBornBefore(1980),hasItems(student1,student2));

        assertThat(dao.findAllBornBefore(1981).size(),is(2));
        assertThat(dao.findAllBornBefore(1980),hasItems(student1,student2));

        assertThat(dao.findAllBornBefore(1970).isEmpty(),is(true));
    }

    //</editor-fold>
}

