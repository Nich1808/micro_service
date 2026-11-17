package kh.edu.istad.platform.iaminfrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientDeviceInfoTest {

    private static final String CHROME_WINDOWS =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";

    @Test
    void parsesDeviceBrowserAndOperatingSystem() {
        ClientDeviceInfo info = ClientDeviceInfo.from(request(CHROME_WINDOWS, "203.0.113.9"));

        assertEquals("Desktop", info.device());
        assertEquals("Chrome", info.browser());
        assertEquals("Windows", info.operatingSystem());
        assertEquals("203.0.113.9", info.ipAddress());
    }

    @Test
    void detectsMobileClients() {
        ClientDeviceInfo info = ClientDeviceInfo.from(request(
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
                        + "AppleWebKit/605.1.15 Version/17.0 Mobile/15E148 Safari/604.1",
                "203.0.113.10"
        ));

        assertEquals("Mobile", info.device());
        assertEquals("Safari", info.browser());
        assertEquals("iOS", info.operatingSystem());
    }

    @Test
    void prefersForwardedAddressOverRemoteAddress() {
        MockHttpServletRequest request = request(CHROME_WINDOWS, "10.0.0.1");
        request.addHeader("X-Forwarded-For", "198.51.100.7, 10.0.0.1");

        assertEquals("198.51.100.7", ClientDeviceInfo.from(request).ipAddress());
    }

    @Test
    void loopbackAddressIsLabelledLocalNetwork() {
        assertEquals("Local network",
                ClientDeviceInfo.from(request(CHROME_WINDOWS, "127.0.0.1")).location());
        assertEquals("Local network",
                ClientDeviceInfo.from(request(CHROME_WINDOWS, "0:0:0:0:0:0:0:1")).location());
    }

    @Test
    void providerCountryHeaderWinsOverAddressGuess() {
        MockHttpServletRequest request = request(CHROME_WINDOWS, "127.0.0.1");
        request.addHeader("CF-IPCountry", "kh");

        assertEquals("KH", ClientDeviceInfo.from(request).location());
    }

    @Test
    void unknownPublicAddressStaysNull() {
        assertNull(ClientDeviceInfo.from(
                request(CHROME_WINDOWS, "203.0.113.9")).location());
    }

    @Test
    void privateRangesAreRecognised() {
        assertTrue(ClientDeviceInfo.isPrivateOrLoopback("10.1.2.3"));
        assertTrue(ClientDeviceInfo.isPrivateOrLoopback("192.168.0.5"));
        assertTrue(ClientDeviceInfo.isPrivateOrLoopback("172.16.4.4"));
        assertTrue(ClientDeviceInfo.isPrivateOrLoopback("172.31.255.1"));
        assertFalse(ClientDeviceInfo.isPrivateOrLoopback("172.32.0.1"));
        assertFalse(ClientDeviceInfo.isPrivateOrLoopback("203.0.113.9"));
    }

    @Test
    void sessionKeyIsStableForTheSameClient() {
        ClientDeviceInfo first = ClientDeviceInfo.from(
                request(CHROME_WINDOWS, "203.0.113.9"));
        ClientDeviceInfo second = ClientDeviceInfo.from(
                request(CHROME_WINDOWS, "203.0.113.9"));

        assertEquals(first.sessionKey("accuser"), second.sessionKey("accuser"));
    }

    @Test
    void sessionKeyDiffersPerDevice() {
        ClientDeviceInfo desktop = ClientDeviceInfo.from(
                request(CHROME_WINDOWS, "203.0.113.9"));
        ClientDeviceInfo mobile = ClientDeviceInfo.from(
                request("Mozilla/5.0 (iPhone) Mobile Safari/604.1", "203.0.113.9"));

        assertFalse(desktop.sessionKey("accuser").equals(mobile.sessionKey("accuser")));
    }

    private static MockHttpServletRequest request(String userAgent, String remoteAddress) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", userAgent);
        request.setRemoteAddr(remoteAddress);
        return request;
    }
}
