package ozone.com.eproz.conroler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class ControllerPageableSanitizationTest {

    @Test
    void userControllerSanitizesSortToUsername() throws Exception {
        Pageable input = PageRequest.of(0, 10, Sort.by("email").descending());
        Pageable sanitized = invokeSanitize(new UserController(null), "username", input);

        assertNotNull(sanitized);
        assertEquals("username", sanitized.getSort().getOrderFor("username").getProperty());
    }

    @Test
    void roleControllerSanitizesSortToName() throws Exception {
        Pageable input = PageRequest.of(0, 10, Sort.by("description").descending());
        Pageable sanitized = invokeSanitize(new RoleController(null), "name", input);

        assertNotNull(sanitized);
        assertEquals("name", sanitized.getSort().getOrderFor("name").getProperty());
    }

    @Test
    void staffControllerSanitizesSortToFname() throws Exception {
        Pageable input = PageRequest.of(0, 10, Sort.by("email").descending());
        Pageable sanitized = invokeSanitize(new StaffControler(null), "fname", input);

        assertNotNull(sanitized);
        assertEquals("fname", sanitized.getSort().getOrderFor("fname").getProperty());
    }

    @Test
    void nextOfKinControllerSanitizesSortToFullName() throws Exception {
        Pageable input = PageRequest.of(0, 10, Sort.by("email").descending());
        Pageable sanitized = invokeSanitize(new NextOfKinController(null), "fullName", input);

        assertNotNull(sanitized);
        assertEquals("fullName", sanitized.getSort().getOrderFor("fullName").getProperty());
    }

    @Test
    void trainingControllerSanitizesSortToTitle() throws Exception {
        Pageable input = PageRequest.of(0, 10, Sort.by("provider").descending());
        Pageable sanitized = invokeSanitize(new TrainingController(null), "title", input);

        assertNotNull(sanitized);
        assertEquals("title", sanitized.getSort().getOrderFor("title").getProperty());
    }

    private Pageable invokeSanitize(Object controller, String defaultProperty, Pageable pageable) throws Exception {
        Method method = controller.getClass().getDeclaredMethod("sanitizePageable", Pageable.class, String.class);
        method.setAccessible(true);
        return (Pageable) method.invoke(controller, pageable, defaultProperty);
    }
}
