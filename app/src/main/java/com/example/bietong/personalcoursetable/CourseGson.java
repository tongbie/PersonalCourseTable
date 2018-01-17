package com.example.bietong.personalcoursetable;

import java.util.List;

/**
 * Created by aaa on 2017/9/29.
 * 个人课表 Gson 类
 */

public class CourseGson {
    private String studentNum="";
    private String studentName="";
    private String termStartTime="";

    private List<DayCourse> mondayCourse;
    private List<DayCourse> tuesdayCourse;
    private List<DayCourse> wednesdayCourse;
    private List<DayCourse> thursdayCourse;
    private List<DayCourse> fridayCourse;
    private List<DayCourse> saturdayCourse;
    private List<DayCourse> sundayCourse;

    public static class DayCourse {
        private String courseName="";
        private String courseWeek="";
        private String coursePlace="";
        private String courseTeacher="";

        public DayCourse(String courseName,String courseWeek,String coursePlace,String courseTeacher){//构造方法？
            super();
            this.courseName=courseName;
            this.courseWeek=courseWeek;
            this.coursePlace=coursePlace;
            this.courseTeacher=courseTeacher;
        }

        public class OtherCourse {
            private String courseName;
            private String courseWeek;
            private String coursePlace;
            private String courseTeacher;

            public void setCourseName(String courseName) {
                this.courseName = courseName;
            }

            public String getCourseName() {
                return courseName;
            }

            public String getCourseWeek() {
                return courseWeek;
            }

            public void setCourseWeek(String courseWeek) {
                this.courseWeek = courseWeek;
            }

            public String getCoursePlace() {
                return coursePlace;
            }

            public void setCoursePlace(String coursePlace) {
                this.coursePlace = coursePlace;
            }

            public String getCourseTeacher() {
                return courseTeacher;
            }

            public void setCourseTeacher(String courseTeacher) {
                this.courseTeacher = courseTeacher;
            }
        }

        /*public void setOtherCourse(DayCourse OtherCourse){
            this.OtherCourse=OtherCourse;
        }

        public DayCourse getOtherCourse(){
            return OtherCourse;
        }*/

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseWeek(String courseWeek) {
            this.courseWeek = courseWeek;
        }

        public String getCourseWeek() {
            return courseWeek;
        }

        public void setCoursePlace(String coursePlace) {
            this.coursePlace = coursePlace;
        }

        public String getCoursePlace() {
            return coursePlace;
        }

        public void setCourseTeacher(String courseTeacher) {
            this.courseTeacher = courseTeacher;
        }

        public String getCourseTeacher() {
            return courseTeacher;
        }
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setMondayCourse(List<DayCourse> mondayCourse) {
        this.mondayCourse = mondayCourse;
    }

    public List<DayCourse> getMondayCourse() {
        return mondayCourse;
    }

    public void setTuesdayCourse(List<DayCourse> tuesdayCourse) {
        this.tuesdayCourse = tuesdayCourse;
    }

    public List<DayCourse> getTuesdayCourse() {
        return tuesdayCourse;
    }

    public void setWednesdayCourse(List<DayCourse> wednesdayCourse) {
        this.wednesdayCourse = wednesdayCourse;
    }

    public List<DayCourse> getWednesdayCourse() {
        return wednesdayCourse;
    }

    public void setThursdayCourse(List<DayCourse> thursdayCourse) {
        this.thursdayCourse = thursdayCourse;
    }

    public List<DayCourse> getThursdayCourse() {
        return thursdayCourse;
    }

    public void setFridayCourse(List<DayCourse> fridayCourse) {
        this.fridayCourse = fridayCourse;
    }

    public List<DayCourse> getFridayCourse() {
        return fridayCourse;
    }

    public void setSaturdayCourse(List<DayCourse> saturdayCourse) {
        this.saturdayCourse = saturdayCourse;
    }

    public List<DayCourse> getSaturdayCourse() {
        return saturdayCourse;
    }

    public void setSundayCourse(List<DayCourse> sundayCourse) {
        this.sundayCourse = sundayCourse;
    }

    public List<DayCourse> getSundayCourse() {
        return sundayCourse;
    }

    public void setTermStartTime(String termStartTime) {
        this.termStartTime = termStartTime;
    }

    public String getTermStartTime() {
        return termStartTime;
    }
}
