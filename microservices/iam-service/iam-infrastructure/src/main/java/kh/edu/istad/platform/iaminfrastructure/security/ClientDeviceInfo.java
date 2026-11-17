package kh.edu.istad.platform.iaminfrastructure.security;

import jakarta.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

/**
 * Best-effort description of the client behind a request: device class, browser and
 * operating system derived from the User-Agent, plus the caller's IP address.
 */
public record ClientDeviceInfo(
        String device,
        String browser,
        String operatingSystem,
        String ipAddress,
        String location,
        String userAgent
) {

    public static ClientDeviceInfo from(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = ipAddressOf(request);

        return new ClientDeviceInfo(
                deviceOf(userAgent),
                browserOf(userAgent),
                operatingSystemOf(userAgent),
                ipAddress,
                locationOf(request, ipAddress),
                userAgent != null && userAgent.length() > 500
                        ? userAgent.substring(0, 500)
                        : userAgent
        );
    }

    /**
     * Physical location is not resolvable from an IP address without a geo database, so
     * the value comes from whichever signal the deployment provides:
     * <ol>
     *   <li>CDN headers ({@code CF-IPCountry}, {@code X-Country-Code}) when the service
     *       sits behind a proxy that reports it;</li>
     *   <li>otherwise private/loopback addresses are labelled "Local network".</li>
     * </ol>
     * Unknown public addresses stay {@code null} rather than being guessed.
     */
    static String locationOf(HttpServletRequest request, String ipAddress) {
        String country = firstNonBlank(
                request.getHeader("CF-IPCountry"),
                request.getHeader("X-Country-Code")
        );

        if (country != null && !"XX".equalsIgnoreCase(country)) {
            return country.toUpperCase(Locale.ROOT);
        }

        if (isPrivateOrLoopback(ipAddress)) {
            return "Local network";
        }

        return null;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }

        return null;
    }

    static boolean isPrivateOrLoopback(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return false;
        }

        String ip = ipAddress.trim();

        if (ip.startsWith("::1") || ip.startsWith("0:0:0:0:0:0:0:1")) {
            return true;
        }

        if (ip.startsWith("10.") || ip.startsWith("192.168.")
                || ip.startsWith("127.") || ip.startsWith("169.254.")) {
            return true;
        }

        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");

            if (parts.length > 1) {
                try {
                    int second = Integer.parseInt(parts[1]);
                    return second >= 16 && second <= 31;
                } catch (NumberFormatException ignored) {
                    return false;
                }
            }
        }

        return ip.startsWith("fc") || ip.startsWith("fd") || ip.startsWith("fe80");
    }

    /**
     * Stable key for "this user on this device at this address", so repeated requests do
     * not create a new session row.
     */
    public String sessionKey(String username) {
        String source = username + "|" + userAgent + "|" + ipAddress;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            return Integer.toHexString(source.hashCode());
        }
    }

    static String deviceOf(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        if (ua.contains("bot") || ua.contains("crawler") || ua.contains("spider")) {
            return "Bot";
        }

        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "Tablet";
        }

        if (ua.contains("mobile") || ua.contains("iphone")
                || ua.contains("android")) {
            return "Mobile";
        }

        return "Desktop";
    }

    static String browserOf(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        if (ua.contains("edg/")) {
            return "Edge";
        }

        if (ua.contains("opr/") || ua.contains("opera")) {
            return "Opera";
        }

        if (ua.contains("chrome") && !ua.contains("chromium")) {
            return "Chrome";
        }

        if (ua.contains("firefox")) {
            return "Firefox";
        }

        if (ua.contains("safari")) {
            return "Safari";
        }

        if (ua.contains("postman") || ua.contains("httpclient")) {
            return "API client";
        }

        return "Unknown";
    }

    static String operatingSystemOf(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }

        String ua = userAgent.toLowerCase(Locale.ROOT);

        if (ua.contains("windows")) {
            return "Windows";
        }

        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) {
            return "iOS";
        }

        if (ua.contains("mac os") || ua.contains("macintosh")) {
            return "macOS";
        }

        if (ua.contains("android")) {
            return "Android";
        }

        if (ua.contains("linux")) {
            return "Linux";
        }

        return "Unknown";
    }

    static String ipAddressOf(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            String first = comma > 0 ? forwarded.substring(0, comma) : forwarded;
            String address = first.trim();

            if (!address.isEmpty() && address.length() <= 45) {
                return address;
            }
        }

        String remote = request.getRemoteAddr();

        if (remote != null && remote.length() > 45) {
            return remote.substring(0, 45);
        }

        return remote;
    }
}
