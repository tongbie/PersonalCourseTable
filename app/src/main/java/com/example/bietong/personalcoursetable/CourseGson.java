package com.example.bietong.personalcoursetable;

import java.util.ArrayList;
import java.util.List;

public class CourseGson {
//    private String studentNum="";
//    private String studentName="";
//    private String termStartTime="";

    private List<DayCourse> mondayCourse;
    private List<DayCourse> tuesdayCourse;
    private List<DayCourse> wednesdayCourse;
    private List<DayCourse> thursdayCourse;
    private List<DayCourse> fridayCourse;
    private List<DayCourse> saturdayCourse;
    private List<DayCourse> sundayCourse;

    public List<List<DayCourse>> getWeekCourseList() {
        List<List<DayCourse>> weekCourseList = new ArrayList<>();
        weekCourseList.add(getMondayCourse());
        weekCourseList.add(getTuesdayCourse());
        weekCourseList.add(getWednesdayCourse());
        weekCourseList.add(getThursdayCourse());
        weekCourseList.add(getFridayCourse());
        weekCourseList.add(getSaturdayCourse());
        weekCourseList.add(getSundayCourse());
        return weekCourseList;
    }

    public static class DayCourse {
        private String courseName = "";
        private String courseWeek = "";
        private String coursePlace = "";
        private String courseTeacher = "";

        public DayCourse(String courseName, String courseWeek, String coursePlace, String courseTeacher) {
            super();
            this.courseName = courseName;
            this.courseWeek = courseWeek;
            this.coursePlace = coursePlace;
            this.courseTeacher = courseTeacher;
        }

//        public class OtherCourse {
//            private String courseName;
//            private String courseWeek;
//            private String coursePlace;
//            private String courseTeacher;
//
//            public void setCourseName(String courseName) {
//                this.courseName = courseName;
//            }
//
//            public String getCourseName() {
//                return courseName;
//            }
//
//            public String getCourseWeek() {
//                return courseWeek;
//            }
//
//            public void setCourseWeek(String courseWeek) {
//                this.courseWeek = courseWeek;
//            }
//
//            public String getCoursePlace() {
//                return coursePlace;
//            }
//
//            public void setCoursePlace(String coursePlace) {
//                this.coursePlace = coursePlace;
//            }
//
//            public String getCourseTeacher() {
//                return courseTeacher;
//            }
//
//            public void setCourseTeacher(String courseTeacher) {
//                this.courseTeacher = courseTeacher;
//            }
//        }
//
//        public void setOtherCourse(DayCourse OtherCourse){
//            this.OtherCourse=OtherCourse;
//        }
//
//        public DayCourse getOtherCourse(){
//            return OtherCourse;
//        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseName() {
            if (courseName == null) {
                courseName = "";
            }
            return courseName;
        }

        public void setCourseWeek(String courseWeek) {
            this.courseWeek = courseWeek;
        }

        public String getCourseWeek() {
            if (courseWeek == null) {
                courseWeek = "";
            }
            return courseWeek;
        }

        public void setCoursePlace(String coursePlace) {
            this.coursePlace = coursePlace;
        }

        public String getCoursePlace() {
            if (coursePlace == null) {
                coursePlace = "";
            }
            return coursePlace;
        }

        public void setCourseTeacher(String courseTeacher) {
            this.courseTeacher = courseTeacher;
        }

        public String getCourseTeacher() {
            if (courseTeacher == null) {
                courseTeacher = "";
            }
            return courseTeacher;
        }
    }

//    public void setStudentNum(String studentNum) {
//        this.studentNum = studentNum;
//    }
//
//    public String getStudentNum() {
//        return studentNum;
//    }
//
//    public void setStudentName(String studentName) {
//        this.studentName = studentName;
//    }
//
//    public String getStudentName() {
//        return studentName;
//    }

    public void setMondayCourse(List<DayCourse> mondayCourse) {
        this.mondayCourse = mondayCourse;
    }

    public List<DayCourse> getMondayCourse() {
        if (mondayCourse == null) {
            mondayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                mondayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return mondayCourse;
    }

    public void setTuesdayCourse(List<DayCourse> tuesdayCourse) {
        this.tuesdayCourse = tuesdayCourse;
    }

    public List<DayCourse> getTuesdayCourse() {
        if (tuesdayCourse == null) {
            tuesdayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                tuesdayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return tuesdayCourse;
    }

    public void setWednesdayCourse(List<DayCourse> wednesdayCourse) {
        this.wednesdayCourse = wednesdayCourse;
    }

    public List<DayCourse> getWednesdayCourse() {
        if (wednesdayCourse == null) {
            wednesdayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                wednesdayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return wednesdayCourse;
    }

    public void setThursdayCourse(List<DayCourse> thursdayCourse) {
        this.thursdayCourse = thursdayCourse;
    }

    public List<DayCourse> getThursdayCourse() {
        if (thursdayCourse == null) {
            thursdayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                thursdayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return thursdayCourse;
    }

    public void setFridayCourse(List<DayCourse> fridayCourse) {
        this.fridayCourse = fridayCourse;
    }

    public List<DayCourse> getFridayCourse() {
        if (fridayCourse == null) {
            fridayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                fridayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return fridayCourse;
    }

    public void setSaturdayCourse(List<DayCourse> saturdayCourse) {
        this.saturdayCourse = saturdayCourse;
    }

    public List<DayCourse> getSaturdayCourse() {
        if (saturdayCourse == null) {
            saturdayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                saturdayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return saturdayCourse;
    }

    public void setSundayCourse(List<DayCourse> sundayCourse) {
        this.sundayCourse = sundayCourse;
    }

    public List<DayCourse> getSundayCourse() {
        if (sundayCourse == null) {
            sundayCourse = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                sundayCourse.add(new DayCourse("", "", "", ""));
            }
        }
        return sundayCourse;
    }

//    public void setTermStartTime(String termStartTime) {
//        this.termStartTime = termStartTime;
//    }
//
//    public String getTermStartTime() {
//        return termStartTime;
//    }
}
