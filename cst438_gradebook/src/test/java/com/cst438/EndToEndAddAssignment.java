package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import java.text.DateFormat;  
import java.text.SimpleDateFormat; 
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;


@SpringBootTest
public class EndToEndAddAssignment {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/emranarsala/desktop/chromedriver";

	public static final String URL = "http://localhost:3000";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_COURSE_ID = "99999";
	public static final String TEST_ASSIGNMENT_DueDate = "2022-11-01";
	public static final String TEST_STUDENT_NAME = "Test";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;
	
	
	@Test
	public void addNewAssignment() throws Exception {
		
		Course c = new Course();	
		c.setCourse_id(99999);
		c.setInstructor(TEST_INSTRUCTOR_EMAIL);
		c.setSemester("fall");
		c.setYear(2021);
		c.setTitle(TEST_COURSE_TITLE);

		courseRepository.save(c);
		
		Assignment addedAssignment = new Assignment();

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on
		
		/*
		 * initialize the WebDriver and get the home page. 
		 */

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get(URL);
		Thread.sleep(SLEEP_DURATION);

	

		try {

			/*
			 *  Locate addNewAssignment button and click
			 */
			driver.findElement(By.xpath("(//a)[last()]")).click();
			Thread.sleep(SLEEP_DURATION);
			
			
			//Find input with id courseId and enter the course id
			driver.findElement(By.xpath("//input[@name='courseId']")).sendKeys(TEST_COURSE_ID);
			Thread.sleep(SLEEP_DURATION);
			
			//Find input with id assignmentName and enter the assignment name
			driver.findElement(By.xpath("//input[@name='assignmentName']")).sendKeys(TEST_ASSIGNMENT_NAME);
			Thread.sleep(SLEEP_DURATION);
			
			//Find input with id dueDate and enter the assignment due date
			driver.findElement(By.xpath("//input[@name='duedate']")).sendKeys(TEST_ASSIGNMENT_DueDate);
			Thread.sleep(SLEEP_DURATION);
			
			//Locate the submit button and click 
			driver.findElement(By.xpath("//a")).click();
			Thread.sleep(SLEEP_DURATION);
			
			
			
			int course_id = Integer.valueOf(TEST_COURSE_ID);
		
			/*
			 * Get a list of the all assignments for the test course from the assignment repository 
			 * Check if test assignment was found
			 * 
			 */
			List<Assignment> assignments = assignmentRepository.findAllAssignments(TEST_INSTRUCTOR_EMAIL, course_id);
			
			assertNotNull(assignments, "Assignments not found in database.");
			
			boolean found = false;
			if (assignments != null) {
			for(Assignment in: assignments) {
				//check if assignment name and course id match match with the test data
				if(in.getName().equals(TEST_ASSIGNMENT_NAME) && in.getCourse().getCourse_id() == Integer.valueOf(TEST_COURSE_ID)) {
						addedAssignment = in;
						found = true;		
				}
				
			}
			}
			
			
			//Check if test assignment was found. 
			assertTrue(found, "Unable to locate new TEST ASSIGNMENT in list of assignments.");
			
		

		} catch (Exception ex) {
			throw ex;
		} finally {

			/*
			 *  clean up database so the test is repeatable.
			 */	
			
			if (assignmentRepository.findAllAssignments(TEST_INSTRUCTOR_EMAIL, Integer.valueOf(TEST_COURSE_ID)) != null){
				assignmentRepository.delete(addedAssignment);
				courseRepository.delete(c);			
			}


			driver.quit();
		}
		
		
		
		
		
		
		
		
	}


}
