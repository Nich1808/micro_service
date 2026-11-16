package kh.edu.istad.platform.iaminfrastructure;

import kh.edu.istad.platform.iaminfrastructure.federation.FederatedPrincipalFactory;
import kh.edu.istad.platform.iaminfrastructure.security.BCryptPasswordHasher;
import kh.edu.istad.platform.iamdomaincore.enums.IdentityProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IamInfrastructureApplicationTests {

    @Test
    void bcryptHasherRoundTrips() {
        BCryptPasswordHasher hasher = new BCryptPasswordHasher();

        String encoded = hasher.hash("Str0ng!Pass2026");

        assertTrue(hasher.matches("Str0ng!Pass2026", encoded));
        assertFalse(hasher.matches("wrong-password", encoded));
    }

    @Test
    void registrationIdsMapToProviders() {
        assertEquals(IdentityProvider.GOOGLE,
                FederatedPrincipalFactory.providerOf("google"));
        assertEquals(IdentityProvider.GITHUB,
                FederatedPrincipalFactory.providerOf("github"));
        assertEquals(IdentityProvider.FACEBOOK,
                FederatedPrincipalFactory.providerOf("facebook"));
        assertEquals(IdentityProvider.TELEGRAM,
                FederatedPrincipalFactory.providerOf("telegram"));

        assertThrows(IllegalStateException.class,
                () -> FederatedPrincipalFactory.providerOf("unknown"));
    }
}
