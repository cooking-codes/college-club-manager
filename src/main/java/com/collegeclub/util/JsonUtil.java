package com.collegeclub.util;

import com.collegeclub.model.Club;
import com.collegeclub.model.Event;
import com.collegeclub.model.Student;
import com.collegeclub.model.ClubRegistration;
import com.collegeclub.model.EventRegistration;

import java.util.List;

/**
 * Pure Java Lightweight JSON Serializer.
 * Avoids heavy 3rd-party dependencies and frameworks while allowing clean
 * JSON communication between Jakarta Servlets and the JavaScript frontend.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - hex.length(); k++) sb.append('0');
                        sb.append(hex);
                    } else {
                        sb.append(ch);
                    }
            }
        }
        return sb.toString();
    }

    public static String toJson(Club club) {
        if (club == null) return "null";
        return "{" +
                "\"id\":" + club.getId() + "," +
                "\"name\":\"" + escape(club.getName()) + "\"," +
                "\"category\":\"" + escape(club.getCategory()) + "\"," +
                "\"description\":\"" + escape(club.getDescription()) + "\"," +
                "\"objectives\":\"" + escape(club.getObjectives()) + "\"," +
                "\"activities\":\"" + escape(club.getActivities()) + "\"," +
                "\"facultyCoordinator\":\"" + escape(club.getFacultyCoordinator()) + "\"," +
                "\"studentCoordinator\":\"" + escape(club.getStudentCoordinator()) + "\"," +
                "\"meetingDay\":\"" + escape(club.getMeetingDay()) + "\"," +
                "\"meetingTime\":\"" + escape(club.getMeetingTime()) + "\"," +
                "\"location\":\"" + escape(club.getLocation()) + "\"," +
                "\"contactEmail\":\"" + escape(club.getContactEmail()) + "\"," +
                "\"memberCount\":" + club.getMemberCount() +
                "}";
    }

    public static String clubListToJson(List<Club> clubs) {
        if (clubs == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < clubs.size(); i++) {
            sb.append(toJson(clubs.get(i)));
            if (i < clubs.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toJson(Event event) {
        if (event == null) return "null";
        String eventDateStr = (event.getEventDate() != null) ? event.getEventDate().toString() : "";
        String deadlineStr = (event.getRegistrationDeadline() != null) ? event.getRegistrationDeadline().toString() : "";
        return "{" +
                "\"id\":" + event.getId() + "," +
                "\"eventName\":\"" + escape(event.getEventName()) + "\"," +
                "\"description\":\"" + escape(event.getDescription()) + "\"," +
                "\"clubId\":" + event.getClubId() + "," +
                "\"clubName\":\"" + escape(event.getClubName()) + "\"," +
                "\"eventDate\":\"" + escape(eventDateStr) + "\"," +
                "\"eventTime\":\"" + escape(event.getEventTime()) + "\"," +
                "\"venue\":\"" + escape(event.getVenue()) + "\"," +
                "\"registrationDeadline\":\"" + escape(deadlineStr) + "\"," +
                "\"maxParticipants\":" + event.getMaxParticipants() + "," +
                "\"status\":\"" + escape(event.getStatus()) + "\"," +
                "\"registeredCount\":" + event.getRegisteredCount() + "," +
                "\"isFull\":" + event.isFull() + "," +
                "\"isClosed\":" + event.isRegistrationClosed() +
                "}";
    }

    public static String eventListToJson(List<Event> events) {
        if (events == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            sb.append(toJson(events.get(i)));
            if (i < events.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String statsToJson(java.util.Map<String, Integer> stats) {
        if (stats == null) return "{}";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (java.util.Map.Entry<String, Integer> entry : stats.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":").append(entry.getValue() != null ? entry.getValue() : 0);
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static String toJson(ClubRegistration cr) {
        if (cr == null) return "null";
        String dateStr = (cr.getRegistrationDate() != null) ? cr.getRegistrationDate().toString() : "";
        return "{" +
                "\"id\":" + cr.getId() + "," +
                "\"clubId\":" + cr.getClubId() + "," +
                "\"clubName\":\"" + escape(cr.getClubName()) + "\"," +
                "\"clubCategory\":\"" + escape(cr.getClubCategory()) + "\"," +
                "\"registrationDate\":\"" + escape(dateStr) + "\"," +
                "\"status\":\"" + escape(cr.getStatus()) + "\"," +
                "\"studentName\":\"" + escape(cr.getStudentName()) + "\"," +
                "\"studentRollNumber\":\"" + escape(cr.getStudentRollNumber()) + "\"," +
                "\"studentDepartment\":\"" + escape(cr.getStudentDepartment()) + "\"" +
                "}";
    }

    public static String clubRegListToJson(List<ClubRegistration> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String toJson(EventRegistration er) {
        if (er == null) return "null";
        String regDateStr = (er.getRegistrationDate() != null) ? er.getRegistrationDate().toString() : "";
        String evDateStr = (er.getEventDate() != null) ? er.getEventDate().toString() : "";
        return "{" +
                "\"id\":" + er.getId() + "," +
                "\"eventId\":" + er.getEventId() + "," +
                "\"eventName\":\"" + escape(er.getEventName()) + "\"," +
                "\"organizingClubName\":\"" + escape(er.getOrganizingClubName()) + "\"," +
                "\"eventDate\":\"" + escape(evDateStr) + "\"," +
                "\"eventTime\":\"" + escape(er.getEventTime()) + "\"," +
                "\"venue\":\"" + escape(er.getEventVenue()) + "\"," +
                "\"registrationDate\":\"" + escape(regDateStr) + "\"," +
                "\"status\":\"" + escape(er.getStatus()) + "\"," +
                "\"studentName\":\"" + escape(er.getStudentName()) + "\"," +
                "\"studentRollNumber\":\"" + escape(er.getStudentRollNumber()) + "\"," +
                "\"studentDepartment\":\"" + escape(er.getStudentDepartment()) + "\"" +
                "}";
    }

    public static String eventRegListToJson(List<EventRegistration> list) {
        if (list == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append(toJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String studentRegistrationsToJson(List<ClubRegistration> clubs, List<EventRegistration> events) {
        return "{" +
                "\"clubMemberships\":" + clubRegListToJson(clubs) + "," +
                "\"eventRegistrations\":" + eventRegListToJson(events) +
                "}";
    }

    public static String responseJson(boolean success, String message) {
        return "{\"success\":" + success + ",\"message\":\"" + escape(message) + "\"}";
    }

    public static String responseJson(boolean success, String message, String dataJson) {
        return "{\"success\":" + success + ",\"message\":\"" + escape(message) + "\",\"data\":" + (dataJson != null ? dataJson : "null") + "}";
    }
}
