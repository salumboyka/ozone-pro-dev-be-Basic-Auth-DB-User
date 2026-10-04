package ozone.com.eproz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EprozApplicationTests {

	@Autowired
	private AuditorAware<String> auditorAware;

	@Autowired
	private MockMvc mockMvc;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void actuatorHealthEndpointIsAvailable() throws Exception {
		mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
	}

	@Test
	void usesAuthenticatedUsernameAsAuditor() {
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new UsernamePasswordAuthenticationToken("test-user", "password",
				List.of()));
		SecurityContextHolder.setContext(context);

		assertEquals("test-user", auditorAware.getCurrentAuditor().orElseThrow());
	}

	@Test
	void usesSystemAsAuditorWithoutAuthenticatedUser() {
		assertEquals("system", auditorAware.getCurrentAuditor().orElseThrow());
	}

	@Test
	void usesSystemAsAuditorForAnonymousAuthentication() {
		var context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(new AnonymousAuthenticationToken("test-key", "anonymousUser",
				AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
		SecurityContextHolder.setContext(context);

		assertEquals("system", auditorAware.getCurrentAuditor().orElseThrow());
	}

}
