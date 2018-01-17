package com.example.bietong.personalcoursetable;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private Course course[][] = new Course[7][10];
    private LinearLayout.LayoutParams layoutParams;//布局参数
    private LinearLayout[] day = new LinearLayout[7];//每天LinearLayout
    private int _2dp;//相邻Button间隔
    private int _58dp;//按钮高度
    private String JsonString;//课表Json字符串
    private String[][] courseNameArray = new String[7][10];//课程名称
    private String[][] courseWeekArray = new String[7][10];//课程时间
    private String[][] coursePlaceArray = new String[7][10];//课程地点
    private String[][] courseTeacherArray = new String[7][10];//课程教师
    private int[][] courseLength = new int[7][10];//课程长度
    private EditText editName;//课程名称修改框
    private EditText editWeek;//课程时间修改框
    private EditText editPlace;//课程地点修改框
    private EditText editTeacher;//课程教师修改框
    private Button popupButton;//“修改”按钮
    private ScrollView scrollView;//滚动布局，用以确定修改弹窗位置
    private LinearLayout popLinearLayout;//“修改”弹窗动态线性布局
    private PopupWindow popupWindow;//“修改”弹框
    private SwipeRefreshLayout swipeRefreshLayout;//下拉刷新
    private SharedPreferences sharedPreferences;
    private int courseWidth;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setStatusBarColor(Color.parseColor("#7ebeab"));//设置状态栏颜色
        sharedPreferences = getSharedPreferences("PersonalCourseTable", Context.MODE_PRIVATE);
        day[0] = (LinearLayout) findViewById(R.id.day0);
        day[1] = (LinearLayout) findViewById(R.id.day1);
        day[2] = (LinearLayout) findViewById(R.id.day2);
        day[3] = (LinearLayout) findViewById(R.id.day3);
        day[4] = (LinearLayout) findViewById(R.id.day4);
        day[5] = (LinearLayout) findViewById(R.id.day5);
        day[6] = (LinearLayout) findViewById(R.id.day6);
        _2dp = (int) getResources().getDimension(R.dimen._2dp);//相邻Button间隔
        _58dp = (int) getResources().getDimension(R.dimen._58dp);//指定Button高度（res/values/dimens/...）
        courseWidth = (int) (this.getWindowManager().getDefaultDisplay().getWidth() / 7.5 - _2dp);
        /*布局 ->*/
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, _58dp);//布局参数
        layoutParams.setMargins(_2dp, _2dp, 0, 0);//设置边距
        /*<- 布局*/
        /*文件读写 ->*/
        JsonString = sharedPreferences.getString("PersonalCourseTable", null);
        if (JsonString == null) {
            JsonString = getLocalJson();//读取本地JSON
            sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
        }
        addJson();//将JSON中的null填充
        getCourseArray();//获取课表数据
        setCourseLength();//设置课程长度
        try {
            Method method = this.getClass().getDeclaredMethod("addLongCourse", new Class[]{});//{}中为参数类型.class
            Object[] objects = new Object[]{};//获得参数Object
            method.invoke(this);//执行方法
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
//        addLongCourse();//添加长Button
        initPopupWindow();//添加PopupWindow控件
        initRefresh();//添加下拉刷新
    }

    @Override/*按钮点击事件*/
    public void onClick(View view) {
        Course courseButtion = (Course) view;
        int X = courseButtion.X, Y = courseButtion.Y;
        if (!course[X][Y].getText().equals("")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle(courseNameArray[X][Y]);
            dialog.setMessage("课程时间：" + courseWeekArray[X][Y] + "\n上课地点：" + coursePlaceArray[X][Y] + "\n任课教师：" + courseTeacherArray[X][Y]);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override/*按钮长按事件*/
    public boolean onLongClick(View view) {
        Course courseButtion = (Course) view;
        int X = courseButtion.X, Y = courseButtion.Y;
        showPopupMenu(course[X][Y], X, Y);
        return true;//返回值改为true，消费掉该事件，阻止事件向下传递
    }

    /*添加Button*/
    @SuppressLint("WrongConstant")
    private void addButton() {
        LinearLayout.LayoutParams spaceLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, _58dp);//布局参数
        spaceLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);//设置边距
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                course[i][j] = new Course(this, courseWidth, _58dp, courseNameArray[i][j]);
                course[i][j].setLayoutParams(layoutParams);//设置大小
                course[i][j].setPosition(i, j);
                if (j == 4 || j == 8) {//设置分割线
                    course[i][j].setLayoutParams(spaceLayoutParams);
                }
                course[i][j].setOnClickListener(this);//点击事件
                course[i][j].setOnLongClickListener(this);
                if (courseNameArray[i][j].equals("")) {//隐藏无内容Button
                    course[i][j].setNull();
                }
                day[i].addView(course[i][j]);
            }
        }
    }

    /*展示选项卡*/
    private void showPopupMenu(View view, final int X, final int Y) {
        PopupMenu popupMenu = new PopupMenu(this, view);//View当前PopupMenu显示的相对View的位置
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());//menu布局
        /*menu的item点击事件->*/
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "修改":
                        reviseJson(X, Y);
                        break;
                    case "删除":
                        deleteJson(X, Y);
                        break;
                    default:
                }
                return true;
            }
        });
        /*<-menu的item点击事件*/
        popupMenu.show();
    }

    /*本地读取JSON文件（main/assets/...）*/
    private String getLocalJson() {
        String JsonString = "";
        try {
            InputStream is = getAssets().open("coursetable_json.txt");    //此处为要加载的json文件名称
            InputStreamReader reader = new InputStreamReader(is, "GBK");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            JsonString = buffer.toString();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "本地数据异常", Toast.LENGTH_LONG).show();
            exception();
        }
        return JsonString;
    }

    /*获取课表数据，置入courseNameArray*/
    private void getCourseArray() {
        Gson gson = new Gson();
        CourseGson courseGson = gson.fromJson(JsonString, CourseGson.class);
        try {
            for (int i = 0; i < 10; i++) {
                if (courseGson.getMondayCourse() != null) {
                    courseNameArray[0][i] = courseGson.getMondayCourse().get(i).getCourseName();
                    courseWeekArray[0][i] = courseGson.getMondayCourse().get(i).getCourseWeek();
                    coursePlaceArray[0][i] = courseGson.getMondayCourse().get(i).getCoursePlace();
                    courseTeacherArray[0][i] = courseGson.getMondayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getTuesdayCourse() != null) {
                    courseNameArray[1][i] = courseGson.getTuesdayCourse().get(i).getCourseName();
                    courseWeekArray[1][i] = courseGson.getTuesdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[1][i] = courseGson.getTuesdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[1][i] = courseGson.getTuesdayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getWednesdayCourse() != null) {
                    courseNameArray[2][i] = courseGson.getWednesdayCourse().get(i).getCourseName();
                    courseWeekArray[2][i] = courseGson.getWednesdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[2][i] = courseGson.getWednesdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[2][i] = courseGson.getWednesdayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getTuesdayCourse() != null) {
                    courseNameArray[3][i] = courseGson.getThursdayCourse().get(i).getCourseName();
                    courseWeekArray[3][i] = courseGson.getThursdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[3][i] = courseGson.getThursdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[3][i] = courseGson.getThursdayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getFridayCourse() != null) {
                    courseNameArray[4][i] = courseGson.getFridayCourse().get(i).getCourseName();
                    courseWeekArray[4][i] = courseGson.getFridayCourse().get(i).getCourseWeek();
                    coursePlaceArray[4][i] = courseGson.getFridayCourse().get(i).getCoursePlace();
                    courseTeacherArray[4][i] = courseGson.getFridayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getSaturdayCourse() != null) {
                    courseNameArray[5][i] = courseGson.getSaturdayCourse().get(i).getCourseName();
                    courseWeekArray[5][i] = courseGson.getSaturdayCourse().get(i).getCourseWeek();
                    coursePlaceArray[5][i] = courseGson.getSaturdayCourse().get(i).getCoursePlace();
                    courseTeacherArray[5][i] = courseGson.getSaturdayCourse().get(i).getCourseTeacher();
                }
                if (courseGson.getSundayCourse() != null) {
                    courseNameArray[6][i] = courseGson.getSundayCourse().get(i).getCourseName();
                    courseWeekArray[6][i] = courseGson.getSundayCourse().get(i).getCourseWeek();
                    coursePlaceArray[6][i] = courseGson.getSundayCourse().get(i).getCoursePlace();
                    courseTeacherArray[6][i] = courseGson.getSundayCourse().get(i).getCourseTeacher();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "课程数据异常", Toast.LENGTH_LONG).show();
            exception();
        }
    }

    /*修改JSON文件*/
    private void reviseJson(final int X, final int Y) {
        final Gson gson = new Gson();
        final CourseGson courseGson = gson.fromJson(JsonString, CourseGson.class);
        editName.setText(courseNameArray[X][Y]);//显示课程名称
        editName.setHint("课程名称：");//显示提示文字
        editWeek.setText(courseWeekArray[X][Y]);//显示课程时间
        editWeek.setHint("课程时间：");
        editPlace.setText(coursePlaceArray[X][Y]);//显示课程地点
        editPlace.setHint("上课地点：");
        editTeacher.setText(courseTeacherArray[X][Y]);//显示任课教师
        editTeacher.setHint("任课教师：");
        /*点击事件*/
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJson(courseGson, X, Y, editName.getText().toString(), editWeek.getText().toString(), editPlace.getText().toString(), editTeacher.getText().toString());
                JsonString = gson.toJson(courseGson);
                sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
                getCourseArray();
                freeCourse();
                addButton();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(scrollView, Gravity.CENTER, 0, 0);
    }

    /*删除JSON文件*/
    private void deleteJson(int X, int Y) {
        courseNameArray[X][Y] = "";
        courseWeekArray[X][Y] = "";
        coursePlaceArray[X][Y] = "";
        courseTeacherArray[X][Y] = "";
        course[X][Y].setNull();
        com.google.gson.Gson gson = new com.google.gson.Gson();
        CourseGson gsonPersonalCourseTable = gson.fromJson(JsonString, CourseGson.class);
        setJson(gsonPersonalCourseTable, X, Y, "", "", "", "");
        JsonString = gson.toJson(gsonPersonalCourseTable);
        sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
    }

    /*修改和删除JSON文件的中间方法*/
    private void setJson(CourseGson gsonPersonalCourseTable, int X, int Y, String courseName, String courseWeek, String coursePlace, String courseTeacher) {
        try {
            switch (X) {
                case 0:
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getMondayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 1:
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getTuesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 2:
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getWednesdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 3:
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getThursdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 4:
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getFridayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 5:
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getSaturdayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                case 6:
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseName(courseName);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCoursePlace(coursePlace);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseTeacher(courseTeacher);
                    gsonPersonalCourseTable.getSundayCourse().get(Y).setCourseWeek(courseWeek);
                    break;
                default:
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "setJson(...)", Toast.LENGTH_LONG).show();
            exception();
        }
    }

    /*将JSON中的null填充*/
    private void addJson() {
        Gson gson = new Gson();
        CourseGson courseGson = gson.fromJson(JsonString, CourseGson.class);
        try {
            for (int i = 0; i < 10; i++) {
                if (courseGson.getMondayCourse() != null) {
                    if (courseGson.getMondayCourse().get(i) == null) {
                        courseGson.getMondayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setMondayCourse(addArrayList());
                }
                if (courseGson.getTuesdayCourse() != null) {
                    if (courseGson.getTuesdayCourse().get(i) == null) {
                        courseGson.getTuesdayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setTuesdayCourse(addArrayList());
                }
                if (courseGson.getWednesdayCourse() != null) {
                    if (courseGson.getWednesdayCourse().get(i) == null) {
                        courseGson.getWednesdayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setWednesdayCourse(addArrayList());
                }
                if (courseGson.getThursdayCourse() != null) {
                    if (courseGson.getThursdayCourse().get(i) == null) {
                        courseGson.getThursdayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setThursdayCourse(addArrayList());
                }
                if (courseGson.getFridayCourse() != null) {
                    if (courseGson.getFridayCourse().get(i) == null) {
                        courseGson.getFridayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setFridayCourse(addArrayList());
                }
                if (courseGson.getSaturdayCourse() != null) {
                    if (courseGson.getSaturdayCourse().get(i) == null) {
                        courseGson.getSaturdayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    courseGson.setSaturdayCourse(addArrayList());
                }
                if (courseGson.getSundayCourse() != null) {
                    if (courseGson.getSundayCourse().get(i) == null) {
                        courseGson.getSundayCourse().set(i, new CourseGson.DayCourse("", "", "", ""));
                    }
                } else {
                    ArrayList<CourseGson.DayCourse> arrayList = new ArrayList<CourseGson.DayCourse>();
                    for (int j = 0; j < 10; j++) {
                        arrayList.add(new CourseGson.DayCourse("", "", "", ""));
                    }
                    courseGson.setSundayCourse(addArrayList());
                }
            }
            JsonString = gson.toJson(courseGson);
            sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "getCourseArray()\n", Toast.LENGTH_LONG).show();
            exception();
        }
    }

    /*addJson()中间方法，填充一天的数据*/
    private ArrayList<CourseGson.DayCourse> addArrayList() {
        ArrayList<CourseGson.DayCourse> arrayList = new ArrayList<CourseGson.DayCourse>();
        for (int j = 0; j < 10; j++) {
            arrayList.add(new CourseGson.DayCourse("", "", "", ""));
        }
        return arrayList;
    }

    /*添加修改弹窗*/
    private void initPopupWindow() {
        /*PopWindow ->*/
        int _256dp = (int) getResources().getDimension(R.dimen._256dp);
        editName = new EditText(this);//CourseName输入框
        editName.setWidth(_256dp);
        editWeek = new EditText(this);//CourseWeek输入框
        editWeek.setWidth(_256dp);
        editPlace = new EditText(this);//CoursePlace输入框
        editPlace.setWidth(_256dp);
        editTeacher = new EditText(this);//CourseTeacher输入框
        editTeacher.setWidth(_256dp);
        popupButton = new Button(this);//弹窗按钮
        popupButton.setBackgroundColor(Color.parseColor("#676767"));
        popupButton.setWidth(_256dp);
        popupButton.setText("保  存");
        popupButton.setTextColor(Color.parseColor("#ffffff"));
        popupButton.setTextSize(getResources().getDimension(R.dimen._8dp));
        /*<- PopWindow*/
        scrollView = (ScrollView) findViewById(R.id.scrollView);//用以确定popupWindow位置
        /*弹窗LinearLayout布局 ->*/
        popLinearLayout = new LinearLayout(this);
        popLinearLayout.setOrientation(1);//设置LinearLayout纵向
        popLinearLayout.addView(editName);
        popLinearLayout.addView(editWeek);
        popLinearLayout.addView(editPlace);
        popLinearLayout.addView(editTeacher);
        popLinearLayout.addView(popupButton);
        popLinearLayout.setBackgroundColor(Color.parseColor("#ffffff"));//设置背景色
        /*<- 弹窗LinearLayout布局*/
        /*PopWindow ->*/
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(popLinearLayout);
        popupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        popupWindow.setWidth(_256dp);
        /*<- PopWindow*/
    }

    /*添加下拉刷新*/
    private void initRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorRefresh);//主题
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.getVerticalScrollbarPosition() == 0) {//判断页面是否在最上方
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setCancelable(true);
                    dialog.setTitle("说明：");
                    dialog.setMessage("* 编辑模式下长按可进行修改\n* 同名课程取首节课的课程信息");
                    dialog.setNegativeButton("刷新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            freeCourse();
                            JsonString = sharedPreferences.getString("PersonalCourseTable", null);
                            if (JsonString == null) {
                                JsonString = getLocalJson();
                            }
                            getCourseArray();
                            setCourseLength();
                            addLongCourse();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.setPositiveButton("编辑模式", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            freeCourse();
                            JsonString = sharedPreferences.getString("PersonalCourseTable", null);
                            if (JsonString == null) {
                                JsonString = getLocalJson();
                            }
                            getCourseArray();
                            addButton();
                            sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    dialog.show();
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }

            }
        });
    }

    /*设置课程长度*/
    private void setCourseLength() {
        try {
            for (int i = 0; i < 7; i++)//初始化课表长度
                for (int j = 0; j < 10; j++)
                    courseLength[i][j] = 1;
            for (int i = 0; i < 7; i++) {
                for (int j = 8; j >= 0; j--) {
                    if (!courseNameArray[i][j].equals("")) {//禁止比较空格
                        if (j >= 0 && j < 3 || j >= 4 && j < 7 || j >= 8 && j < 9) {//禁止跨时段比较
                            if (courseNameArray[i][j].equals(courseNameArray[i][j + 1])) {//连接同名课程
                                courseLength[i][j] = courseLength[i][j + 1] + 1;
                                courseLength[i][j + 1] = courseLength[i][j + 1] - courseLength[i][j + 1];
                                getCourseArray();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            exception();
        }
    }

    /*添加长按钮*/
    public void addLongCourse() {
        LinearLayout.LayoutParams longLayoutParams = null;
        int courseHeight;
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 10; j++) {
                courseHeight = courseLength[i][j] * _58dp + (courseLength[i][j] - 1) * _2dp;
                course[i][j] = new Course(this, courseWidth, courseHeight,
                        courseNameArray[i][j] + "\n" + coursePlaceArray[i][j] + "\n" + courseTeacherArray[i][j]);
                course[i][j].setPosition(i, j);//设置坐标
                /* 设置课程块大小及间距 */
                longLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, courseLength[i][j] * _58dp + (courseLength[i][j] - 1) * _2dp);
                longLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._2dp), 0, 0);//设置边距
                course[i][j].setLayoutParams(longLayoutParams);//设置大小
                if (courseLength[i][j] == 0) {
                    longLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen._0dp));
                    longLayoutParams.setMargins(_2dp, 0, 0, 0);//设置边距
                    course[i][j].setLayoutParams(longLayoutParams);
                }
                if (j == 4 || j == 8) {//设置分割线
                    longLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, courseLength[i][j] * _58dp + (courseLength[i][j] - 1) * _2dp);
                    longLayoutParams.setMargins(_2dp, (int) getResources().getDimension(R.dimen._12dp), 0, 0);
                    course[i][j].setLayoutParams(longLayoutParams);
                }
                if (courseNameArray[i][j].equals("")) {
                    course[i][j].setNull();
                }
                day[i].addView(course[i][j]);
            }
        }
    }

    /*释放Button空间，清空屏幕*/
    private void freeCourse() {
        course = null;
        System.gc();
        for (int j = 0; j < 7; j++) {
            day[j].removeAllViews();
        }
        course = new Course[7][10];
    }

    /* 异常处理 */
    private void exception() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("PersonalCourseTable");
//        editor.clear();
        editor.commit();
        JsonString = getLocalJson();
        sharedPreferences.edit().putString("PersonalCourseTable", JsonString).commit();
        Toast.makeText(getApplicationContext(), "数据异常，请重启应用", Toast.LENGTH_SHORT).show();
        finish();
    }
}
