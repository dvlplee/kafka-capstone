package com.chacha.util;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class ClientInfoUtils {

    private static String geoipKey;

    @Value("${geoip-key}")
    public void setGeoipKey(String geoipKey) {
        ClientInfoUtils.geoipKey = geoipKey;
    }

    // 클라이언트의 IP 주소를 가져오는 메서드
    public static String getClientIp(HttpServletRequest request) throws UnknownHostException {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            /* Private IP 가져오기
             InetAddress address = InetAddress.getLocalHost();
             ip = address.getHostAddress();
            */

            // Public IP를 가져오기
            try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://ifconfig.me/ip").openStream(), "UTF-8").useDelimiter("\\A")) {
                ip = s.hasNext() ? s.next() : "Unknown IP";
            } catch (IOException e) {
                ip = "Unknown IP";
                e.printStackTrace();
            }
        }
        System.out.println("Client's current IP address is " + ip);
        return ip;
    }

    // IP 주소로 접속 위치를 가져오는 메서드
    public static String getClientLocation(String ip) {
        try {
            WebServiceClient client = new WebServiceClient.Builder(1094094, geoipKey).host("geolite.info").build();
            InetAddress ipAddress = InetAddress.getByName(ip);

            CountryResponse countryRes = client.country(ipAddress);
            String country = countryRes.getCountry().getName();

            CityResponse cityRes = client.city(ipAddress);
            String city = cityRes.getCity().getName();

            return city + ", " + country;

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
    }

    // User-Agent에서 운영 체제를 탐지하는 메서드
    public static String detectOS(String userAgent) {
        if (userAgent.contains("Windows NT 10.0")) {
            return "Windows 10";
        } else if (userAgent.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("Windows NT 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("Windows NT 6.1")) {
            return "Windows 7";
        } else if (userAgent.contains("Android")) {
            return "Android";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS";
        } else if (userAgent.contains("Macintosh")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        }  else {
            return "Unknown OS";
        }
    }

    // User-Agent에서 브라우저를 탐지하는 메서드
    public static String detectBrowser(String userAgent) {
        if (userAgent.contains("Edg/")) {
            return "Edge"; // Microsoft Edge
        } else if (userAgent.contains("SamsungBrowser")) {
            return "SamsungBrowser"; // Opera 브라우저
        } else if (userAgent.contains("OPR/") || userAgent.contains("Opera")) {
            return "Opera"; // Opera 브라우저
        } else if (userAgent.contains("Chrome") && !userAgent.contains("Edg/") && !userAgent.contains("OPR/")) {
            return "Chrome"; // Google Chrome
        } else if (userAgent.contains("Firefox")) {
            return "Firefox"; // Mozilla Firefox
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari"; // Apple Safari
        } else if (userAgent.contains("Trident/") || userAgent.contains("MSIE")) {
            return "Internet Explorer"; // 이전 버전의 Internet Explorer
        } else {
            return "Unknown Browser"; // 기타 브라우저
        }
    }
}
