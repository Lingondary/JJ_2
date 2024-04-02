package ru.gb.lesson4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import javax.persistence.Query;
import java.util.List;

public class Main {
    private static final SessionFactory sessionFactory;


    static {
        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String[] args) {
        Student student = new Student();
        student.setFirstName("John");
        student.setSecondName("Doe");
        student.setAge(25);

        // Пример Persist
        persistStudent(student);

        // Пример Find
        Student foundStudent = findStudentById(1);
        System.out.println("Found Student: " + foundStudent.getFirstName() + " " + foundStudent.getSecondName());

        // Пример Merge
        foundStudent.setAge(30);
        mergeStudent(foundStudent);

        // Пример Remove
        removeStudent(foundStudent);

        // Пример запроса поиска студентов старше 20 лет
        List<Student> studentsOlderThan20 = findStudentsOlderThan(20);
        System.out.println("Students older than 20:");
        for (Student s : studentsOlderThan20) {
            System.out.println(s.getFirstName() + " " + s.getSecondName());
        }

        sessionFactory.close();
    }

    private static void persistStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.persist(student);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private static Student findStudentById(int id) {
        Session session = sessionFactory.openSession();
        try {
            return session.get(Student.class, id);
        } finally {
            session.close();
        }
    }

    private static void mergeStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(student);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private static void removeStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.remove(student);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    private static List<Student> findStudentsOlderThan(int age) {
        Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery("FROM Student s WHERE s.age > :age");
            query.setParameter("age", age);
            return query.getResultList();
        } finally {
            session.close();
        }
    }
}

